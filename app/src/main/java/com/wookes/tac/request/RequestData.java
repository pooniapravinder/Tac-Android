package com.wookes.tac.request;

import java.util.Map;

public class RequestData {
    private String url;
    private Map<String, String> params;
    private String requestMethod;

    public RequestData(String url, Map<String, String> params, String requestMethod) {
        this.url = url;
        this.params = params;
        this.requestMethod = requestMethod;
    }

    String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}
