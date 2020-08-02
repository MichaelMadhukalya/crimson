package com.crimson.converter;

import com.crimson.types.JsonType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DataFrame {

  /**
   * Containers for DataFrame
   */
  final Map<String, List<Cell>> frame = new LinkedHashMap<>();
  final Set<String> keys = new LinkedHashSet<>();

  /**
   * Max cols and rows for DataFrame
   */
  static final int MAX_NUMBER_COLS = 65_536;
  static final int MAX_NUMBER_ROWS = 1_000_000;

  /**
   * DataFrame counters
   */
  int rowCounter = 0, colCounter = 0;

  DataFrame() {
  }

  public <T extends JsonType> void addHeader(List<Cell> header) {
    header.stream()
        .forEach(
            e -> {
              frame.put(e.name, new ArrayList<>());
              keys.add(e.name);
              ++colCounter;
              if (colCounter >= MAX_NUMBER_COLS) {
                throw new IllegalStateException(
                    String.format("DataFrame exceeded max. column size : %d", colCounter));
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
                throw new IllegalStateException(
                    String.format("DataFrame exceeded max. row size : %d", rowCounter));
              }
            });

    frame.values().stream()
        .forEach(
            e -> {
              if (e.size() < rowCounter) {
                e.add(null);
              }
            });
  }

  public void clear() {
    frame.entrySet().stream().forEach(e -> e.getValue().clear());
  }

  public boolean initialized() {
    return frame.size() == keys.size() && keys.size() > 0;
  }
}
