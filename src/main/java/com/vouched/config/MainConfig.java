package com.vouched.config;

import com.hubspot.rosetta.jdbi3.RosettaRowMapperFactory;
import com.vouched.dao.UserDao;
import com.vouched.dao.endorsement.AccessDao;
import com.vouched.dao.endorsement.EndorsementDao;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapperFactory;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
public class MainConfig {

  // Hikari
  @Bean
  @Qualifier("main")
  public DataSource dataSource(
      @Value("${spring.datasource.url}") String url,
      @Value("${spring.datasource.username}") String username,
      @Value("${spring.datasource.password}") String password) {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(url);
    ds.setUsername(username);
    ds.setPassword(password);
    return ds;
  }

  public static RowMapperFactory factory(Class<?> type) {
    return RowMapperFactory.of(type, BeanMapper.of(type));
  }

  @Bean
  public Jdbi jdbi(@Qualifier("main") DataSource ds) {
    TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
    Jdbi jdbi = Jdbi.create(proxy);

    // Install standard psql plugins with json parsing
    jdbi.installPlugin(
        new JdbiPlugin() {
          @Override
          public void customizeJdbi(Jdbi jdbi) {
            // Handle absent value as optional if single result

            jdbi.installPlugin(new PostgresPlugin());
            jdbi.installPlugin(new SqlObjectPlugin());
            jdbi.installPlugin(new Jackson2Plugin());
            jdbi.registerRowMapper(new RosettaRowMapperFactory());
            jdbi.registerColumnMapper(new OptionalMapperFactory());
          }
        });
    return jdbi;
  }

  @Bean
  public EndorsementDao endorsementDao(Jdbi jdbi) {
    return jdbi.onDemand(EndorsementDao.class);
  }

  @Bean
  public AccessDao endorsementAccessDao(Jdbi jdbi) {
    return jdbi.onDemand(AccessDao.class);
  }


  @Bean
  public UserDao userDao(Jdbi jdbi) {
    return jdbi.onDemand(UserDao.class);
  }
}
