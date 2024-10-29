package org.example;

import okhttp3.OkHttpClient;

public class HttpClientBuilder {

    private HttpClientBuilder() {
    }

    public static OkHttpClient getOKHttpClient() {
        return new OkHttpClient().newBuilder().build();
    }
}
