package com.crimson.types;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonValue.ValueType;
import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonNumberTest {
  String input =
      "{\"key\": [1, 2.3, 3E4, 3.1E+4, 3.1289E04, 277162424654518927, 2.341E3, 18726262626352726E-09]}";

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void inputAsArrayOfNumbers_test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(input);
    Assert.assertTrue(null != jsonObject && jsonObject.size() == 1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(null != jsonArray && jsonArray.size() == 8);

    JsonNumber jsonNumber1 = jsonArray.getJsonNumber(0);
    Assert.assertTrue(jsonNumber1.intValue() == 1);

    JsonNumber jsonNumber2 = jsonArray.getJsonNumber(1);
    Assert.assertTrue(jsonNumber2.isIntegral() == false);

    JsonNumber jsonNumber3 = jsonArray.getJsonNumber(2);
    Assert.assertTrue(jsonNumber3.intValueExact() == 30000);

    JsonNumber jsonNumber4 = jsonArray.getJsonNumber(3);
    Assert.assertTrue(jsonNumber4.longValue() == 31_000L);

    JsonNumber jsonNumber5 = jsonArray.getJsonNumber(4);
    Assert.assertTrue(jsonNumber5.longValueExact() == 31289L);

    JsonNumber jsonNumber6 = jsonArray.getJsonNumber(5);
    Assert.assertTrue(
        jsonNumber6.bigIntegerValue().equals(BigInteger.valueOf(277_162_424_654_518_912L)));

    JsonNumber jsonNumber7 = jsonArray.getJsonNumber(6);
    Assert.assertTrue(jsonNumber7.bigIntegerValueExact().equals(BigInteger.valueOf(2_341)));

    JsonNumber jsonNumber8 = jsonArray.getJsonNumber(7);
    Assert.assertTrue(jsonNumber8.doubleValue() == 18726262.626352726);

    JsonNumber jsonNumber9 = jsonArray.getJsonNumber(7);
    Assert.assertTrue(jsonNumber9.bigDecimalValue().equals(BigDecimal.valueOf(18726262.626352726)));
  }

  @Test
  public void verifyCorrectValueType_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(input);
    Assert.assertTrue(null != jsonObject && jsonObject.size() == 1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(null != jsonArray && jsonArray.size() == 8);

    JsonNumber jsonNumber = jsonArray.getJsonNumber(7);
    Assert.assertTrue(jsonNumber.getValueType() == ValueType.NUMBER);
  }
}
