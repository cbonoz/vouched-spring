package com.vouched.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.*;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import com.vouched.config.AppProperties;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class EmailService {


    private final MailjetClient mailjetClient;

    @Inject
    public EmailService(AppProperties appProperties) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(appProperties.mailjetApiKey)
                .apiSecretKey(appProperties.mailjetSecretKey)
                .build();

        mailjetClient = new MailjetClient(options);
    }

    public void sendEmail(String subject, String body, List<String> recipientEmails) throws MailjetException {
        TransactionalEmail message1 = TransactionalEmail
                .builder()
                .to(recipientEmails.stream().map(SendContact::new).toList())
                .from(new SendContact(AppProperties.SENDER_EMAIL, AppProperties.APP_NAME))
                .htmlPart(body)
                .subject(subject)
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsRequest request = SendEmailsRequest
                .builder()
                .message(message1) // you can add up to 50 messages per request
                .build();

        // act
        SendEmailsResponse response = request.sendWith(mailjetClient);
    }

}
