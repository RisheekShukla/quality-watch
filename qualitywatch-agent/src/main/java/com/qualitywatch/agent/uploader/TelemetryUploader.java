package com.qualitywatch.agent.uploader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitywatch.agent.model.TelemetryPayload;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TelemetryUploader {

    private static final MediaType JSON = MediaType.parse("application/json");

    private final String serverUrl;
    private final String apiKey;
    private final Log log;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelemetryUploader(String serverUrl, String apiKey, Log log) {
        this.serverUrl = serverUrl;
        this.apiKey = apiKey;
        this.log = log;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void upload(TelemetryPayload payload) throws IOException {
        String url = serverUrl.replaceAll("/$", "") + "/api/v1/telemetry/upload";

        String json = objectMapper.writeValueAsString(payload);

        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        if (apiKey != null && !apiKey.isEmpty()) {
            requestBuilder.header("X-API-Key", apiKey);
        }

        Request request = requestBuilder.build();

        log.info("Uploading telemetry to: " + url);

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.code() + " - " + response.message());
            }
            log.info("Upload successful");
        }
    }
}
