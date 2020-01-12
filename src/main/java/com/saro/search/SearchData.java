package com.saro.search;

import lombok.Getter;

@Getter
public class SearchData {
    private final String url;

    public SearchData(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
