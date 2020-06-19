package com.crimson.types;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonNumber extends JsonType<JsonNumber> implements javax.json.JsonNumber {
  BigDecimal number;

  private JsonNumber() {}

  public static final JsonNumber newInstance() {
    return new JsonNumber();
  }

  @Override
  public boolean isIntegral() {
    return number.scale() <= 0;
  }

  @Override
  public int intValue() {
    return number.intValue();
  }

  @Override
  public int intValueExact() {
    return number.intValueExact();
  }

  @Override
  public long longValue() {
    return number.longValue();
  }

  @Override
  public long longValueExact() {
    return number.longValueExact();
  }

  @Override
  public BigInteger bigIntegerValue() {
    return number.toBigInteger();
  }

  @Override
  public BigInteger bigIntegerValueExact() {
    return number.toBigIntegerExact();
  }

  @Override
  public double doubleValue() {
    return number.doubleValue();
  }

  @Override
  public BigDecimal bigDecimalValue() {
    return number;
  }

  @Override
  public ValueType getValueType() {
    return ValueType.NUMBER;
  }

  @Override
  public String toString() {
    return BigDecimal.valueOf(doubleValue()).toEngineeringString();
  }

  @Override
  public JsonNumber cast(Object value) {
    if (null == value) {
      throw new IllegalArgumentException("Can't construct valid JsonNumber from null object");
    }

    try {
      this.number = new BigDecimal(value.toString());
      super.value = this;
    } catch (NumberFormatException e) {
    }

    if (null != this.number) {
      return this;
    }

    throw new UnCastableObjectToInstanceTypeException(
        String.format("Can't find a decimal type for value = {%s}", value));
  }
}
