package com.crimson.types;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class JsonBooleanTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void castValidInput_Test() {
        JsonBoolean jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast(JsonValue.TRUE);
        Assert.assertTrue(jsonBoolean.booleanValue == Boolean.TRUE);
    }

    @Test
    public void castInValidStringAsInput_Test() {
        JsonBoolean jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("none");
        Assert.assertTrue(jsonBoolean.booleanValue == Boolean.FALSE);
    }

    @Test
    public void castValidStringAsInput_Test() {
        JsonBoolean jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("False");
        Assert.assertTrue(jsonBoolean.booleanValue == Boolean.FALSE);
    }

    @Test
    public void castValidStringAndGetIntegerValue_Test() {
        JsonBoolean jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("TRUE");
        Assert.assertTrue(jsonBoolean.getInt() == 1);
    }

    @Test
    public void castValidStringAndCheckValueType_Test() {
        JsonBoolean jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("FALSE");
        Assert.assertTrue(jsonBoolean.getValueType() == ValueType.FALSE);
    }
}