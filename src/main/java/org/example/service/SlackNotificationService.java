package org.example.service;

import com.slack.api.Slack;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    public static void sendMessage(String token, String channelId, String message) {
        Slack slack = Slack.getInstance();

        try {
            // Build and send the message request
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId) // Channel or user ID (for DM)
                    .text(message)
                    .build();

            ChatPostMessageResponse response = slack.methods(token).chatPostMessage(request);

            if (response.isOk()) {
                LOGGER.info("Message sent successfully: {} ", response.getMessage().getText());
            } else {
                LOGGER.info("Error: {} ", response.getError());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
