package com.crimson.types;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class JParserTest {
    String input_1 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, 2, 3], \"key4\": "
            + "{ \"key5\": \"value5\", \"key6\": \"value6\"}, \"key7\": [1, [2, [3, 4, 5]], {\"key8\": "
            + "{\"key9\": [0.0096, 8.0E+02, {\"key10\": \"value10\", \"key11\": [1001.01, 2.1E-03, [[1.6E+02,0.2,3.14159], 4.3E+03]]}]}}]}";

    String[] inputs = new String[]{
            "{\"key12\": \"value\"}",
            "{\"key13\": null}",
            "{\"key14\": 3.234}",
            "{\"key15\": false}",
            "{\"key16\": [1, 2.0, 3.14159]}"
    };

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void verifyAsyncParserAccess_Test() throws InterruptedException, ExecutionException, TimeoutException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(input_1);

        // Start an async parsing process in the background and wait for completion
        CompletableFuture<?> completableFuture = CompletableFuture.runAsync(() -> createJsonObject());
        completableFuture.get(1_000, TimeUnit.MILLISECONDS);

        Assert.assertTrue(null != jsonObject && jsonObject.size() == 5);
    }

    private void createJsonObject() {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(input_1);
        Assert.assertTrue(null != jsonObject && jsonObject.size() == 5);
    }

    //@Test
    public void verifyForkJoinPoolParserAccess_Test() {
        // Verify parser access via ForkJoin pool (parallel stream). This test makes more sense when the number of CPUs is much
        // smaller than the size of the "inputs" array since for Fork Join pool based on parallel streams the number of threads
        // is typically set equal to the number of CPU cores available.
        Arrays.asList(inputs).parallelStream().forEach(this::createJsonObject);
    }

    private void createJsonObject(String in) {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(in);
        Assert.assertTrue(null != jsonObject && jsonObject.size() > 0);
    }

    //@Test
    public void verifyWorkerThreadPoolParserAccess_Test() throws InterruptedException, ExecutionException, TimeoutException {
        // Create custom thread pool with two threads
        ExecutorService service = Executors.newFixedThreadPool(2);

        // Use an array of completable futures to gather futures submitted to the above worker thread pool of size 2
        CompletableFuture[] completableFutures = new CompletableFuture[5];
        Arrays.asList(inputs).stream()
                .map(e -> CompletableFuture.runAsync(() -> createJsonObject(e), service))
                .collect(Collectors.toList())
                .toArray(completableFutures);

        CompletableFuture completableFuture = CompletableFuture.allOf(completableFutures);
        completableFuture.get(1_000, TimeUnit.MILLISECONDS);
    }
}