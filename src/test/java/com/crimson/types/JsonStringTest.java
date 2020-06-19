package com.crimson.types;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonStringTest {

  JsonString jsonString;

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void castValidStringAsInput_Test() {
    String test = "test";
    jsonString = JsonString.newInstance();
    jsonString.cast(test);
    Assert.assertTrue(jsonString.string.equals(test));
  }

  @Test(expected = IllegalArgumentException.class)
  public void castInValidStringAsInput_Test() {
    jsonString = JsonString.newInstance();
    jsonString.cast(null);
  }
}
