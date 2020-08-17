package com.crimson.converter;

import com.crimson.converter.DataFrame.DataFrameBuilder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SourceToSink implements IStreamObserver<Object> {

  /**
   * Queue of records parsed from source
   */
  private Queue<Object> queue;
  /**
   * Source and sink files
   */
  private String sourceFile;
  private String sinkFile;

  /**
   * Max row limit for in memory persistence
   */
  private int maximumRowLimit = 1_000_000;

  /**
   * Maximum in memory size of DataFrame before it is flushed to persistence store
   */
  private int maximumMemoryMB = 100;

  /**
   * Default encoding scheme
   */
  private static final String DEFAULT_ENCODING_SCHEME = "utf-8";

  /**
   * Shared pool.
   */
  static final ExecutorService sharedPool = Executors.newFixedThreadPool(2);

  public SourceToSink() {
  }

  @Override
  public void stream(Object[] records) {
    Arrays.stream(records).forEach(e -> queue.add(e));
  }

  public SourceToSink readFromSource(String source) {
    sourceFile = source;
    return this;
  }

  public SourceToSink writeToSink(String sink) {
    sinkFile = sink;
    return this;
  }

  public SourceToSink create() {
    /* Initialize and poll queue for data */
    queue = new LinkedBlockingQueue<>();
    sharedPool.submit(() -> doTask(DataFrameBuilder.newInstance()));

    try {
      /* Initialize DataSource */
      DataSource<Object> dataSource = new DataSource<>(sourceFile);
      dataSource.start(this::stream);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }

    return this;
  }

  private void doTask(DataFrame dataFrame) {
    while (true) {
      try {
        Object object = queue.poll();

        /* If over row limit then flush to output stream before writing new rows */
        if (dataFrame.rowCounter >= maximumRowLimit) {
          OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(sinkFile), true));

          String output = dataFrame.toString();

          if (null != output && output.length() > 0) {
            byte[] bytes = output.getBytes(Charset.forName(DEFAULT_ENCODING_SCHEME));
            if (null != bytes && bytes.length > 0) {
              outputStream.write(bytes);
              outputStream.flush();
              dataFrame.clear();
            }
          }
        }

        dataFrame.addRow(object);
      } catch (Exception e) {
        throw new IllegalStateException(String.format("This task should not throw an exception. Task failed: {%s}", e));
      }
    }
  }

  public int getMaxRowLimit() { return maximumRowLimit; }

  public SourceToSink setMaximumRowLimit(int maxRowLimit) {
    maximumRowLimit = maxRowLimit;
    return this;
  }

  public SourceToSink setMaximumMB(int memoryMB) {
    if (memoryMB != 1 || memoryMB != 5 || memoryMB != 25 || memoryMB != 100) {
      throw new IllegalArgumentException("Maximum in memory size of DataFrame in MB can be either 1, 5, 25 or 100");
    }
    maximumMemoryMB = memoryMB;
    return this;
  }
}
