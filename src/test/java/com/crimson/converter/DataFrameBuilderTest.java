package com.crimson.converter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DataFrameBuilderTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * This test is executed only when the sample-json.json file exists in the location inside data directory.
   */
  @Test
  public void verify_DataFrame_headers_load_test() {
    if (checkFileExists()) {
      DataFrame dataFrame = DataFrameBuilder.get();
      Assert.assertNotNull(dataFrame);
      Assert.assertTrue(dataFrame.initialized());
    }
  }

  private boolean checkFileExists() {
    return new File("/tmp/data/sample-json.json").exists();
  }
}
