package com.saro.search;

import lombok.Getter;

@Getter
class SearchData {
    private final String url;

    SearchData(String url){
        this.url = url;
    }

    String getUrl() {
        return url;
    }
}
