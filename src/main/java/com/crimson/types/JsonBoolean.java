package com.crimson.types;

import javax.json.JsonValue;

public class JsonBoolean extends JsonType<JsonBoolean> implements JsonValue {
  static final JsonValue TRUE_VALUE = JsonValue.TRUE;
  static final JsonValue FALSE_VALUE = JsonValue.FALSE;
  Boolean booleanValue;

  private JsonBoolean() {
    this.booleanValue = null;
  }

  public static final JsonBoolean newInstance() {
    return new JsonBoolean();
  }

  public boolean getBoolean() {
    return booleanValue;
  }

  public int getInt() {
    if (booleanValue == Boolean.TRUE) {
      return 1;
    } else if (booleanValue == Boolean.FALSE) {
      return 0;
    }

    throw new IllegalStateException(
        String.format("Uninitialized JsonBoolean type does not have boolean value"));
  }

  public String getString() {
    if (booleanValue == Boolean.TRUE) {
      return Boolean.TRUE.toString();
    } else if (booleanValue == Boolean.FALSE) {
      return Boolean.FALSE.toString();
    }

    throw new IllegalStateException(
        String.format("Uninitialized JsonBoolean type not associated with boolean value"));
  }

  public JsonNumber getJsonNumber() {
    if (booleanValue == Boolean.TRUE) {
      return JsonNumber.newInstance().cast((Object) String.valueOf(1));
    } else if (booleanValue == Boolean.FALSE) {
      return JsonNumber.newInstance().cast((Object) String.valueOf(0));
    }

    throw new IllegalStateException(
        String.format("Uninitialized JsonBoolean type not associated with boolean value"));
  }

  @Override
  public ValueType getValueType() {
    if (booleanValue == Boolean.TRUE) {
      return ValueType.TRUE;
    } else if (booleanValue == Boolean.FALSE) {
      return ValueType.FALSE;
    }

    throw new IllegalStateException(
        String.format("Uninitialized JsonBoolean type not associated with boolean value"));
  }

  @Override
  public String toString() {
    return booleanValue.toString();
  }

  @Override
  public JsonBoolean cast(Object value) {
    if (null == value) {
      throw new IllegalArgumentException("Can't construct valid JsonBoolean from null object");
    }

    if (value.equals(TRUE_VALUE)) {
      booleanValue = Boolean.TRUE;
    } else if (value.equals(FALSE_VALUE)) {
      booleanValue = Boolean.FALSE;
    }

    try {
      booleanValue = Boolean.parseBoolean(String.valueOf(value));
      super.value = this;
    } catch (Exception e) {
      throw new UnCastableObjectToInstanceTypeException(
          String.format("Unable to get valid boolean value from input"));
    }

    return this;
  }
}
