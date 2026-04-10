package com.osm.common.result;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void shouldCreateSuccessResultWithData() {
        String data = "test data";
        Result<String> result = Result.success(data);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    void shouldCreateSuccessResultWithoutData() {
        Result<Void> result = Result.success();

        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateErrorResult() {
        Result<Void> result = Result.error("something went wrong");

        assertEquals(500, result.getCode());
        assertEquals("something went wrong", result.getMessage());
    }

    @Test
    void shouldCreateErrorResultWithCustomCode() {
        Result<Void> result = Result.error(400, "bad request");

        assertEquals(400, result.getCode());
        assertEquals("bad request", result.getMessage());
    }
}
