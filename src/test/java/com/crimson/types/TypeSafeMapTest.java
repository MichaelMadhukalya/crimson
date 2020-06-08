package com.crimson.types;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TypeSafeMapTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void typeSafeMapChecker_Test() {
        String input = "{ \"key1\": \"test\", \"key2\": [1, 2, 3], \"key3\": {\"key4\": \"value4\"} }";
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(input);

        Map<JsonType<?>, Object> typeSafeMap = new HashMap<>();

        JsonType<?> stringType = (JsonType<?>) jsonObject.get("key1");
        if (stringType instanceof JsonString) {
            typeSafePut(typeSafeMap, JsonString.newInstance(), stringType.valueOf());
        }

        JsonType<?> arrayType = (JsonType<?>) jsonObject.get("key2");
        if (arrayType instanceof JsonArray) {
            typeSafePut(typeSafeMap, JsonArray.newInstance(), arrayType.valueOf());
        }

        JsonType<?> objectType = (JsonType<?>) jsonObject.get("key3");
        if (objectType instanceof JsonObject) {
            typeSafePut(typeSafeMap, JsonObject.newInstance(), objectType.valueOf());
        }

        Assert.assertTrue(typeSafeMap.size() == 3);
    }

    void typeSafePut(Map<JsonType<?>, Object> typeSafeMap, JsonType<?> type, Object value) {
        try {
            typeSafeMap.put(type, type.cast(value));
        } catch (JsonType.UnCastableObjectToInstanceTypeException e) {
            throw e;
        }
    }

    Object typeSafeGet(Map<JsonType<?>, Object> typeSafeMap, JsonType<?> type) {
        return typeSafeMap.get(type);
    }

    @Test(expected = Exception.class)
    public void typeSafeMapFail_Test() {
        String input = "{ \"key1\": \"test\", \"key2\": [1, 2, 3], \"key3\": {\"key4\": \"value4\"} }";
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(input);

        Map<JsonType<?>, Object> typeSafeMap = new HashMap<>();

        JsonType<?> stringType = (JsonType<?>) jsonObject.get("key1");
        if (stringType instanceof JsonString) {
            typeSafePut(typeSafeMap, JsonString.newInstance(), stringType.valueOf());
        }

        JsonType<?> arrayType = (JsonType<?>) jsonObject.get("key2");
        if (arrayType instanceof JsonArray) {
            typeSafePut(typeSafeMap, JsonObject.newInstance(), arrayType.valueOf());
        }

        JsonType<?> objectType = (JsonType<?>) jsonObject.get("key3");
        if (objectType instanceof JsonObject) {
            typeSafePut(typeSafeMap, JsonArray.newInstance(), objectType.valueOf());
        }
    }

}
