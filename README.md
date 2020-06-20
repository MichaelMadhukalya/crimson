# Crimson
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) ![Java CI with Maven](https://github.com/MichaelMadhukalya/crimson/workflows/Java%20CI%20with%20Maven/badge.svg)

## Introduction
**Crimson** is a simple, fast, light weight, thread safe, extensible JSON parser written in Java. It provides full **interface** compatibility with standard Java JSON APIs. Crimson interops fully with standard Java data structures such as: *List*, *Map*, *String* etc. It uses *UTF-8* as the default encoding scheme while serializing raw bytes to persistent storage on disk.

## Design
Crimson uses a **recursive descent** strategy to parse inputs produced by lexical analyzer. A syntax checker validates input tokens in first pass looking for obvious issues such as: incorrect parenthesis match etc. On the other hand, semantic verification guards against issues such as key names not being in proper format e.g. *"key1"* as opposed to being *2E-05* or *null*. Finally, a recursive descent parser de-serializes input into one of the **7** data types supported by Crimson. Please see below for a discussion about the hierarchy of data types used in Crimson. 

## Data Type
The following table lists the **7** data types used in Crimson.

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
This idea originates from **Item 29**, *Chapter 5: Generics* of **Effective Java** (2/e). An abstract super type can be used to safely create type safe a heterogenous container where rather than parameterizing the entire container on a single key type individual rows/entries can be parameterized separately. This is of importance in the **big data** world where often the same container is used to keep entries from different columns all of which may have separate types. Using Crimson we can create a type safe container as follows:

First, we declare a map with its key being of type ```JsonType``` and value being of type ```Object```.

```java
Map<JsonType<?>, Object> typeSafeMap = new HashMap<>();
```

Next, we de-serialize an input into a JsonObject where the values corresponding to the three keys viz. "key1", "key2", and "key3" represent a ```JsonString```, ```JsonArray``` and ```JsonObject``` as follows: 

```java
String input = "{ \"key1\": \"test\", \"key2\": [1, 2, 3], \"key3\": {\"key4\": \"value4\"} }";
JsonObject jsonObject = JsonObject.newInstance();
jsonObject.cast(input);
```

Finally, we insert the three items via type safe put operations into the container. An assertion check indicates that three objects were added to the container.

```java
JsonType<?> stringType = (JsonType<?>) jsonObject.get("key1");
if (stringType instanceof JsonString) {
    typeSafePut(typeSafeMap, JsonString.newInstance(), stringType.valueOf());
}

JsonType<?> arrayType = (JsonType<?>) jsonObject.get("key2");
if (arrayType instanceof JsonArray) {
    typeSafePut(typeSafeMap, JsonArray.newInstance(), arrayType.valueOf());
}

JsonType<?> objectType = (JsonType<?>) jsonObject.get("key3");
if (objectType instanceof JsonObject) {
    typeSafePut(typeSafeMap, JsonObject.newInstance(), objectType.valueOf());
}

Assert.assertTrue(typeSafeMap.size() == 3);
```

```java
void typeSafePut(Map<JsonType<?>, Object> typeSafeMap, JsonType<?> type, Object value) {
    try {
        typeSafeMap.put(type, type.cast(value));
    } catch (JsonType.UnCastableObjectToInstanceTypeException e) {
        throw e;
    }
}
```

Similarly, insertion fails when there is a type mismatch as shown below. 

```java
@Test(expected = JsonType.UnCastableObjectToInstanceTypeException.class)
public void typeSafeMapFail_Test() {
    ...
    ...
    JsonType<?> arrayType = (JsonType<?>) jsonObject.get("key2");
    if (arrayType instanceof JsonArray) {
        typeSafePut(typeSafeMap, JsonObject.newInstance(), arrayType.valueOf());
    }

    JsonType<?> objectType = (JsonType<?>) jsonObject.get("key3");
    if (objectType instanceof JsonObject) {
        typeSafePut(typeSafeMap, JsonArray.newInstance(), objectType.valueOf());
    }
}
```

The full test case can be found here:
https://github.com/MichaelMadhukalya/crimson/blob/master/src/test/java/com/crimson/types/TypeSafeMapTest.java#L11

```JsonType``` provides two additional benefits:

* ```JsonType``` is parameterized by a type parameter which is bounded by a subtype of its own type. This provides additional compile time type safety by preventing arbitrary parameterization of ```JsonType``` instances. 

```java
public abstract class JsonType<T extends JsonType> implements JsonValue {
...
}
```

* Second, ```valueOf()``` method provides access to the actual object instance associated with ```JsonType```. This semantic can be exploited to obtain a shallow copy of a valid ```JsonType``` object parsed from input e.g. ```JsonString```, ```JsonNumber```, ```JsonArray``` or ```JsonObject```.

Please see the link below for full class declaration of ```JsonType```:
https://github.com/MichaelMadhukalya/crimson/blob/master/src/main/java/com/crimson/types/JsonType.java#L5

## Implementation 
In addition to the **7** primary data types Crimson also has a few utility classes that are currently under development. 

Name | Description
-----| -----------
**JParser** | Top level parser used for parsing input object
**JsonMapper** | Util that provides interop with Java standard data structures such as *List*, *Map* etc.
**JsonWriter** | Util for serializing Json data types into raw bytes as per provided encoding scheme.

A full link to Crimson types can be found here:
https://github.com/MichaelMadhukalya/crimson/tree/master/src/main/java/com/crimson/types

## Testing
Crimson has a suite of 87 unit/functional tests. 
Link: https://github.com/MichaelMadhukalya/crimson/tree/master/src/test/java/com/crimson/types

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.crimson.types.JsonBooleanTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.125 sec
Running com.crimson.types.JsonNumberTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.266 sec
Running com.crimson.types.JsonArrayTest
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.044 sec
Running com.crimson.types.JsonObjectTest
Tests run: 53, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.17 sec
Running com.crimson.types.JsonMapperTest
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.024 sec
Running com.crimson.types.TypeSafeMapTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 sec
Running com.crimson.types.JParserTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.028 sec
Running com.crimson.types.JsonNullTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec
Running com.crimson.types.JsonStringTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 sec

Results :

Tests run: 87, Failures: 0, Errors: 0, Skipped: 0
```

## Parallel Processing
Crimson is *thread safe* and allows parallel processing of inputs via a *Fork-Join* or custom *Worker* thread pool. Link to a few test cases demonstrating parallel processing of Json inputs can be found below: 
https://github.com/MichaelMadhukalya/crimson/blob/master/src/test/java/com/crimson/types/JParserTest.java#L53
https://github.com/MichaelMadhukalya/crimson/blob/master/src/test/java/com/crimson/types/JParserTest.java#L69

## How to build?
Crimson can be built as a standard Java project using Maven. Please ensure that Maven is installed on your machine and both ```JAVA_HOME``` and ```MAVEN_HOME``` environment variables are set correctly. 

From inside the project directory run the following commands in order:
```
mvn clean
mvn compile
mvn test
mvn package
mvn install
```

This will create a ```target``` folder inside the project directory where you will find the project jar.

## Future work
As part of future work the following tasks are tentatively planned:

- [ ] Add support for schema/shape inference
- [ ] Add support for serializing data types onto disk using different compression formats e.g. ```GZIP```, ```LZ4```, ```snappy``` etc.
- [ ] Add support for a new Json data type ```JsonBinary``` capable of parsing binary data serialized in some format e.g. ```Base-64```. This will require some work at the lexical parser level.
- [ ] Add support that allows interop/conversion between User Defined Types (UDT)/Standard types and the **7** data types natively supported by Crimson via interface such as the one below:
```
<T> public T parse(Class<T> UDT, Object value)
```
- [ ] Add support for storage/retrieval of Crimson data types in standard *Big Data* storage formats such as: AVRO, PARQUET, CSV etc.  
