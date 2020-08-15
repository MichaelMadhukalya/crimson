package com.crimson.converter;

import static com.crimson.converter.DataFrame.DataFrameBuilder.newInstance;

import com.crimson.types.JsonObject;
import java.io.File;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DataFrameBuilderTest {

  String test = "{\"id\": \"u-234ef53901cdbc73ff59120bcccde3dc\", \"name\": \"John. S\", \"lisn\": 1, \"last_update_date\": 1596346582, \"dob\": \"Dec-15-1978\"}";

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
      DataFrame dataFrame = newInstance();
      Assert.assertNotNull(dataFrame);
      Assert.assertTrue(dataFrame.initialized());
    }
  }

  private boolean checkFileExists() {
    return new File("/tmp/data/sample-json.json").exists();
  }

  /**
   * This test is executed only when the sample-json.json file exists in the location inside data directory.
   */
  @Test
  public void verify_DataFrame_headers_and_rows_load_test() {
    if (checkFileExists()) {
      DataFrame dataFrame = newInstance();
      Assert.assertNotNull(dataFrame);
      Assert.assertTrue(dataFrame.initialized());
      JsonObject jsonObject = JsonObject.newInstance();
      jsonObject.cast(test);
      dataFrame.addRow(jsonObject);
    }
  }
}
