package com.saro.search;

import org.junit.Test;

import java.util.List;

import static com.saro.search.WebSearch.*;
import static org.junit.Assert.assertEquals;

public class WebSearchTest {

    @Test
    public void test_PrintTop5UsedJSLibs() {
        List<String> topJSLibs = printTopJSLibraries(buildSearchQuery("java tutorials", 10));
        assertEquals(5, topJSLibs.size());
    }
}
