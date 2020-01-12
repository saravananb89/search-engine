package com.saro.search;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WebSearchTest {

    private WebSearch webSearch;
    private SearchResult result;

    @Before
    public void setUp() throws Exception {
        webSearch = new WebSearch();
        result = webSearch.search("java tutorials", 10);
    }

    @Test
    public void test_SearchByQuery() {
        assertFalse(result.getUrls().isEmpty());
    }

    @Test
    public void test_ExtractUsedJSLibraries() {
        List<String> usedJSLibraries = webSearch.extractJSLibraries(result);

        List<String> topJSLibs = webSearch.printTopJSLibraries(usedJSLibraries);
        assertTrue(topJSLibs.size() == 5);
    }
}
