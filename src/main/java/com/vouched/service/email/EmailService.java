package com.vouched.service.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vouched.config.AppProperties;
import com.vouched.model.TemplateFile;
import com.vouched.model.param.UserInvite;
import com.vouched.model.param.UserRequest;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Service
public class EmailService {


  private final AppProperties appProperties;
  private final TemplateService templateService;
  private final TransactionalEmailsApi apiInstance;
  private final ObjectMapper objectMapper;

  @Inject
  public EmailService(AppProperties appProperties,
      TemplateService templateService) {
    this.appProperties = appProperties;
    this.templateService = templateService;
    ApiClient defaultClient = Configuration.getDefaultApiClient();

    // Configure API key authorization: api-key
    ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
    apiKey.setApiKey(appProperties.brevoApiKey);

    apiInstance = new TransactionalEmailsApi();
    objectMapper = new ObjectMapper();
  }

  public void sendUserInvite(UserInvite userInvite) {
    Map<String, Object> params = objectMapper.convertValue(userInvite, Map.class);
    params.put("signUpLink", getSignUpLink());
    String template = templateService.getTemplate(
        TemplateFile.USER_INVITE.getTemplateName(), params);
    sendEmail("You've been invited to Vouched", template, List.of(userInvite.email()));
  }

  public void sendUserRequest(UserRequest userRequest) {
    Map<String, Object> params = objectMapper.convertValue(userRequest, Map.class);
    params.put("signUpLink", getSignUpLink());
    String template = templateService.getTemplate(
        TemplateFile.USER_REQUEST.getTemplateName(), params);
    sendEmail("User requested to join Vouched", template, appProperties.adminEmails);
  }

  private String sendEmail(String subject, String body, List<String> recipientEmails) {
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

    CreateSmtpEmail createSmtpEmail;
    try {
      createSmtpEmail = apiInstance.sendTransacEmail(smtpEmail);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }

    return createSmtpEmail.getMessageId();
  }

  private String getSignUpLink() {
    return String.format("https://%s/sign-up", appProperties.appDomain);
  }

}
