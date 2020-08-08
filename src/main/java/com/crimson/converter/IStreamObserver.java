package com.crimson.converter;

@FunctionalInterface
public interface IStreamObserver<T> {

  void stream(T[] records);
}
