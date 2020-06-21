package com.crimson.types;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser.Event;
import java.util.*;
import java.util.stream.Collectors;

public class JsonArray extends JsonType<JsonArray> implements javax.json.JsonArray {
  JParser parser;
  List<? super JsonValue> list = new ArrayList<>();

  private JsonArray() {}

  public static final JsonArray newInstance() {
    return new JsonArray();
  }

  @Override
  public JsonObject getJsonObject(int i) {
    com.crimson.types.JsonObject object = com.crimson.types.JsonObject.newInstance();
    object.cast(list.get(i));
    return object;
  }

  @Override
  public javax.json.JsonArray getJsonArray(int i) {
    JsonArray array = JsonArray.newInstance();
    array.cast(list.get(i));
    return array;
  }

  @Override
  public JsonNumber getJsonNumber(int i) {
    com.crimson.types.JsonNumber number = com.crimson.types.JsonNumber.newInstance();
    number.cast(list.get(i));
    return number;
  }

  @Override
  public JsonString getJsonString(int i) {
    com.crimson.types.JsonString string = com.crimson.types.JsonString.newInstance();
    string.cast(list.get(i));
    return string;
  }

  @Override
  public <T extends JsonValue> List<T> getValuesAs(Class<T> aClass) {
    List<T> newlist = new ArrayList<>();
    newlist =
        list.stream()
            .map(
                e -> {
                  T val = null;
                  JsonType<?> jsonType = (JsonType<?>) e;
                  val = (T) jsonType.cast(((JsonType<?>) e).toString());
                  return val;
                })
            .collect(Collectors.toList());

    return newlist;
  }

  @Override
  public String getString(int i) {
    JsonType<?> valueType = (JsonType<?>) list.get(i);
    return valueType.valueOf().toString();
  }

  @Override
  @Deprecated
  public String getString(int i, String s) {
    return null;
  }

  @Override
  public int getInt(int i) {
    com.crimson.types.JsonNumber number = com.crimson.types.JsonNumber.newInstance();
    number.cast(list.get(i));
    return number.intValue();
  }

  @Override
  @Deprecated
  public int getInt(int i, int i1) {
    return 0;
  }

  @Override
  public boolean getBoolean(int i) {
    com.crimson.types.JsonBoolean jsonBoolean = com.crimson.types.JsonBoolean.newInstance();
    jsonBoolean.cast(list.get(i));
    return jsonBoolean.booleanValue;
  }

  @Override
  @Deprecated
  public boolean getBoolean(int i, boolean b) {
    return false;
  }

  @Override
  public boolean isNull(int i) {
    JsonType<?> valueType = (JsonType<?>) list.get(i);
    if (null != valueType && valueType.toString().equals("null")) {
      return true;
    }

    return false;
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return CollectionUtils.isEmpty(list);
  }

  @Override
  public boolean contains(Object o) {
    return list.stream().filter(e -> e.equals(o)).count() >= 1 ? true : false;
  }

  @Override
  public Iterator<JsonValue> iterator() {
    return (Iterator<JsonValue>) list.iterator();
  }

  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @Override
  public <T> T[] toArray(T[] ts) {
    return list.toArray(ts);
  }

  @Override
  public boolean add(JsonValue jsonValue) {
    return list.add(jsonValue);
  }

  @Override
  public boolean remove(Object o) {
    return list.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return list.containsAll(collection);
  }

  @Override
  public boolean addAll(Collection<? extends JsonValue> collection) {
    return list.addAll(collection);
  }

  @Override
  public boolean addAll(int i, Collection<? extends JsonValue> collection) {
    return list.addAll(i, collection);
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return list.removeAll(collection);
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return list.retainAll(collection);
  }

  @Override
  public void clear() {
    list.clear();
  }

  @Override
  public JsonValue get(int i) {
    return (JsonValue) list.get(i);
  }

  @Override
  public JsonValue set(int i, JsonValue jsonValue) {
    return (JsonValue) list.set(i, jsonValue);
  }

  @Override
  public void add(int i, JsonValue jsonValue) {
    list.add(i, jsonValue);
  }

  @Override
  public JsonValue remove(int i) {
    return (JsonValue) list.remove(i);
  }

