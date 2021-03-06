package com.saro.search;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
class WebSearch {
    private static SearchConfig CONFIG = new SearchConfig();

    private static final Logger log =  getLogger();

    static {
        PropertyConfigurator.configure("C:\\projekte\\search-engine\\src\\main\\resources\\log4j.properties");
    }

    WebSearch() {
    }

    static SearchQuery buildSearchQuery(String query, int numResults) {
        return new SearchQuery.Builder(query).numResults(numResults).build();
    }

    private static List<String> extractJSLibraries(SearchResult searchResult) {
        List<String> sources = new ArrayList<>();

        for (String url : searchResult.getUrls()) {
            try {
                sources.addAll(Jsoup.parse(Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000)
                        .ignoreHttpErrors(true).get().html())
                        .select("script")
                        .stream()
                        .map(element -> element.attr("src"))
                        .filter(src -> !StringUtil.isBlank(src))
                        .collect(Collectors.toList()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sources;
    }

    static List<String> printTopJSLibraries(SearchQuery searchQuery) {
        List<String> usedJSLibraries = extractJSLibraries(search(searchQuery));
        Map<String, Integer> duplicateMap = new HashMap<>();
        for (String name : usedJSLibraries) {
            Integer count = duplicateMap.get(name);
            if (count == null) {
                count = 0;
            }
            duplicateMap.put(name, count + 1);
        }

        Map<String, Integer> sortedMap = duplicateMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        List<String> topJSLibs = sortedMap.keySet().stream().limit(5).collect(toList());
        topJSLibs.
                forEach(script -> System.out.println("Printing Top 5 Used JS Libraries " + script));

        return topJSLibs;
    }

    private static SearchResult search(SearchQuery query) {
        log.debug("Search query: {}", query);
        Document response = getResponse(query);
        List<SearchData> hitsUrls = parseResponse(response);
        return new SearchResult(query, hitsUrls);
    }

    private static Document getResponse(SearchQuery query) {
        String uri = getUri(query);
        log.debug("Complete URL: {}", uri);
        Document result = null;
        try {
            result = Jsoup.connect(uri)
                    .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(5000)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getUri(SearchQuery query) {
        String uri = CONFIG.getGoogleSearchUrl().replaceAll(CONFIG.PLHD_QUERY, query.getQuery());
        uri = uri.replaceAll(CONFIG.PLHD_RESULTS_NUM,
                ifPresent(CONFIG.PLHD_RESULTS_NUM, query.getNumResults()));
        uri = uri.replaceAll(CONFIG.PLHD_SITE, ifPresent(CONFIG.PLHD_SITE, query.getSite()));
        return uri;
    }

    private static String ifPresent(String plhd, Object param) {
        if (param != null) {
            log.debug("URL parameter: {}", plhd + param);
            return plhd + param;
        } else {
            return "";
        }
    }

    private static List<SearchData> parseResponse(Document searchDoc) {

        List<SearchData> result = new ArrayList<>();

        Elements links = searchDoc.select("a[href]");

        for (Element link : links) {
            String linkHref = link.attr("href");
            Matcher matcher = CONFIG.PURE_URL_PATTERN.matcher(linkHref);
            if (matcher.matches()) {
                String pureUrl = matcher.group(1);
                if (!pureUrl.startsWith(CONFIG.CACHE_URL) && !pureUrl.startsWith(CONFIG.PREFRENCES_URL)) {
                    result.add(new SearchData(pureUrl));
                    log.debug("{}", pureUrl);
                }
            }
        }
        return result;
    }

    private static org.slf4j.Logger getLogger() {
        final Throwable t = new Throwable();
        t.fillInStackTrace();
        return LoggerFactory.getLogger(t.getStackTrace()[1].getClassName());
    }
    @Data
    public static class SearchConfig {

        private final String PLHD_QUERY = "__query__";
        private String PLHD_RESULTS_NUM = "&num=";
        private String PLHD_SITE = "&as_sitesearch=";

        private String PURE_URL_REGEX
                = "/url\\?q=(.*)&sa.*";
        private final Pattern PURE_URL_PATTERN = Pattern.compile(PURE_URL_REGEX);

        private String CACHE_URL = "http://webcache.googleusercontent.com";

        private String PREFRENCES_URL = "https://www.google.com/preferences";

        private String GOOGLE_SEARCH_URL_PREFIX = "https://www.google.com/search?";

        String getGoogleSearchUrl() {
            return GOOGLE_SEARCH_URL_PREFIX + "q=" + PLHD_QUERY + PLHD_RESULTS_NUM + PLHD_SITE;
        }
    }
}
