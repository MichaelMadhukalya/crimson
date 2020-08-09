package com.crimson.converter;

import com.crimson.types.JsonObject;

@FunctionalInterface
public interface IStreamObserver<E extends JsonObject> {

  void stream(E[] records);
}
