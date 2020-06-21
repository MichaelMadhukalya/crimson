package com.crimson.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

public class JsonWriter implements IJsonWriter<JsonType> {
  /** Object separator */
  static final String LINE_SEPARATOR = String.valueOf('\n');

  @Override
  public void write(JsonType<JsonType> object, OutputStream out) throws IOException {
    write(object, out, Charset.defaultCharset());
  }

  @Override
  public void write(JsonType<JsonType> object, OutputStream out, Charset charset)
      throws IOException {
    String value = null;
    value = object.toString();
    out.write(value.getBytes(charset));
  }

  @Override
  public void write(JsonType<JsonType>[] objects, OutputStream out) throws IOException {
    byte[] lineSep = LINE_SEPARATOR.getBytes(Charset.defaultCharset());

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    Optional<IOException> ioException =
        Arrays.asList(objects).stream()
            .map(
                e -> {
                  IOException exception = null;
                  try {
                    byte[] data = e.toString().getBytes(Charset.defaultCharset());
                    byteArrayOutputStream.write(data);
                    byteArrayOutputStream.write(lineSep);
                  } catch (IOException ex) {
                    exception = ex;
                  }

                  return Optional.ofNullable(exception);
                })
            .reduce(
                (u, v) -> {
                  if (u.isPresent()) {
                    return u;
                  } else if (v.isPresent()) {
                    return v;
                  } else {
                    return Optional.empty();
                  }
                })
            .get();

    if (ioException.isPresent()) {
      throw new IOException(ioException.get());
    }

    out.write(byteArrayOutputStream.toByteArray());
    byteArrayOutputStream.flush();
    byteArrayOutputStream.close();
  }

  @Override
  public void close() throws IOException {}
}
