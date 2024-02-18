package com.vouched.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class LowerCaseWithUnderscoresModule extends SimpleModule {

  @Override
  public void setupModule(SetupContext context) {
    context.setNamingStrategy(new SnakeCaseStrategy());
  }
}