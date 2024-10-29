package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailNotifierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifierService.class);

    private EmailNotifierService() {
    }

    public static void sendEmail() {

        // Recipient's email address
        String to = "prabhu18r@gmail.com";

        // Sender's email address
        String from = "prabhurao39@gmail.com";

        // Gmail SMTP server host and port
        String host = "smtp.gmail.com";
        int port = 587;

        // Sender's email login credentials
        final String username = "prabhurao39@gmail.com"; // Your email
        final String password = "pqgy mnov qyue dhxm"; // Your email app password (not the regular password)

        // Setting up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS (TLS encryption)
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.trust", host);

        // Get the default Session object
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set the "From" header field
            message.setFrom(new InternetAddress(from));

            // Set the "To" header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set the "Subject" header field
            message.setSubject("Email Subject : vulnerabilities found ");

            // Set the message content (body)
            message.setText("Hello, this is a email to inform vulnerabilities are found in your repo ");

            // Send message
            Transport.send(message);

            LOGGER.info("Sent message successfully....");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

