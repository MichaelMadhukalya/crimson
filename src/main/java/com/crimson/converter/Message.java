package com.crimson.converter;

class Message {

  final String id;

  Message() {
    this(null);
  }

  Message(String uuid) {
    this.id = uuid;
  }
}
