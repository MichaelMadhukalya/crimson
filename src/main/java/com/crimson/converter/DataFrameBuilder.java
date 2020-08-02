package com.crimson.converter;

import com.crimson.types.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataFrameBuilder {

  /**
   * Samples directory details
   */
  static final String SAMPLE_DIRECTORY = "/tmp/data";

  /**
   * Sample file
   */
  static final String SAMPLE_FILE_NAME = "sample-json.json";

  /**
   * Max chunk size to be read from samples file
   */
  static final int CHUNK_SIZE = 1_048_576;
  /**
   * Buffer for content read from file
   */
  static final ByteBuffer buffer = ByteBuffer.allocate(CHUNK_SIZE);

  /**
   * DataFrame. This is built during static initialization
   */
  static DataFrame dataFrame = null;

  /**
   * Empty string
   */
  static final String EMPTY = StringUtils.EMPTY;

  public static DataFrame get() {
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
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
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
          .entrySet()
          .forEach(
              e -> {
                String pre = StringUtils.isEmpty(prefix) ? e.getKey() : prefix + "." + e.getKey();
                List<Cell> res = inferSchema(pre, (JsonType<?>) e.getValue(), supplier);
                result.addAll(res);
              });
    }

    return result;
  }
}
