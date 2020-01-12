package com.saro.search;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;

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

        webSearch.printTopJSLibraries(usedJSLibraries);
        assertFalse(usedJSLibraries.isEmpty());
    }
}
