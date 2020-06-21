package com.crimson.types;

import javax.json.JsonValue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonMapper {
  public Map<Object, Object> toMap(JsonObject object) {
    return object.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> transform(e.getValue())));
  }

  public List<Object> toList(JsonArray array) {
    return array.stream().map(e -> transform((JsonType) e)).collect(Collectors.toList());
  }

  public List<Object> toList(String input) {
    return toList(JsonArray.newInstance().cast(input));
  }

  public Map<Object, Object> toMap(String input) {
    return toMap(JsonObject.newInstance().cast(input));
  }

  public JsonObject toJsonObject(String input) {
    return JsonObject.newInstance().cast(input);
  }

  public JsonArray toJsonArray(String input) {
    return JsonArray.newInstance().cast(input);
  }

  public String toString(JsonObject jsonObject) {
    return jsonObject.toString();
  }

  public String toString(JsonArray jsonArray) {
    return jsonArray.toString();
  }

  private Object transform(Object type) {
    if (type instanceof JsonType) {
      JsonType<?> jsonType = (JsonType<?>) type;

      if (isScalarType(jsonType)) {
        if (jsonType instanceof JsonNull) {
          return JsonValue.NULL;
        } else if (jsonType instanceof JsonBoolean) {
          return Boolean.valueOf(((JsonBoolean) jsonType).booleanValue);
        } else if (jsonType instanceof JsonNumber) {
          return BigDecimal.valueOf(((JsonNumber) jsonType).number.doubleValue());
        } else if (jsonType instanceof JsonString) {
          return ((JsonString) jsonType).string;
        }
      } else if (isStructureType(jsonType)) {
        if (jsonType instanceof JsonArray) {
          return toList((JsonArray) jsonType);
        } else if (jsonType instanceof JsonObject) {
          return toMap((JsonObject) jsonType);
        }
      }
    }

    throw new IllegalArgumentException(
        String.format("Specified input Json type is not in valid format"));
  }

  private boolean isScalarType(JsonType<?> jsonType) {
    return jsonType instanceof JsonNull
        || jsonType instanceof JsonBoolean
        || jsonType instanceof JsonNumber
        || jsonType instanceof JsonString;
  }

  private boolean isStructureType(JsonType<?> jsonType) {
    return jsonType instanceof JsonArray || jsonType instanceof JsonObject;
  }
}
