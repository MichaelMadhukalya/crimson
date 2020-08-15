package com.crimson.converter;

interface IMailboxProcessor<E> {

  void send(E message);

  void receive(E message);
}
