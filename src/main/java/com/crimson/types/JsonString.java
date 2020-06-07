package com.crimson.types;

public class JsonString extends JsonType<JsonString> implements javax.json.JsonString {
    String string;

    private JsonString() {
    }

    public static final JsonString newInstance() {
        return new JsonString();
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public CharSequence getChars() {
        return null;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public String toString() {
        if (null != string && string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"') {
            return string;
        } else {
            return new StringBuffer().append("\"").append(string).append("\"").toString();
        }
    }

    @Override
    public JsonString cast(Object value) {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonString from null object");
        }

        string = String.valueOf(value);
        super.value = this;
        return this;
    }
}