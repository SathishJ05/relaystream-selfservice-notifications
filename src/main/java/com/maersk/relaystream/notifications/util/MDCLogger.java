package com.maersk.relaystream.notifications.util;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCLogger {

  private static final String MDC_KEY_OPERATION = "operation";
  private static final String MDC_KEY_KEY = "key";
  private static final String MDC_KEY_KEY1 = "key1";
  private static final String MDC_KEY_SERVICEORDER = "keyServiceOrder";

  public void setMdcClusterOperations(String operation, String tenantCode) {
    MDC.put(MDC_KEY_OPERATION, operation);
    MDC.put(MDC_KEY_KEY, "tenantCode=" + tenantCode);
  }

  public void clearMdc() {
    MDC.remove(MDC_KEY_OPERATION);
    MDC.remove(MDC_KEY_KEY);
    MDC.remove(MDC_KEY_KEY1);
    MDC.remove(MDC_KEY_SERVICEORDER);
  }
}
