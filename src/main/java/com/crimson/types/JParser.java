package com.crimson.types;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import javax.json.Json;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import java.io.Closeable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class JParser implements JsonParser, Closeable {
    /**
     * Cache of JParser instance by thread
     */
    static final Map<String, JParser> cache = new HashMap<>();
    private final Logger logger = Logger.getLogger(getClass());
    private final Stack<Event> events = new Stack<>();
    /**
     * Mutable state of the parser
     */
    private JsonParser jsonParser;
    private String input;

    private JParser() {
        this(null);
    }

    private JParser(String input) {
        if (!validate(input)) {
            throw new IllegalArgumentException(
                    String.format("Input JSON string {%s} has either unbalanced or out of order parenthesis", input));
        }

        this.input = input;
        this.jsonParser = Json.createParser(new StringReader(this.input));
    }

    public static JParser getOrCreateNewInstance(String input) {
        String name = Thread.currentThread().getName();
        if (cache.containsKey(name) && cache.get(name) != null) {
            return cache.get(name);
        }

        cache.put(name, new JParser(input));
        return cache.get(name);
    }

    @VisibleForTesting
    public static void cleanup() {
        cache.clear();
    }

    public boolean hasNext() {
        if (isClose()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                    "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }

        return jsonParser.hasNext();
    }

    public Event next() {
        if (checkAccess()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                    "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }

        if (events.size() == 0) {
            return jsonParser.next();
        }

        return events.pop();
    }

    @Override
    public String getString() {
        return jsonParser.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return jsonParser.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return jsonParser.getInt();
    }

    @Override
    public long getLong() {
        return jsonParser.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return jsonParser.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return jsonParser.getLocation();
    }

    public void pushBack(Event event) {
        if (checkAccess()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                    "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }

        events.push(event);
    }

    @Override
    public void close() {
        if (checkAccess()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                    "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }

        if (null != jsonParser) {
            jsonParser.close();
            jsonParser = null;
            input = null;
            String name = Thread.currentThread().getName();
            cache.remove(name);
        }

        logger.debug(String.format("Removed JParser instance for thread: {%s}", Thread.currentThread().getName()));
    }

    public boolean checkAccess() {
        return isClose();
    }

    public boolean isClose() {
        return !cache.containsKey(Thread.currentThread().getName()) || null == cache.get(Thread.currentThread().getName());
    }

    /**
     * Validate a given input string to verify if it is in valid JSON format. The validation is done based on checking of syntax
     * to ensure that out of order parenthesis and un-balanced parenthesis are caught properly. However, semantic verification
     * is only done at the time of parsing e.g. ensuring that every key type is represented as a string.
     *
     * @param input Input
     * @return True if JSON string is valid, false otherwise
     */
    private boolean validate(String input) {
        Stack<String> stack = new Stack<>();
        String START_OBJECT = "{", END_OBJECT = "}";
        String START_ARRAY = "[", END_ARRAY = "]";

        for (int i = 0; i < input.length(); i++) {
            String current = String.valueOf(input.charAt(i));
            if (current.equals(START_OBJECT) || current.equals(START_ARRAY)) {
                stack.push(current);
            } else if (current.equals(END_OBJECT)) {
                if (stack.size() < 1) {
                    return false;
                } else if (!stack.peek().equals(START_OBJECT)) {
                    return false;
                } else {
                    stack.push(current);
                    stack.pop();
                    stack.pop();
                }
            } else if (current.equals(END_ARRAY)) {
                if (stack.size() < 1) {
                    return false;
                } else if (!stack.peek().equals(START_ARRAY)) {
                    return false;
                } else {
                    stack.push(current);
                    stack.pop();
                    stack.pop();
                }
            }
        }

        return stack.isEmpty();
    }
}