package com.crimson.converter;

import com.crimson.converter.DataFrame.DataFrameBuilder;
import com.crimson.types.JsonObject;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SourceToSink implements IStreamObserver<JsonObject> {

  /**
   * Queue for JsonObjects
   */
  Queue<JsonObject> queue;

  /**
   * Source and sink files
   */
  String sourceFile;
  String sinkFile;

  /**
   * Single threaded pool
   */
  final ExecutorService pool = Executors.newSingleThreadExecutor();

  public SourceToSink() {
  }

  @Override
  public void stream(JsonObject[] records) {
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

  public void create() {
    /* Initialize and poll queue for data */
    queue = new LinkedBlockingQueue<>();
    pool.submit(() -> doTask(DataFrameBuilder.newInstance()));

    /* Initialize DataSource */
    try {
      DataSource<JsonObject> dataSource = new DataSource<JsonObject>(sourceFile);
      dataSource.start(this::stream);
    } catch (Exception e) {
    }
  }

  private void doTask(DataFrame dataFrame) {
    while (true) {
      try {
        dataFrame.addRow(queue.poll());
      } catch (Exception e) {
        throw new IllegalStateException(String.format("This task should not throw an exception. Task failed: {%s}", e));
      }
    }
  }
}
