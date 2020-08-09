package com.crimson.converter;

import com.crimson.types.JsonObject;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class SourceToSinkTransformer implements IStreamObserver<JsonObject> {

  final Queue<JsonObject> objectQueue = new LinkedBlockingDeque<>();

  public SourceToSinkTransformer() {
  }

  @Override
  public void stream(JsonObject[] records) {
    Objects.nonNull(records);
    Arrays.stream(records).forEach(e -> objectQueue.add(e));
  }
}
