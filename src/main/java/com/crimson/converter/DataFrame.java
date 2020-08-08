package com.crimson.converter;

import com.crimson.types.JsonNull;
import com.crimson.types.JsonType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataFrame {

  /**
   * Containers for DataFrame
   */
  final Map<String, List<Cell>> frame = new LinkedHashMap<>();
  final Set<String> keys = new HashSet<>();

  /**
   * Max cols and rows for DataFrame
   */
  static final int MAX_NUMBER_COLS = 65_536;
  static final int MAX_NUMBER_ROWS = 1_000_000;

  /**
   * DataFrame counters
   */
  int rowCounter = 0, colCounter = 0;

  /**
   * Separators
   */
  static final String COLUMN_SEPARATOR = ",";
  static final String ROW_SEPARATOR = "\n";

  DataFrame() {
  }

  private DataFrame(DataFrame dataFrame) {
    dataFrame.frame.entrySet().stream().forEach(e -> frame.put(e.getKey(), new ArrayList<>()));
    dataFrame.keys.stream().forEach(e -> keys.add(e));
  }

  public <T extends JsonType> void addHeader(List<Cell> header) {
    if (initialized()) {
      return;
    }

    header.stream()
        .forEach(
            e -> {
              frame.put(e.name, new ArrayList<>());
              keys.add(e.name);
              ++colCounter;
              if (colCounter >= MAX_NUMBER_COLS) {
                throw new IllegalStateException(String.format("DataFrame exceeded max. column size : %d", colCounter));
              }
            });
  }

  public <T extends JsonType> void addRow(List<Cell> row) {
    row.stream()
        .forEach(
            e -> {
              List<Cell> curr = frame.getOrDefault(e.name, new ArrayList<>());
              curr.add(e);
              ++rowCounter;
              if (rowCounter >= MAX_NUMBER_ROWS) {
                throw new IllegalStateException(String.format("DataFrame exceeded max. row size : %d", rowCounter));
              }
            });

    frame.values().stream()
        .forEach(
            e -> {
              if (e.size() < rowCounter) {
                JsonType<?> jsonType = JsonNull.newInstance();
                Object value = jsonType.toString();
                e.add(new Cell("", jsonType, value));
              }
            });
  }

  public void clear() {
    frame.entrySet().stream().forEach(e -> e.getValue().clear());
  }

  public boolean initialized() {
    return frame.size() == keys.size() && keys.size() > 0;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    frame.keySet().stream().forEach(e -> buffer.append(e).append(COLUMN_SEPARATOR));
    buffer.append(ROW_SEPARATOR);

    for (int i = 0; i < rowCounter; i++) {
      final int idx = i;
      frame.values().stream().forEach(e -> buffer.append(e.get(idx)).append(COLUMN_SEPARATOR));
      buffer.append(ROW_SEPARATOR);
    }

    return buffer.toString();
  }
}
