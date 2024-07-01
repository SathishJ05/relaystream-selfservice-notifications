package com.maersk.relaystream.notifications.config;

import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The Class GithubConfig. Configuration class for connecting to Github. */
@Configuration
public class GithubConfig {

  /** The github token. */
  @Value("${github.token}")
  private String token;

  @Value("${github.user}")
  private String user;

  @Value("${env}")
  private String env;

  @Value("${spring.artemis.broker-url}")
  private String url;

  /**
   * Creates the github config bean.
   *
   * @return the github config bean.
   * @throws IOException if the token is not found or invalid.
   */
  @Bean
  GitHub createGithubConfig() throws IOException {
    return new GitHubBuilder().withAppInstallationToken(token).build();
  }
}
