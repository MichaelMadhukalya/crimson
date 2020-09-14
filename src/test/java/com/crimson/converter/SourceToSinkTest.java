package com.crimson.converter;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SourceToSinkTest {

  @Before
  public void before() {
  }

  @After
  public void after() {
    /* Delete file after test */
    File output = new File("/tmp/data/out.csv");
    if (output.exists()) {
      output.delete();
    }
  }

  @Test
  public void sourceToSkink_create_dataframe_test() {
    SourceToSink sourceToSink =
        new SourceToSink()
            .readFromSource("/tmp/data/data-json.json")
            .writeToSink("/tmp/data/out.csv", true)
            .setMaximumRowLimit(1)
            .setMaximumMB(25)
            .start();

  }
}
