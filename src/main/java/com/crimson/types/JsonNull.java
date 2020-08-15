package com.crimson.types;

import javax.json.JsonValue;

public class JsonNull extends JsonType<JsonNull> implements JsonValue {

  static final JsonValue NULL_VALUE = JsonValue.NULL;

  private JsonNull() {}

  public static final JsonNull newInstance() {
    return new JsonNull();
  }

  @Override
  public ValueType getValueType() {
    return ValueType.NULL;
  }

  @Override
  public String toString() {
    return "null";
  }

  @Override
  public JsonNull cast(Object value) {
    if (NULL_VALUE.equals(value)) {
      super.value = this;
      return this;
    }

    throw new UnCastableObjectToInstanceTypeException(
        String.format("Can't convert non null object to JsonNull type"));
  }
}
