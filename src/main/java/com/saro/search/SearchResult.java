package com.saro.search;

import lombok.Getter;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;

@NotThreadSafe
@Getter
public class SearchResult {
    private final List<SearchData> datas;
    private final SearchQuery query;

    public SearchResult(SearchQuery query, List<SearchData> datas) {
        this.query = query;
        this.datas = datas;
    }

    public List<String> getUrls(){
        List<String> urls = new ArrayList<String>(datas.size());
        for (SearchData data : datas) {
            urls.add(data.getUrl());
        }
        return urls;
    }
}
