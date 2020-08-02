package com.crimson.converter;

import com.crimson.types.JsonType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

class Cell {
  /** Name */
  String name;

  /** Type */
  JsonType<?> jsonType;

  /** Value */
  Object value;

  Cell() {}

  Cell(String name, JsonType<?> jsonType, Object value) {
    this.name = name;
    this.jsonType = jsonType;
    this.value = value;
  }

  @Override
  public boolean equals(Object arg) {
    if (!(arg instanceof JsonType)) {
      return false;
    }

    Cell that = (Cell) arg;
    if (that == this) {
      return true;
    }

    return new EqualsBuilder()
        .append(name, that.name)
        .append(jsonType, that.jsonType)
        .append(value, that.value)
        .build();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new Cell(name, jsonType, value);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(new StringBuffer())
        .append(name)
        .append(jsonType.toString())
        .append(value)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).append(jsonType).append(value).build();
  }
}
