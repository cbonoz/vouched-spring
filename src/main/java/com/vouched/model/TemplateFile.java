package com.vouched.model;

public enum TemplateFile {
  BASIC_EMAIL("basic-email.ftlh"),
  USER_INVITE("user-invite.ftlh"),
  NEW_ENDORSEMENT("new-endorsement.ftlh"),
  USER_REQUEST("user-request.ftlh");
  private final String templateName;

  TemplateFile(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return templateName;
  }


}