  @Override
  public int indexOf(Object o) {
    int index = -1;

    for (Object e : list) {
      ++index;
      if (e.equals(o)) {
        return index;
      }
    }

    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    int lastIndex = -1, index = 0;

    for (Object e : list) {
      if (e.equals(o)) {
        lastIndex = index;
      }
      index++;
    }

    return lastIndex;
  }

  @Override
  public ListIterator<JsonValue> listIterator() {
    return (ListIterator<JsonValue>) list.listIterator();
  }

  @Override
  public ListIterator<JsonValue> listIterator(int i) {
    return listIterator(i);
  }

  @Override
  public List<JsonValue> subList(int i, int i1) {
    return subList(i, i1);
  }

  @Override
  public ValueType getValueType() {
    return ValueType.ARRAY;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer().append("[");
    list.stream()
        .forEach(
            e -> {
              JsonType<?> valueType = (JsonType<?>) e;
              buffer.append(valueType.toString()).append(',');
            });

    if (buffer.length() > 1 && buffer.charAt(buffer.length() - 1) == ',') {
      buffer.deleteCharAt(buffer.length() - 1);
    }
    buffer.append("]");
    return buffer.toString();
  }

  JsonArray cast(Object... args) {
    if (ArrayUtils.isEmpty(args)) {
      throw new IllegalArgumentException(
          String.format("Can't construct valid JsonArray from null input"));
    }

    if (args.length > 1) {
      this.parser = (JParser) args[1];
    }

    return cast(args[0]);
  }

  @Override
  public JsonArray cast(Object value) {
    if (null == value) {
      throw new IllegalArgumentException(
          String.format("Can't construct valid JsonArray from null object"));
    }

    if (CollectionUtils.isNotEmpty(list)) {
      return this;
    }

    try {
      if (null == parser) {
        parser = JParser.newInstance(value.toString());
      }

      Event event = parser.next();
      if (!event.equals(Event.START_ARRAY)) {
        throw new IllegalArgumentException(
            String.format(
                "JsonArray should always begin with START_ARRAY but found {%s} instead",
                event.toString()));
      }

      String key = null;
      JsonType<?> val = null;
      Object data = null;

      boolean end = false;
      while (parser.hasNext() && !end) {
        event = parser.next();

        switch (event) {
          case START_OBJECT:
            parser.pushBack(Event.START_OBJECT);
            val = com.crimson.types.JsonObject.newInstance().cast(value, parser);
            list.add(val);
            break;
          case END_OBJECT:
            throw new UnCastableObjectToInstanceTypeException(
                String.format("Error parsing input JSON unbalanced END_OBJECT object found"));
          case START_ARRAY:
            parser.pushBack(Event.START_ARRAY);
            val = com.crimson.types.JsonArray.newInstance().cast(value, parser);
            list.add(val);
            break;
          case END_ARRAY:
            end = true;
            break;
          case KEY_NAME:
            key = parser.getString();
            if (StringUtils.isEmpty(key)) {
              throw new UnCastableObjectToInstanceTypeException(
                  String.format("Key name can't be null or empty in JsonObject"));
            }
            break;
          case VALUE_STRING:
            data = parser.getString();
            val = com.crimson.types.JsonString.newInstance().cast(data);
            list.add(val);
            break;
          case VALUE_NUMBER:
            data = parser.getBigDecimal();
            val = com.crimson.types.JsonNumber.newInstance().cast(data);
            list.add(val);
            break;
          case VALUE_TRUE:
            val = com.crimson.types.JsonBoolean.newInstance().cast(JsonValue.TRUE);
            list.add(val);
            break;
          case VALUE_FALSE:
            val = com.crimson.types.JsonBoolean.newInstance().cast(JsonValue.FALSE);
            list.add(val);
            break;
          case VALUE_NULL:
            val = com.crimson.types.JsonNull.newInstance().cast(JsonValue.NULL);
            list.add(val);
            break;
          default:
            throw new UnCastableObjectToInstanceTypeException(
                String.format("Unknown event type encountered parsing input for JsonObject"));
        }
      }

      /* Close parser if no more tokens are left to parse */
      if (!parser.hasNext()) {
        parser.close();
        parser = null;
      }

      super.value = this;
    } catch (Exception e) {
      throw new UnCastableObjectToInstanceTypeException(
          String.format("Exception creating JsonArray from input string {%s}: {%s}", value, e));
    }

    return this;
  }
}
