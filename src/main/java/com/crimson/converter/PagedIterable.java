package com.crimson.converter;

import com.crimson.types.JsonObject;
import java.util.Iterator;
import java.util.function.Consumer;

class PagedIterable<T extends JsonObject> implements Iterator {

  static final int MAX_OBJECTS_PER_PAGE = 5_000;
  static final int MAX_PAGES = 20;

  final Object[][] PAGES = new Object[MAX_PAGES][];
  int pos = 0, end = 0;

  Consumer<T[]> consumer;

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public T[] next() {
    return null;
  }
}
