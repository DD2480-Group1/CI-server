package com.group1;


import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Assert;

import org.json.simple.JSONObject;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }
    
    @Test
    public void shouldAnswerWithFalse() {
        assertFalse(false);
    }

    // Test that parseJSON successfully parses a simple
    // json string to a json object
    @Test
    public void successfullJSONParse() {
        try {
            String validJSON = "{\"name\":\"John\", \"age\":30}";
            JSONObject j = (JSONObject) App.parseJSON(validJSON); 
            assertEquals(j.keySet().size(), 2);

        } catch (Exception e) {
            Assert.fail("Failed accessing parseJSON method");
        }
    }

    // If invalid json is parsed, the parse method should return an
    // empty JSONObject
    @Test
    public void failedJSONParse() {
        try {
            String notJSON = "just some text";
            JSONObject j = (JSONObject) App.parseJSON(notJSON);
            assertEquals(j.keySet().size(), 0);

        } catch (Exception e) {
            System.out.println("Stacktrace print is expected!");
            Assert.fail("Failed accessing parseJSON method");
        }
    }
}
