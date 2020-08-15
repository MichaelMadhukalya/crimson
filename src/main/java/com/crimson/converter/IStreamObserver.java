package com.crimson.converter;

@FunctionalInterface
public interface IStreamObserver<E> {

  void stream(E[] records);
}
