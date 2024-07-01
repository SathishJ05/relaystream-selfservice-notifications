package com.maersk.relaystream.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication(
    scanBasePackages = {
      "com.maersk.relaystream.notifications",
      "com.maersk.relaystream.coreengine.github.activity",
      "com.maersk.relaystream.coreengine.teamwebhook",
      "com.maersk.relaystream.coreengine.util"
    })
@EnableJms
class SpringBootAppNotifications {
  public static void main(String[] args) {
    ConfigurableApplicationContext a =
        SpringApplication.run(SpringBootAppNotifications.class, args);
    a.start();
  }
}
