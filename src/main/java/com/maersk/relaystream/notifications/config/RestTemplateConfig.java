package com.maersk.relaystream.notifications.config;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Creates a Rest template bean which does not throw exception when the backend service returns
   * 4xx or 5xx status codes.
   *
   * @param builder the builder
   * @return the rest template
   */
  @Bean
  RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .errorHandler(
            new ResponseErrorHandler() {

              @Override
              public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
                logger.debug(
                    "HTTP status code client error is {}",
                    httpResponse.getStatusCode().is4xxClientError());
                return httpResponse.getStatusCode().is5xxServerError()
                    || httpResponse.getStatusCode().is4xxClientError();
              }

              @Override
              public void handleError(ClientHttpResponse httpResponse) throws IOException {
                logger.error("HTTP status code is {}", httpResponse.getStatusCode().value());
                logger.debug(
                    "HTTP status code client error is {}",
                    httpResponse.getStatusCode().is4xxClientError());

                if (httpResponse.getStatusCode().is5xxServerError()) {
                  // Do not throw exception
                  logger.error("Exception for HTTP 5xx in HTTPClient");
                } else if (httpResponse.getStatusCode().is4xxClientError()) {
                  // handle CLIENT_ERROR
                  logger.error(
                      "HTTP status code client error in else is {}",
                      httpResponse.getStatusCode().is4xxClientError());
                  logger.error("Bad request exception in HTTPClient");
                }
              }
            })
        .build();
  }
}
