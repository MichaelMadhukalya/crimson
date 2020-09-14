package com.crimson.converter;

import static com.crimson.converter.DataFrame.DataFrameBuilder.newInstance;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
  private final Queue<Object> queue = new LinkedBlockingQueue<>();
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

  /**
   * Output stream for writing to the sink file
   */
  OutputStream outputStream = null;

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
    return writeToSink(sink, false);
  }

  public SourceToSink writeToSink(String sink, boolean shouldCreateFile) {
    try {
      sinkFile = sink;

      /* If file does not exist then default is to create file */
      File file = new File(sinkFile);
      if (!file.exists()) {
        if (shouldCreateFile) {
          file.createNewFile();
        } else {
          throw new IllegalStateException(String.format("Output file does not exist for transforming data"));
        }
      }

      outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return this;
  }

  public SourceToSink start() {
    /* Start polling queue */
    DataFrame frame = newInstance();
    sharedPool.submit(() -> doTask(frame));

    try {
      /* Initialize DataSource */
      DataSource<Object> dataSource = new DataSource<>(sourceFile);
      dataSource.start(this::stream);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }

    return this;
  }

  private void doTask(DataFrame frame) {
    try {
      Object object = queue.poll();

      /* If over row limit then flush to output stream before writing new rows */
      if (frame.getRowCount() >= maximumRowLimit) {

        String output = frame.toString();

        if (null != output && output.length() > 0) {
          byte[] bytes = output.getBytes(Charset.forName(DEFAULT_ENCODING_SCHEME));
          if (null != bytes && bytes.length > 0) {
            outputStream.write(bytes);
            outputStream.flush();
            frame.clear();
          }
        }
      }

      frame.addRow(object);
    } catch (Exception e) {
      throw new IllegalStateException(String.format("This task should not throw an exception. Task failed: {%s}", e));
    }

    doTask(frame);
  }

  public int getMaxRowLimit() { return maximumRowLimit; }

  public SourceToSink setMaximumRowLimit(int maxRowLimit) {
    maximumRowLimit = maxRowLimit;
    return this;
  }

  public SourceToSink setMaximumMB(int memoryMB) {
    if (memoryMB != 1 && memoryMB != 5 && memoryMB != 25 && memoryMB != 100) {
      throw new IllegalArgumentException("Maximum in memory size of DataFrame in MB can be either 1, 5, 25 or 100");
    }
    maximumMemoryMB = memoryMB;
    return this;
  }
}
