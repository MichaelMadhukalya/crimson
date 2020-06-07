package com.crimson.types;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class JsonWriter implements IJsonWriter<JsonType> {
    /**
     * Object separator
     */
    static final String LINE_SEPARATOR = String.valueOf('\n');
    /**
     * Logger
     */
    final Logger logger = Logger.getLogger(JsonWriter.class.getSimpleName());

    @Override
    public void write(JsonType<JsonType> object, OutputStream out) {
        write(object, out, Charset.defaultCharset());
    }

    @Override
    public void write(JsonType<JsonType> object, OutputStream out, Charset charset) {
        String value = null;
        try {
            value = object.toString();
            out.write(value.getBytes(charset));
        } catch (IOException e) {
            logger.error(String.format("Exception writing input: {%s} to output stream", value));
        }
    }

    @Override
    public void write(JsonType<JsonType>[] objects, OutputStream out) {
        byte[] lineSep = LINE_SEPARATOR.getBytes(Charset.defaultCharset());

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Arrays.asList(objects).stream().forEach(e -> {
                try {
                    byte[] data = e.toString().getBytes(Charset.defaultCharset());
                    byteArrayOutputStream.write(data);
                    byteArrayOutputStream.write(lineSep);
                } catch (IOException ex) {
                    logger.error(String.format("Exception writing JsonObject to byte array output stream: {%s}", ex));
                }
            });

            out.write(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            logger.error(String.format("Exception writing JsonObject to byte array output stream: {%s}", e));
        }
    }

    @Override
    public void close() throws IOException {
    }
}
