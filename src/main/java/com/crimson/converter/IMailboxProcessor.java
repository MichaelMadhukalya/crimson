package com.crimson.converter;

interface IMailboxProcessor<M> {

  void send(M message);

  void receive(M message);
}
