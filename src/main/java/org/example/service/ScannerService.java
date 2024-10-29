package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import okhttp3.Response;
import org.example.HttpClientBuilder;
import org.example.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerService extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void getVulnerabilityStatus() {
        // Create a request object
        Request request = new Request.Builder()
                .url(AppConstants.COMMIT_ID_API)
                .addHeader("Authorization", "Bearer " + AppConstants.BEARER_TOKEN)
                .build();

        try {
            // Send the request and get the response
            Response response = HttpClientBuilder.getOKHttpClient().newCall(request).execute();

            if (response.isSuccessful()) {
                // Get the response body as a string
                String jsonResponse = response.body().string();
                // Parse the JSON response using Jackson ObjectMapper
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                int totalVulnerabilities = jsonNode.get("commits").get(0).get("totalVulnerabilities").asInt();
                if (totalVulnerabilities > 1) {

                    // calls the email notification service for configured account
                    EmailNotifierService.sendEmail();

                    // call the slack notification service which requires slack account / setup
                    SlackNotificationService.sendMessage(AppConstants.TOKEN, AppConstants.CHANNEL_ID, AppConstants.MESSAGE);
                }
            } else {
                LOGGER.info("Request failed with status code: {} ", response.code());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Call the API
                getVulnerabilityStatus();

                // Sleep for 10 minutes (600000 milliseconds)
                Thread.sleep(10 * 60 * 1000L);

            } catch (InterruptedException e) {
                LOGGER.info("Thread was interrupted: {} ", e.getMessage());
                Thread.currentThread().interrupt();  // Restore interrupted status
                break;
            }
        }
    }
}
