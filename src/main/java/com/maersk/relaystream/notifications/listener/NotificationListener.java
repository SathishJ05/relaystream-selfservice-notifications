package com.maersk.relaystream.notifications.listener;

import static com.maersk.relaystream.coreengine.util.AppConstants.*;
import static com.maersk.relaystream.coreengine.util.AppConstants.JMS_PROP_TENANTCODE;
import static com.maersk.relaystream.coreengine.util.AppConstants.OPERATION_CREATE_ADDRESS;
import static com.maersk.relaystream.coreengine.util.AppConstants.OPERATION_CREATE_CLUSTER;
import static com.maersk.relaystream.coreengine.util.AppConstants.OPERATION_DELETE_ADDRESS;
import static com.maersk.relaystream.coreengine.util.AppConstants.OPERATION_DELETE_CLUSTER;
import static com.maersk.relaystream.coreengine.util.AppConstants.OPERATION_UPDATE_ADDRESS;
import static com.maersk.relaystream.coreengine.util.AppConstants.OPERATION_UPDATE_CLUSTER;
import static com.maersk.relaystream.notifications.util.NotificationConstants.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maersk.relaystream.coreengine.teamwebhook.NotificationDTO;
import com.maersk.relaystream.coreengine.teamwebhook.TeamWebhook;
import com.maersk.relaystream.notifications.util.MDCLogger;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

  private final MDCLogger mdcLogger;

  private final JdbcTemplate jdbcTemplate;

  private final TeamWebhook teamWebhook;

  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public NotificationListener(
      MDCLogger mdcLogger, JdbcTemplate jdbcTemplate, TeamWebhook teamWebhook) {
    this.mdcLogger = mdcLogger;
    this.jdbcTemplate = jdbcTemplate;
    this.teamWebhook = teamWebhook;
  }

  @JmsListener(destination = "${queue.notification}")
  public void listener(Message<String> message) {
    ObjectMapper objectMapper = new ObjectMapper();
    String tenantCode = message.getHeaders().get(JMS_PROP_TENANTCODE, String.class);
    String operation = message.getHeaders().get(JMS_PROP_OPERATION, String.class);

    mdcLogger.setMdcClusterOperations(operation, tenantCode);

    // Convert payload to NotificationDTO
    try {
      logger.info("Message is: {}", message.getPayload());
      logger.info("Headers are: {}", message.getHeaders());
      NotificationDTO notificationDTO =
          objectMapper.readValue(message.getPayload(), NotificationDTO.class);
      logger.info("Successfully converted message to NotificationDTO: {}", notificationDTO);

      // Get the channel webhook for the tenant
      String channelWebhook =
          jdbcTemplate.queryForObject(GET_CHANNEL_WEBHOOK_MESSAGE_QUERY, String.class, tenantCode);

      if (channelWebhook != null && operation != null && tenantCode != null) {
        if (channelWebhook.startsWith("https://")) {
          logger.info("Teams webhook is: {}", channelWebhook);

          sendTeamsNotification(operation, notificationDTO, channelWebhook);
        } else {
          logger.error("Channel webhook is not a valid URL for tenantCode: {}", tenantCode);
          return;
        }
      } else {
        logger.error("Invalid TenantCode or Operation or ChannelWebhook");
        return;
      }
      mdcLogger.clearMdc();
    } catch (Exception e) {
      logger.error("Exception occurred", e);
    }
  }

  private void sendTeamsNotification(
      String operation, NotificationDTO notificationDTO, String channelWebhook) {
    switch (operation) {
      case OPERATION_CREATE_CLUSTER:
        teamWebhook.sendClusterCreateNotification(
            notificationDTO.getActorName(),
            notificationDTO.getClusterCode(),
            channelWebhook,
            notificationDTO.getEnv(),
            notificationDTO.getClusterEndpoint(),
            notificationDTO.getClusterType(),
            notificationDTO.getPlatformType(),
            notificationDTO.getClusterSize(),
            notificationDTO.getAuthType());
        logger.info("Cluster create - Teams message sent successfully");
        break;
      case OPERATION_UPDATE_CLUSTER:
        teamWebhook.sendClusterUpdateNotification(
            notificationDTO.getActorName(),
            notificationDTO.getClusterCode(),
            channelWebhook,
            notificationDTO.getEnv(),
            notificationDTO.getClusterSize());
        logger.info("Cluster update - Teams message sent successfully");
        break;
      case OPERATION_DELETE_CLUSTER:
        teamWebhook.sendClusterDeleteNotification(
            notificationDTO.getActorName(),
            notificationDTO.getClusterCode(),
            channelWebhook,
            notificationDTO.getEnv());
        logger.info("Cluster delete - Teams message sent successfully");
        break;
      case OPERATION_CREATE_ADDRESS:
        teamWebhook.sendAddressCreateNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getAddressName(),
            notificationDTO.getAddressType(),
            notificationDTO.getAddressSize(),
            notificationDTO.getClaimRemaining());
        logger.info("Address create - Teams message sent successfully");
        break;
      case OPERATION_UPDATE_ADDRESS:
        teamWebhook.sendAddressUpdateNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getAddressName(),
            notificationDTO.getAddressSize(),
            notificationDTO.getClaimRemaining(),
            notificationDTO.getAddedQueues(),
            notificationDTO.getRemovedQueues());
        logger.info("Address update - Teams message sent successfully");
        break;
      case OPERATION_DELETE_ADDRESS:
        teamWebhook.sendAddressDeleteNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getAddressName(),
            notificationDTO.getClaimRemaining());
        logger.info("Address delete - Teams message sent successfully");
        break;
      case OPERATION_CREATE_TENANT:
        teamWebhook.sendTenantCreateNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getClusterCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getClusterEndpoint(),
            notificationDTO.getClaimSizeRequested(),
            notificationDTO.getKeyVaultName());
        logger.info("Tenant create - Teams message sent successfully");
        break;
      case OPERATION_UPDATE_TENANT:
        teamWebhook.sendTenantUpdateNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getClusterCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getClaimSizeRequested(),
            notificationDTO.getClaimRemaining());
        logger.info("Tenant update - Teams message sent successfully");
        break;
      case OPERATION_DELETE_TENANT:
        teamWebhook.sendTenantDeleteNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            channelWebhook,
            notificationDTO.getClusterCode(),
            notificationDTO.getEnv());
        logger.info("Tenant delete - Teams message sent successfully");
        break;
      case OPERATION_PR_APPROVAL:
        teamWebhook.sendPullRequestApproval(
            channelWebhook,
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getAddressName(),
            notificationDTO.getUuid(),
            notificationDTO.getIssueNumber(),
            notificationDTO.getEnv());
        logger.info("Pull Request Approval - Teams message sent successfully");
        break;
      case OPERATION_USER_NOTIFICATION:
        teamWebhook.sendUserNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getAddedUsers(),
            notificationDTO.getDeletedUsers());
        logger.info("User Notification - Teams message sent successfully");
        break;
      case OPERATION_SEND_PERMISSION:
        teamWebhook.sendPermissionNotification(
            notificationDTO.getActorName(),
            notificationDTO.getTenantCode(),
            notificationDTO.getEnv(),
            channelWebhook,
            notificationDTO.getAddressName(),
            notificationDTO.getProducersAdded(),
            notificationDTO.getProducersRemoved(),
            notificationDTO.getConsumersAdded(),
            notificationDTO.getConsumersRemoved());
        logger.info("Send Permission - Teams message sent successfully");
        break;
      default:
        logger.error("Operation not supported: {}", operation);
    }
  }
}
