package com.maersk.relaystream.notifications.util;

public class NotificationConstants {

  private NotificationConstants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String OPERATION_CREATE_TENANT = "create-tenant";
  public static final String OPERATION_UPDATE_TENANT = "update-tenant";
  public static final String OPERATION_DELETE_TENANT = "delete-tenant";
  public static final String OPERATION_PR_APPROVAL = "pull-request-approval";
  public static final String OPERATION_USER_NOTIFICATION = "user-notification";
  public static final String OPERATION_SEND_PERMISSION = "send-permission";
  public static final String GET_CHANNEL_WEBHOOK_MESSAGE_QUERY =
      "SELECT channel_webhook FROM tenant where tenant_code= ?";
}
