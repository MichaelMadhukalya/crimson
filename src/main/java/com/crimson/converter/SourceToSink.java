package com.crimson.converter;

import com.crimson.converter.DataFrame.DataFrameBuilder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SourceToSink implements IStreamObserver<Object> {

  /**
   * Single threaded pool
   */
  final ExecutorService pool = Executors.newSingleThreadExecutor();
  Queue<Object> queue;
  /**
   * Source and sink files
   */
  String sourceFile;
  String sinkFile;

  /**
   * Max row limit for in memory persistence
   */
  int maximumRowLimit = 1_000;

  /**
   * Default encoding scheme
   */
  static final String DEFAULT_ENCODING_SCHEME = "utf-8";

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
    pool.submit(() -> doTask(DataFrameBuilder.newInstance()));

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
          BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(sinkFile)));

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
}
