package com.crimson.types;

import com.crimson.types.JsonType.UnCastableObjectToInstanceTypeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonValue;

public class JsonNullTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void castJsonNullObjectAsInput_Test() {
    JsonNull jsonNull = JsonNull.newInstance();
    jsonNull.cast(JsonValue.NULL);
    Assert.assertTrue(1 == 1);
  }

  @Test(expected = UnCastableObjectToInstanceTypeException.class)
  public void castNonNullObjectAsINputExpectedException_Test() {
    JsonNull jsonNull = JsonNull.newInstance();
    jsonNull.cast("test");
  }

  @Test(expected = UnCastableObjectToInstanceTypeException.class)
  public void castNullObjectAsInput_Test() {
    JsonNull jsonNull = JsonNull.newInstance();
    jsonNull.cast(null);
  }
}
