package com.crimson.converter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DataFrameBuilderTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void verify_DataFrame_headers_load_test() {
    DataFrame dataFrame = DataFrameBuilder.get();
    Assert.assertNotNull(dataFrame);
    Assert.assertTrue(dataFrame.initialized());
  }
}
