/*
 * Copyright 2018-2020 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.jacklyn.notification.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcdng.jacklyn.common.business.AbstractJacklynBusinessService;
import com.tcdng.jacklyn.common.business.SystemNotificationProvider;
import com.tcdng.jacklyn.common.data.SystemNotification;
import com.tcdng.jacklyn.notification.constants.NotificationModuleErrorConstants;
import com.tcdng.jacklyn.notification.constants.NotificationModuleNameConstants;
import com.tcdng.jacklyn.notification.constants.NotificationModuleSysParamConstants;
import com.tcdng.jacklyn.notification.data.Message;
import com.tcdng.jacklyn.notification.data.MessageDictionary;
import com.tcdng.jacklyn.notification.data.NotificationChannelDef;
import com.tcdng.jacklyn.notification.data.NotificationTemplateDef;
import com.tcdng.jacklyn.notification.entities.Notification;
import com.tcdng.jacklyn.notification.entities.NotificationAttachment;
import com.tcdng.jacklyn.notification.entities.NotificationAttachmentQuery;
import com.tcdng.jacklyn.notification.entities.NotificationChannel;
import com.tcdng.jacklyn.notification.entities.NotificationChannelQuery;
import com.tcdng.jacklyn.notification.entities.NotificationInbox;
import com.tcdng.jacklyn.notification.entities.NotificationInboxQuery;
import com.tcdng.jacklyn.notification.entities.NotificationQuery;
import com.tcdng.jacklyn.notification.entities.NotificationRecipient;
import com.tcdng.jacklyn.notification.entities.NotificationRecipientQuery;
import com.tcdng.jacklyn.notification.entities.NotificationTemplate;
import com.tcdng.jacklyn.notification.entities.NotificationTemplateQuery;
import com.tcdng.jacklyn.notification.utils.NotificationUtils;
import com.tcdng.jacklyn.notification.utils.NotificationUtils.TemplateNameParts;
import com.tcdng.jacklyn.shared.notification.MessageType;
import com.tcdng.jacklyn.shared.notification.NotificationInboxReadStatus;
import com.tcdng.jacklyn.shared.notification.NotificationStatus;
import com.tcdng.jacklyn.shared.notification.data.ToolingAttachmentGenItem;
import com.tcdng.jacklyn.shared.xml.config.module.ModuleConfig;
import com.tcdng.jacklyn.shared.xml.config.module.NotificationTemplateConfig;
import com.tcdng.jacklyn.system.business.SystemService;
import com.tcdng.jacklyn.system.entities.Authentication;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.FileAttachment;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default notification business service implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Transactional
@Component(NotificationModuleNameConstants.NOTIFICATIONSERVICE)
public class NotificationServiceImpl extends AbstractJacklynBusinessService
        implements NotificationService, SystemNotificationProvider {

    private static final String SEND_NOTIFICATION_LOCK = "notif::sendnotification-lock";

    @Configurable
    private SystemService systemService;

    @Configurable(NotificationModuleNameConstants.EMAILMESSAGINGCHANNEL)
    private MessagingChannel emailNotificationChannel;

    @Configurable(NotificationModuleNameConstants.SMSMESSAGINGCHANNEL)
    private MessagingChannel smsNotificationChannel;

    private FactoryMap<String, NotificationTemplateDef> templates;

    private FactoryMap<String, NotificationChannelDef> channels;

    public NotificationServiceImpl() {
        templates = new FactoryMap<String, NotificationTemplateDef>(true) {

            @Override
            protected boolean stale(String templateGlobalName, NotificationTemplateDef notificationTemplateDef)
                    throws Exception {
                boolean stale = false;
                try {
                    TemplateNameParts templateNames = NotificationUtils.getTemplateNameParts(templateGlobalName);
                    long versionNo =
                            db().value(long.class, "versionNo", new NotificationTemplateQuery()
                                    .moduleName(templateNames.getModuleName()).name(templateNames.getTemplateName()));
                    stale = versionNo != notificationTemplateDef.getVersionNo();
                } catch (Exception e) {
                    logError(e);
                }

                return stale;
            }

            @Override
            protected NotificationTemplateDef create(String templateGlobalName, Object... params) throws Exception {
                TemplateNameParts templateNames = NotificationUtils.getTemplateNameParts(templateGlobalName);
                NotificationTemplate notificationTemplate =
                        db().list(new NotificationTemplateQuery().moduleName(templateNames.getModuleName())
                                .name(templateNames.getTemplateName()));
                if (notificationTemplate == null) {
                    throw new UnifyException(NotificationModuleErrorConstants.MESSAGE_TEMPLATE_WITH_NAME_UNKNOWN,
                            templateGlobalName);
                }

                return new NotificationTemplateDef(notificationTemplate.getId(),
                        resolveApplicationMessage(notificationTemplate.getSubject()),
                        notificationTemplate.getActionLink(),
                        StringUtils.breakdownParameterizedString(notificationTemplate.getTemplate()),
                        notificationTemplate.getMessageType(), notificationTemplate.getHtmlFlag(),
                        notificationTemplate.getVersionNo());
            }

        };

        channels = new FactoryMap<String, NotificationChannelDef>(true) {

            @Override
            protected boolean stale(String name, NotificationChannelDef notificationChannelDef) throws Exception {
                boolean stale = false;
                try {
                    long versionNo = db().value(long.class, "versionNo", new NotificationChannelQuery().name(name));
                    stale = versionNo != notificationChannelDef.getVersionNo();
                } catch (Exception e) {
                    logError(e);
                }

                return stale;
            }

            @Override
            protected NotificationChannelDef create(String name, Object... params) throws Exception {
                NotificationChannel notificationChannel = db().list(new NotificationChannelQuery().name(name));
                if (notificationChannel == null) {
                    throw new UnifyException(NotificationModuleErrorConstants.MESSAGE_CHANNEL_WITH_NAME_UNKNOWN, name);
                }

                String userName = null;
                String password = null;
                if (notificationChannel.getAuthenticationId() != null) {
                    Authentication authentication =
                            systemService.findAuthentication(notificationChannel.getAuthenticationId()).getData();
                    userName = authentication.getUserName();
                    password = authentication.getPassword();
                }

                return new NotificationChannelDef(notificationChannel.getId(), notificationChannel.getName(),
                        notificationChannel.getNotificationType(), notificationChannel.getHostAddress(),
                        notificationChannel.getHostPort(), notificationChannel.getSecurityType(), userName, password,
                        notificationChannel.getVersionNo());
            }

        };
    }

    @Override
    public Long createNotificationTemplate(NotificationTemplate notificationTemplate) throws UnifyException {
        return (Long) db().create(notificationTemplate);
    }

    @Override
    public NotificationTemplate findNotificationTemplate(Long notificationTemplateId) throws UnifyException {
        return db().find(NotificationTemplate.class, notificationTemplateId);
    }

    @Override
    public NotificationTemplate findNotificationTemplate(String moduleName, String name) throws UnifyException {
        return db().list(new NotificationTemplateQuery().moduleName(moduleName).name(name));
    }

    @Override
    public List<NotificationTemplate> findNotificationTemplates(NotificationTemplateQuery query) throws UnifyException {
        return db().listAll(query.addSelect("id", "name", "description", "subject", "htmlFlag", "moduleId", "moduleName",
                "moduleDescription", "status", "statusDesc"));
    }

    @Override
    public int updateNotificationTemplate(NotificationTemplate notificationTemplate) throws UnifyException {
        return db().updateByIdVersion(notificationTemplate);
    }

    @Override
    public int deleteNotificationTemplate(Long id) throws UnifyException {
        return db().delete(NotificationTemplate.class, id);
    }

    @Override
    public NotificationTemplateDef getRuntimeNotificationTemplateDef(String templateGlobalName) throws UnifyException {
        return templates.get(templateGlobalName);
    }

    @Override
    public Long createNotificationChannel(NotificationChannel notificationChannel) throws UnifyException {
        return (Long) db().create(notificationChannel);
    }

    @Override
    public NotificationChannel findNotificationChannel(Long notificationChannelId) throws UnifyException {
        return db().find(NotificationChannel.class, notificationChannelId);
    }

    @Override
    public NotificationChannel findNotificationChannel(NotificationChannelQuery query) throws UnifyException {
        return db().find(query);
    }

    @Override
    public List<NotificationChannel> findNotificationChannels(NotificationChannelQuery query) throws UnifyException {
        return db().listAll(query);
    }

    @Override
    public int updateNotificationChannel(NotificationChannel notificationChannel) throws UnifyException {
        return db().updateByIdVersion(notificationChannel);
    }

    @Override
    public int deleteNotificationChannel(Long id) throws UnifyException {
        return db().delete(NotificationChannel.class, id);
    }

    @Override
    public void sendNotification(Message message) throws UnifyException {
        NotificationTemplateDef notificationTemplateDef = templates.get(message.getTemplateGlobalName());
        NotificationChannelDef notifChannelDef = channels.get(message.getNotificationChannelName());

        if (notifChannelDef.getNotificationType().internal()) {
            // Internal system notifications
            Map<String, Object> dictionary = message.getDictionary();
            dictionary.put("subject", notificationTemplateDef.getSubject());
            dictionary.put("senderContact", message.getSenderContact());
            dictionary.put("senderName", resolveApplicationMessage(message.getSenderName()));
            String messageBody =
                    StringUtils.buildParameterizedString(notificationTemplateDef.getTokenList(), dictionary);
            List<String> userIdList = new ArrayList<String>();
            for (Message.Recipient recipient : message.getRecipients()) {
                userIdList.add(recipient.getName());
            }

            createSystemNotifications(notificationTemplateDef.getMessageType(), notificationTemplateDef.getSubject(),
                    messageBody, notificationTemplateDef.getActionLink(), message.getReference(), userIdList);
        } else {
            // Put notification in external communication system
            Notification notification = new Notification();
            Long notificationTemplateId = templates.get(message.getTemplateGlobalName()).getNotificationTemplateId();
            notification.setNotificationTemplateId(notificationTemplateId);

            Long notificationChannelId = notifChannelDef.getNotificationChannelId();
            notification.setNotificationChannelId(notificationChannelId);

            byte[] dictionary = null;
            if (message.isDictionary()) {
                dictionary = IOUtils.streamToBytes(message.getDictionary());
            }

            notification.setSenderName(message.getSenderName());
            notification.setSenderContact(message.getSenderContact());
            notification.setReference(message.getReference());
            notification.setDictionary(dictionary);
            notification.setDueDt(db().getNow());
            notification.setAttempts(Integer.valueOf(0));
            notification.setStatus(NotificationStatus.NOT_SENT);
            Long notificationId = (Long) db().create(notification);

            NotificationRecipient notifRecipient = new NotificationRecipient();
            notifRecipient.setNotificationId(notificationId);
            for (com.tcdng.jacklyn.notification.data.Message.Recipient recipient : message.getRecipients()) {
                notifRecipient.setRecipientName(recipient.getName());
                notifRecipient.setRecipientContact(recipient.getContact());
                db().create(notifRecipient);
            }

            if (!message.getAttachments().isEmpty()) {
                NotificationAttachment notifAttachment = new NotificationAttachment();
                notifAttachment.setNotificationId(notificationId);
                for (com.tcdng.jacklyn.notification.data.Message.Attachment attachment : message.getAttachments()) {
                    notifAttachment.setType(attachment.getType());
                    notifAttachment.setFileName(attachment.getFileName());
                    notifAttachment.setData(attachment.getData());
                    db().create(notifAttachment);
                }
            }
        }
    }

    @Override
    public List<Notification> findNotifications(NotificationQuery query) throws UnifyException {
        return db().listAll(query);
    }

    @Override
    public Notification findNotification(Long notificationId) throws UnifyException {
        return db().list(Notification.class, notificationId);
    }

    @Override
    public int setNotificationStatus(List<Long> notificationIds, NotificationStatus status) throws UnifyException {
        return db().updateAll(new NotificationQuery().idIn(notificationIds), new Update().add("status", status));
    }

    @Override
    public List<? extends SystemNotification> findUserSystemNotifications(String userId) throws UnifyException {
        List<NotificationInbox> notificationList =
                db().listAll(new NotificationInboxQuery().userId(userId).addOrder(OrderType.DESCENDING, "createDt"));
        db().updateAll(new NotificationInboxQuery().userId(userId).status(NotificationInboxReadStatus.NOT_READ),
                new Update().add("status", NotificationInboxReadStatus.READ));
        return notificationList;
    }

    @Override
    public int countUserSystemNotifications(String userId) throws UnifyException {
        return db().countAll(new NotificationInboxQuery().userId(userId).status(NotificationInboxReadStatus.NOT_READ));
    }

    @Override
    public int dismissUserSystemNotifications(String userId) throws UnifyException {
        return db().deleteAll(new NotificationInboxQuery().userId(userId));
    }

    @Override
    public int dismissUserSystemNotification(SystemNotification systemNotification) throws UnifyException {
        return db().delete(NotificationInbox.class, systemNotification.getId());
    }

    @Override
    public void createSystemNotifications(MessageType messageType, String subject, String message, String actionLink,
            String reference, List<String> userIdList) throws UnifyException {
        NotificationInbox notificationInbox = new NotificationInbox();
        notificationInbox.setSubject(subject);
        notificationInbox.setMessage(message);
        notificationInbox.setMessageType(messageType);
        notificationInbox.setActionLink(actionLink);
        notificationInbox.setActionTarget(reference);
        for (String userId : userIdList) {
            notificationInbox.setUserId(userId);
            db().create(notificationInbox);
        }
    }

    @Override
    public List<ToolingAttachmentGenItem> findToolingAttachmentGenTypes() throws UnifyException {
        return getToolingTypes(ToolingAttachmentGenItem.class, MessageAttachmentGenerator.class);
    }

    @SuppressWarnings("unchecked")
    @Periodic(PeriodicType.SLOWER)
    public void sendNotifications(TaskMonitor taskMonitor) throws UnifyException {
        if (grabClusterLock(SEND_NOTIFICATION_LOCK)) {
            try {
                if (systemService.getSysParameterValue(boolean.class,
                        NotificationModuleSysParamConstants.NOTIFICATION_ENABLED)) {
                    int maxBatchSize =
                            systemService.getSysParameterValue(int.class,
                                    NotificationModuleSysParamConstants.NOTIFICATION_MAX_BATCH_SIZE);
                    int maxAttempts =
                            systemService.getSysParameterValue(int.class,
                                    NotificationModuleSysParamConstants.NOTIFICATION_MAXIMUM_TRIES);
                    int retryMinutes =
                            systemService.getSysParameterValue(int.class,
                                    NotificationModuleSysParamConstants.NOTIFICATION_RETRY_MINUTES);

                    Date now = db().getNow();
                    List<Notification> notificationList =
                            db().listAll(new NotificationQuery().due(now).status(NotificationStatus.NOT_SENT)
                                    .orderById().setLimit(maxBatchSize));
                    for (Notification notification : notificationList) {
                        int attempts = notification.getAttempts() + 1;
                        notification.setAttempts(attempts);
                        MessageDictionary messageDictionary = null;
                        if (notification.getDictionary() != null) {
                            messageDictionary =
                                    new MessageDictionary((Map<String, Object>) IOUtils.streamFromBytes(Map.class,
                                            notification.getDictionary()));
                        } else {
                            messageDictionary = new MessageDictionary();
                        }

                        Update update = new Update();
                        if (sendNotification(notification, messageDictionary)) {
                            // Update to SENT status
                            update.add("sentDt", now).add("status", NotificationStatus.SENT);
                        } else {
                            if (attempts >= maxAttempts) {
                                update.add("status", NotificationStatus.ABORTED);
                            } else {
                                // Shift and update due date by retry minutes
                                Date dueDt =
                                        CalendarUtils.getNowWithFrequencyOffset(now, FrequencyUnit.MINUTE,
                                                retryMinutes);
                                update.add("dueDt", dueDt);
                            }
                        }

                        db().updateById(Notification.class, notification.getId(), update);
                        commitTransactions();
                    }
                }
            } finally {
                releaseClusterLock(SEND_NOTIFICATION_LOCK);
            }
        }
    }

    @Override
    public void installFeatures(List<ModuleConfig> moduleConfigList) throws UnifyException {
        // Install new and update old
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        for (ModuleConfig moduleConfig : moduleConfigList) {
            Long moduleId = systemService.getModuleId(moduleConfig.getName());
            if (moduleConfig.getNotificationTemplates() != null
                    && DataUtils.isNotBlank(moduleConfig.getNotificationTemplates().getNotificationTemplateList())) {
                logDebug("Installing message type definitions for module [{0}]...",
                        resolveApplicationMessage(moduleConfig.getDescription()));
                NotificationTemplateQuery mtQuery = new NotificationTemplateQuery();
                for (NotificationTemplateConfig notificationTemplateConfig : moduleConfig.getNotificationTemplates()
                        .getNotificationTemplateList()) {
                    mtQuery.clear();
                    NotificationTemplate oldNotificationTemplate =
                            db().find(mtQuery.moduleId(moduleId).name(notificationTemplateConfig.getName()));
                    String description = resolveApplicationMessage(notificationTemplateConfig.getDescription());
                    if (oldNotificationTemplate == null) {
                        notificationTemplate.setModuleId(moduleId);
                        notificationTemplate.setName(notificationTemplateConfig.getName());
                        notificationTemplate.setDescription(description);
                        notificationTemplate.setSubject(notificationTemplateConfig.getSubject());
                        notificationTemplate.setTemplate(notificationTemplateConfig.getTemplate());
                        notificationTemplate.setHtmlFlag(notificationTemplateConfig.isHtml());
                        notificationTemplate.setMessageType(notificationTemplateConfig.getMessageType());
                        notificationTemplate.setActionLink(notificationTemplateConfig.getActionLink());
                        db().create(notificationTemplate);
                    } else {
                        oldNotificationTemplate.setDescription(description);
                        oldNotificationTemplate.setSubject(notificationTemplateConfig.getSubject());
                        oldNotificationTemplate.setTemplate(notificationTemplateConfig.getTemplate());
                        oldNotificationTemplate.setHtmlFlag(notificationTemplateConfig.isHtml());
                        oldNotificationTemplate.setMessageType(notificationTemplateConfig.getMessageType());
                        oldNotificationTemplate.setActionLink(notificationTemplateConfig.getActionLink());
                        db().updateByIdVersion(oldNotificationTemplate);
                    }
                }
            }
        }
    }

    @Override
    public void clearCache() throws UnifyException {
        templates.clear();
        channels.clear();
    }

    private boolean sendNotification(Notification notification, MessageDictionary messageDictionary) {
        try {
            NotificationTemplateDef notificationTemplateDef =
                    templates.get(NotificationUtils.getTemplateGlobalName(notification.getModuleName(),
                            notification.getNotificationTemplateName()));
            NotificationChannelDef notificationChannelDef = channels.get(notification.getNotificationChannelName());
            MessagingChannel messageChannel = null;
            switch (notificationChannelDef.getNotificationType()) {
                case SMS:
                    messageChannel = smsNotificationChannel;
                    break;
                case EMAIL:
                default:
                    messageChannel = emailNotificationChannel;
                    break;
            }

            String subject = resolveApplicationMessage(notification.getSubject());
            String senderContact = resolveApplicationMessage(notification.getSenderContact());
            messageDictionary.addEntry("subject", subject);
            messageDictionary.addEntry("senderContact", senderContact);
            messageDictionary.addEntry("senderName", resolveApplicationMessage(notification.getSenderName()));
            String messageBody =
                    StringUtils.buildParameterizedString(notificationTemplateDef.getTokenList(),
                            messageDictionary.getDictionary());

            // Recipients
            List<String> recipientContactList =
                    db().valueList(String.class, "recipientContact",
                            new NotificationRecipientQuery().notificationId(notification.getId()));

            // Attachments. Combine generated and database attachments.
            List<FileAttachment> fileAttachmentList = Collections.emptyList();
            boolean isAttachment = false;
            String attachmentGenerator = notification.getAttachmentGenerator();
            if (StringUtils.isNotBlank(attachmentGenerator)) {
                // Generated attachments
                fileAttachmentList = new ArrayList<FileAttachment>();
                MessageAttachmentGenerator notificationAttachmentGenerator =
                        (MessageAttachmentGenerator) getComponent(attachmentGenerator);
                fileAttachmentList.addAll(notificationAttachmentGenerator.generateAttachments(messageDictionary));
                isAttachment = true;
            }

            // Database attachments
            List<NotificationAttachment> attachmentList =
                    db().listAll(new NotificationAttachmentQuery().notificationId(notification.getId()));
            if (!attachmentList.isEmpty()) {
                if (!isAttachment) {
                    fileAttachmentList = new ArrayList<FileAttachment>();
                }

                for (NotificationAttachment notificationAttachment : attachmentList) {
                    fileAttachmentList.add(new FileAttachment(notificationAttachment.getType(),
                            notificationAttachment.getFileName(), notificationAttachment.getData()));
                }
            }

            // Send
            return messageChannel.sendMessage(notificationChannelDef, subject, senderContact, recipientContactList,
                    messageBody, notificationTemplateDef.isHtml(), fileAttachmentList);
        } catch (UnifyException e) {
            logError(e);
        }
        return false;
    }
}
