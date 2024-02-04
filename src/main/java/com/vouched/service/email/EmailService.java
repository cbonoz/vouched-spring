package com.vouched.service.email;

import com.vouched.config.AppProperties;
import com.vouched.model.TemplateFile;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {


    private final TemplateService templateService;
    private final TransactionalEmailsApi apiInstance;

    @Inject
    public EmailService(AppProperties appProperties, TemplateService templateService) {
        this.templateService = templateService;
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(appProperties.brevoApiKey);

        apiInstance = new TransactionalEmailsApi();
    }

    public void sendUserInvite(String userEmail, Map<String, Object> params) {
        String template = templateService.getTemplate(TemplateFile.USER_INVITE.getTemplateName(), params);
        sendEmail("You've been invited to Vouched", template, List.of(userEmail));
    }

    public void sendUserRequest(String userEmail, Map<String, Object> params) {
        String template = templateService.getTemplate(TemplateFile.USER_REQUEST.getTemplateName(), params);
        sendEmail("Vouched User Request", template, List.of(userEmail));
    }

    private void sendEmail(String subject, String body, List<String> recipientEmails) {
        SendSmtpEmail smtpEmail = new SendSmtpEmail();
        smtpEmail.setSubject(subject);
        smtpEmail.setHtmlContent(body);
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setName(AppProperties.APP_NAME);
        sender.setEmail(AppProperties.SENDER_EMAIL);
        smtpEmail.setSender(sender);
        smtpEmail.setTo(recipientEmails.stream().map(email -> {
            SendSmtpEmailTo sendSmtpEmailTo = new SendSmtpEmailTo();
            sendSmtpEmailTo.setEmail(email);
            return sendSmtpEmailTo;
        }).toList());

        try {
            apiInstance.sendTransacEmail(smtpEmail);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

}
