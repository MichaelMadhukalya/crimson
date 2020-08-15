package com.crimson.types;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonMapperTest {

  String input =
      "{  \"key1\": \"value1\","
          + "\"key2\": null,"
          + "\"key3\": true,"
          + "\"key4\": 2.34E+02,"
          + "\"key5\" : [1, 2, 3.14159], "
          + "\"key6\": {\"key7\": \"value7\", \"key8\": [0, null, false]}"
          + "}";

  String arr =
      "[1, 2.0, 3.14159, [23, 3E+4, 0.34], {\"key1\": \"value1\"}, [11, {\"key2\": [11, 2]}, 13, 9]]";

  @Before
  public void before() {}

  @After
  public void after() {}

  @Test
  public void stringToJsonObject_test() {
    JsonMapper mapper = new JsonMapper();
    JsonObject jsonObject = mapper.toJsonObject(input);
    Assert.assertTrue(null != jsonObject && jsonObject.size() == 6);
  }

  @Test
  public void stringToJsonArray_test() {
    JsonMapper mapper = new JsonMapper();
    JsonArray jsonArray = mapper.toJsonArray(arr);
    Assert.assertTrue(null != jsonArray && jsonArray.size() == 6);
  }

  @Test
  public void jsonObjectToMap_test() {
    JsonMapper mapper = new JsonMapper();
    JsonObject jsonObject = mapper.toJsonObject(input);
    Assert.assertTrue(null != jsonObject && jsonObject.size() == 6);
    Map<Object, Object> map = mapper.toMap(jsonObject);
    Assert.assertTrue(null != map && map.size() == 6);
  }

  @Test
  public void stringToMap_test() {
    JsonMapper mapper = new JsonMapper();
    Map<Object, Object> map = mapper.toMap(input);
    Assert.assertTrue(null != map && map.size() == 6);
  }

  @Test
  public void jsonObjectToString_test() {
    JsonMapper mapper = new JsonMapper();
    JsonObject jsonObject = mapper.toJsonObject(input);
    Assert.assertTrue(
        null != jsonObject
            && jsonObject.size() == 6
            && mapper.toString(jsonObject).contains("null"));
  }

  @Test
  public void stringToList_test() {
    JsonMapper mapper = new JsonMapper();
    JsonArray jsonArray = JsonArray.newInstance();
    jsonArray.cast(arr);
    List<Object> list = mapper.toList(arr);
    Assert.assertTrue(null != list && list.size() == 6);
  }

  @Test
  public void jsonArrayToString_Test() {
    JsonMapper mapper = new JsonMapper();
    JsonArray jsonArray = JsonArray.newInstance();
    jsonArray.cast(arr);
    String res = mapper.toString(jsonArray);
    Assert.assertTrue(null != res && res.contains("30000.0"));
  }
}
