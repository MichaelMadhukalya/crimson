package com.crimson.converter;

import java.nio.CharBuffer;

public class JsonToCsvConverter {

  /**
   * Data directory details
   */
  static final String DATA_DIRECTORY = "/tmp/data";

  /**
   * Data file
   */
  static final String DATA_FILE_NAME = "data-json.json";

  /**
   * Chunk size (~65 MB)
   */
  static final int CHUNK_SIZE = 67_108_864;
  static final CharBuffer buffer = CharBuffer.allocate(CHUNK_SIZE);

  /**
   * State of JsonToCsvConverter
   */
  int pos = 0, end = 0;
  String fileName;

  public JsonToCsvConverter(String fileName) {
    this.fileName = fileName;
  }

  public void readlines() {
  }

  private String[] chomp() {
    return null;
  }
}
