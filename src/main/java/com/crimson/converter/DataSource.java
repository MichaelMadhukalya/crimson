package com.crimson.converter;

import static com.crimson.converter.SourceToSink.sharedPool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class DataSource<E> {

  /**
   * Data directory details
   */
  private static final String DATA_DIRECTORY = "/tmp/data";

  /**
   * Data file
   */
  private static final String DATA_FILE_NAME = "data-json.json";

  /**
   * Read buffer size (~32 MB)
   */
  private static final int READ_BUFFER_SIZE = 33_554_432;
  private static final char[] buffer = new char[READ_BUFFER_SIZE];

  /**
   * Read chunk size
   */
  private static final int CHUNK_SIZE = 65_536;

  private static final String NEWLINE = System.lineSeparator();
  /**
   * Internal buffered reader
   */
  private final Reader reader;
  private final FileReader fileReader;

  /**
   * State of JsonToCsvConverter
   */
  private int offset = 0;
  private boolean started = false;
  private boolean stopped = false;
  private boolean err = false;

  DataSource() {
    this(DATA_DIRECTORY + "/" + DATA_FILE_NAME);
  }

  DataSource(String fileName) {
    try {
      fileReader = new FileReader(fileName);
      reader = new BufferedReader(fileReader, READ_BUFFER_SIZE);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public void start(Consumer<E[]> consumer) {
    Objects.requireNonNull(consumer);

    if (!started && !err) {
      started = true;
      process(consumer);
    } else if (err) {
      throw new IllegalStateException("DataSource state is in error. Can't re-initialize now");
    } else if (started) {
      if (stopped) {
        stopped = false;
        process(consumer);
      }
    }
  }

  private void process(Consumer<E[]> consumer) {
    while (!stopped) {
      try {
        boolean ready = isAvailable();
        if (!ready) {
          stopped = true;
          break;
        }

        int count = reader.read(buffer, offset, CHUNK_SIZE);
        if (count <= 0) {
          stopped = true;
          break;
        } else if (count <= CHUNK_SIZE) {
          Optional<String[]> opt = chomp();
          if (opt.isPresent()) {
            sharedPool.submit(() -> {
              List<E> objects = Arrays.stream(opt.get()).map(e -> (E) e).collect(Collectors.toList());
              consumer.accept((E[]) objects.toArray());
            });
          }
        }
      } catch (IOException e) {
        err = true;
        close();
        throw new IllegalStateException(e);
      }
    }
  }

  private boolean isAvailable() {
    while (true) {
      try {
        if (fileReader.ready()) {
          return true;
        }
        Thread.sleep(5_000);
      } catch (IOException | InterruptedException e) {
        return false;
      }
    }
  }

  private Optional<String[]> chomp() {
    String s = new String(buffer);
    int end = s.lastIndexOf(NEWLINE);
    if (end < 0) { throw new IllegalStateException("Unable to find line terminator char in chars read"); }
    offset += (end + 1);
    for (int i = 0; i < buffer.length; i++) { buffer[i] = ' '; }
    return Optional.ofNullable(s.substring(0, end + 1).split(NEWLINE));
  }

  private void close() {
    try {
      reader.close();
    } catch (IOException e) {
    }
  }

  public boolean isStarted() { return started; }

  public boolean isErr() { return err; }

  public boolean isStopped() { return stopped; }
}
