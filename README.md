# Crimson
## Introduction
**Crimson** is a simple, fast, light-weight, extensible JSON parser written in Java from *scratch*. It provides full **interface** compatibility with standard Java JSON APIs. Crimson is thread-safe; and inerops fully with Java data structures such as: *List*, *Map*, *String* etc. It uses *UTF-8* as the default encoding scheme while serializing raw bytes for persistent storage on disk.

## Design
Crimson uses a **recursive descent** strategy to parse inputs produced by lexical analyzer. A syntax checker validates input tokens in first pass looking for obvious issues such as: incorrect parenthesis match etc. On the other hand, semantic verification guards against issues such as key names not being in proper format e.g. *"key1"* as opposed to being *2E-05* or *null*. Finally, a recursive descent parser de-serializes input into one of the 7 data types supported in Crimson. We discuss more about the hierarchy of data types in the next section. 

## Data Type
The following table lists the 7 data types used in Crimson.

Name | Description
-----| -----------
**JsonType** | Recursive parameterized abstract super type of all concrete types.
**JsonNull** | Concrete type for representing a null value. It has no internal de-serialized type representation.
**JsonBoolean** | Concrete type for representing a boolean value. Internally represented as a Boolean.
**JsonString** | Concrete type for representing a string. Internally represented as a String.
**JsonNumber** | Concrete type for representing a decimal number. Internally represented as a Decimal Number.
**JsonArray** | Concrete type for representing a list. Internal deserialized representation is a List.
**JsonObject** | Concrete type for representing a map (key-value pair). Internal deserialized representation is a Map.

#### Why JsonType?
We got the idea for this while reading **Item 29**, *Chapter 5: Generics* of **Effective Java** (2/e). An abstract super type can be used to safely create type safe heterogenous containers where rather than parameterizing the entire container on a single key type individual rows/entries can be paramterized separetely. This is of particular importance in the **big data** world where often the same container is used to keep entries from different columns all of which may have separate types. Using Crimson we can create a type safe container as follows (courtsey JsonType):

```java
Map<JsonType<?>, Object> typeSafeMap = new HashMap<>();
```

Now, lets say we de-serialize an input into a JsonObject where the values corresponding to the three keys viz. "key1", "key2", and "key3" represent a JsonString, JsonArray and JsonObject. 

```java
String input = "{ \"key1\": null, \"key2\": \"test\", \"key3\": [1, 2, 3], \"key4\": {\"key5\": \"value5\"} }";
JsonObject jsonObject = JsonObject.newInstance();
jsonObject.cast(input);

JsonType<?> type = jsonObject.get("key1");
if (type instanceOf JsonString) {
  typeSafeMap.put(type, type.valueOf());
}

type = jsonObject.get("key2");
if (type instanceOf JsonArray) {
  typeSafeMap.put(type, type.valueOf());
}

type = jsonObject.get("key3");
if (type instanceOf JsonObject) {
  typeSafeMap.put(type, type.valueOf());
}
```



## Implementation 

## Testing

## Future work
