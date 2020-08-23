package com.crimson.converter;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.crimson.types.JsonArray;
import com.crimson.types.JsonBoolean;
import com.crimson.types.JsonNull;
import com.crimson.types.JsonNumber;
import com.crimson.types.JsonObject;
import com.crimson.types.JsonString;
import com.crimson.types.JsonType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class DataFrame {

  /**
   * Max cols and rows for DataFrame
   */
  private static final int MAX_NUMBER_COLS = 65_536;
  private static final int MAX_NUMBER_ROWS = 1_000_000;
  /**
   * Separators
   */
  private static final String COLUMN_SEPARATOR = ",";
  private static final String ROW_SEPARATOR = System.lineSeparator();
  /**
   * Containers for DataFrame
   */
  private final Map<String, List<Cell>> frame = new LinkedHashMap<>();
  private final Set<String> keys = new HashSet<>();
  /**
   * DataFrame counters
   */
  private int rowCount = 0, colCount = 0;

  private DataFrame() {
  }

  private DataFrame(DataFrame dataFrame) {
    dataFrame.frame.entrySet().stream().forEach(e -> frame.put(e.getKey(), new ArrayList<>()));
    dataFrame.keys.stream().forEach(e -> keys.add(e));
  }

  public void addHeader(List<Cell> header) {
    if (initialized()) {
      return;
    }

    header.stream().forEach(e -> {
      frame.put(e.name, new ArrayList<>());
      keys.add(e.name);
      ++colCount;
      if (colCount >= MAX_NUMBER_COLS) {
        throw new IllegalStateException(String.format("DataFrame exceeded max. column size : %d", colCount));
      }
    });
  }

  public void addRow(Object object) {
    Objects.requireNonNull(object);
    JsonObject jsonObject = JsonObject.newInstance().cast(object);
    List<Cell> cells = DataFrameBuilder.inferSchema(EMPTY, jsonObject, null);
    addRow(cells);
  }

  public void addRow(String input) {
    Objects.requireNonNull(input);
    JsonObject jsonObject = JsonObject.newInstance().cast(input);
    List<Cell> cells = DataFrameBuilder.inferSchema(EMPTY, jsonObject, null);
    addRow(cells);
  }

  public void addRow(JsonType<?> jsonType) {
    Objects.requireNonNull(jsonType);
    if (!(jsonType instanceof JsonObject)) {
      throw new IllegalArgumentException("JsonType object not an instance of JsonObject type");
    }

    JsonObject jsonObject = (JsonObject) jsonType;
    List<Cell> cells = DataFrameBuilder.inferSchema(EMPTY, jsonType, null);
    addRow(cells);
  }

  private void addRow(List<Cell> row) {
    row.stream().forEach(e -> {
      if (rowCount > MAX_NUMBER_ROWS) {
        throw new IllegalStateException(String.format("DataFrame exceeded max. row size : %d", rowCount));
      }
      List<Cell> lst = frame.get(e.name);
      lst.add(e);
    });
    ++rowCount;

    frame.values().stream().forEach(e -> {
      if (e.size() < rowCount) {
        JsonType<?> jsonType = JsonNull.newInstance();
        Object value = jsonType.toString();
        e.add(new Cell(EMPTY, jsonType, value));
      }
    });
  }

  public void clear() {
    frame.entrySet().stream().forEach(e -> e.getValue().clear());
  }

  public boolean initialized() {
    return frame.size() == keys.size() && keys.size() > 0;
  }

  public int getRowCount() { return rowCount; }

  public int getColCount() { return colCount; }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    /* Add headers */
    frame.keySet().stream().forEach(e -> buffer.append(e).append(COLUMN_SEPARATOR));
    buffer.append(ROW_SEPARATOR);

    /* Add rows */
    for (int i = 0; i < rowCount; i++) {
      final int idx = i;
      frame.values().stream().forEach(e -> {
        Cell cell = e.get(idx);
        if (cell == null) {
          buffer.append(EMPTY);
        } else {
          buffer.append(cell.value).append(COLUMN_SEPARATOR);
        }
      });
      buffer.append(ROW_SEPARATOR);
    }

    return buffer.toString();
  }

  static class DataFrameBuilder {

    /**
     * Samples directory details
     */
    private static final String SAMPLE_DIRECTORY = "/tmp/data";

    /**
     * Sample file
     */
    private static final String SAMPLE_FILE_NAME = "sample-json.json";

    /**
     * Max chunk size to be read from samples file
     */
    private static final int CHUNK_SIZE = 1_048_576;
    /**
     * Buffer for content read from file
     */
    private static final ByteBuffer buffer = ByteBuffer.allocate(CHUNK_SIZE);

    /**
     * DataFrame. This is built during static initialization
     */
    private static DataFrame dataFrame = null;

    static {
      newInstance();
    }

    public static DataFrame newInstance() {
      /* Load data frame headers if not loaded already */
      if (null == dataFrame) {
        build();
      }
      return dataFrame;
    }

    private static DataFrame build() {
      String fileName = SAMPLE_DIRECTORY + "/" + SAMPLE_FILE_NAME;
      try (FileInputStream fileInputStream = new FileInputStream(new File(fileName))) {
        FileChannel fileChannel = fileInputStream.getChannel();
        fileChannel.read(buffer);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }

      try {
        String content = new String(buffer.array(), Charset.defaultCharset());
        int idx = content.indexOf('\n');
        if (idx >= 0) {
          content = content.substring(0, idx);
        }
        JsonObject jsonObject = JsonObject.newInstance().cast(content);
        List<Cell> cells = inferSchema(EMPTY, jsonObject, DataFrame::new);
        dataFrame.addHeader(cells);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }

      return dataFrame;
    }

    private static List<Cell> inferSchema(String prefix, JsonType<?> jsonType, Supplier<DataFrame> supplier) {
      if (null == dataFrame) {
        dataFrame = supplier.get();
      }

      List<Cell> result = new ArrayList<>();
      if (jsonType instanceof JsonNull || jsonType instanceof JsonBoolean || jsonType instanceof JsonNumber
          || jsonType instanceof JsonString || jsonType instanceof JsonArray) {
        Cell cell = new Cell(prefix, jsonType, jsonType.toString());
        result.add(cell);
      } else {
        JsonObject jsonObject = (JsonObject) jsonType;
        jsonObject
            .forEach((key, value) -> {
              String pre = isEmpty(prefix) ? key : prefix + "." + key;
              List<Cell> res = inferSchema(pre, (JsonType<?>) value, supplier);
              result.addAll(res);
            });
      }

      return result;
    }
  }
}
