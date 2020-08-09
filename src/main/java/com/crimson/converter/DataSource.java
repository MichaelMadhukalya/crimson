package com.crimson.converter;

import com.crimson.types.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class DataSource<E extends JsonObject> implements Closeable, IMailboxProcessor<Message> {

  /**
   * Data directory details
   */
  static final String DATA_DIRECTORY = "/tmp/data";

  /**
   * Data file
   */
  static final String DATA_FILE_NAME = "data-json.json";

  /**
   * Chunk size (~32 MB)
   */
  static final int CHUNK_SIZE = 33_554_432;
  static final char[] buffer = new char[CHUNK_SIZE];

  /**
   * State of JsonToCsvConverter
   */
  int offset = 0;
  boolean started = false;
  boolean stopped = false;
  boolean err = false;

  /**
   * Internal buffered reader
   */
  final Reader reader;

  /**
   * Blocking queue where message arrives in FIFO order requesting DataSource to read from underlying stream
   */
  final Queue<Message> messages = new ArrayBlockingQueue<Message>(16);

  /**
   * Thread pool per DataSource
   */
  final ExecutorService service = Executors.newFixedThreadPool(2);

  DataSource() {
    this(DATA_DIRECTORY + "/" + DATA_FILE_NAME);
  }

  DataSource(String fileName) {
    try {
      reader = new BufferedReader(new FileReader(fileName) {});
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public void start(Consumer<E[]> consumer) {
    Objects.requireNonNull(consumer);

    if (!started && !err) {
      service.submit(() -> process(consumer));
      started = true;
    } else if (err) {
      throw new IllegalStateException("DataSource state is in error. Can't re-initialize now");
    } else if (started) {
      if (stopped) {
        stopped = false;
        service.submit(() -> process(consumer));
      }
    }
  }

  private void process(Consumer<E[]> consumer) {
    while (!stopped) {
      try {
        int count = reader.read(buffer, offset, CHUNK_SIZE);
        if (count <= 0) {
          break;
        } else if (count <= CHUNK_SIZE) {
          service.submit(() -> stream(consumer));
          receive(messages.poll());
        }
      } catch (IOException e) {
        err = true;
        throw new IllegalStateException(e);
      }
    }
  }

  @Override
  public void send(Message message) {
    messages.add(message);
  }

  @Override
  public void receive(Message message) {
    if (null == message || null == message.id) {
      stopped = true;
    }
  }

  private void stream(Consumer<E[]> consumer) {
    Optional<String[]> opt = chomp();
    if (opt.isPresent()) {
      List<JsonObject> jsonObjects = Arrays.stream(opt.get()).map(e -> {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(e);
        return jsonObject;
      }).filter(e -> null != e.valueOf()).collect(Collectors.toList());
      consumer.accept((E[]) jsonObjects.toArray());
    } else {
      throw new IllegalStateException();
    }
  }

  private Optional<String[]> chomp() {
    String s = new String(buffer);
    int end = s.lastIndexOf("\n");
    if (end < 0) { throw new IllegalStateException("Unable to find line terminator char in chars read"); }
    offset += (end + 1);
    return Optional.of(s.substring(0, end + 1).split("\n"));
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

}
