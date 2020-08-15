package com.crimson.types;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonArrayTest {

  String INPUT_1 =
      "{\"key\": [1, 3.14, 5E-03, 6.1E2, null, \"null\", false, [1, 43.238E+02, {\"key1\": \"value1\"}]]}";

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void mixedArrayValuesAsInput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);
    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);
  }

  @Test
  public void compareValuesFromMixedArray_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);

    JsonNumber number1 = jsonArray.getJsonNumber(0);
    Assert.assertTrue(number1.intValue() == 1);

    JsonNumber number2 = jsonArray.getJsonNumber(1);
    Assert.assertTrue(number2.intValue() == 3);

    JsonNumber number3 = jsonArray.getJsonNumber(2);
    Assert.assertTrue(number3.toString().equals("0.005"));

    JsonNumber number4 = jsonArray.getJsonNumber(3);
    Assert.assertTrue(number4.toString().equals("610.0"));
  }

  @Test
  public void verifyNullValueAsInput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);

    JsonType<?> valueType = (JsonType<?>) jsonArray.get(4);
    Assert.assertTrue(valueType instanceof JsonNull);
  }

  @Test
  public void verifyStringValueAsInput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);

    JsonType<?> valueType = (JsonType<?>) jsonArray.get(5);
    Assert.assertTrue(valueType instanceof JsonString);
    Assert.assertTrue(valueType.toString().equals("\"null\""));

    javax.json.JsonString s1 = jsonArray.getJsonString(5);
    Assert.assertTrue(s1.toString().equals("\"null\""));
  }

  @Test
  public void verifyBooleanValueAsInput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);

    JsonType<?> valueType = (JsonType<?>) jsonArray.get(6);
    Assert.assertTrue(valueType instanceof JsonBoolean);
    Assert.assertTrue(((JsonBoolean) valueType).getInt() == 0);
  }

  @Test
  public void verifyNestedArrayValueAsInput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray1 = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray1.size() == 8);

    JsonArray jsonArray2 = jsonArray1.getJsonArray(7);
    Assert.assertTrue(null != jsonArray2 && jsonArray2.size() == 3);

    JsonNumber number1 = jsonArray2.getJsonNumber(0);
    Assert.assertTrue(number1.intValue() == 1);

    JsonNumber number2 = jsonArray2.getJsonNumber(1);
    Assert.assertTrue(number2.toString().equals("4323.8"));

    javax.json.JsonObject jsonObject1 = jsonArray2.getJsonObject(2);
    Assert.assertTrue(jsonObject1.size() == 1 && jsonObject1.containsKey("key1"));

    javax.json.JsonString s = jsonObject1.getJsonString("key1");
    Assert.assertTrue(s.toString().equals("\"value1\""));
  }

  @Test
  public void verifyIntValueAsOutput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);
    Assert.assertTrue(jsonArray.getInt(0) == 1);
  }

  @Test
  public void verifyBooleanValueAsOutput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);
    Assert.assertTrue(jsonArray.getBoolean(6) == false);
  }

  @Test
  public void verifyStringValueAsOutput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);
    Assert.assertTrue(jsonArray.getString(5).equals("\"null\""));
  }

  @Test
  public void verifyNullValueAsOutput_Test() {
    JsonObject jsonObject = JsonObject.newInstance();
    jsonObject.cast(INPUT_1);

    JsonArray jsonArray = jsonObject.getJsonArray("key");
    Assert.assertTrue(jsonArray.size() == 8);
    Assert.assertTrue(jsonArray.isNull(4) == true);
  }
}
