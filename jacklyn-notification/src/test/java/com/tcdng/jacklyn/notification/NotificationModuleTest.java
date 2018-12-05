/*
 * Copyright 2018 The Code Department
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
package com.tcdng.jacklyn.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.tcdng.jacklyn.common.AbstractJacklynTest;
import com.tcdng.jacklyn.common.constants.RecordStatus;
import com.tcdng.jacklyn.notification.business.NotificationModule;
import com.tcdng.jacklyn.notification.constants.NotificationModuleNameConstants;
import com.tcdng.jacklyn.notification.entities.Notification;
import com.tcdng.jacklyn.notification.entities.NotificationChannel;
import com.tcdng.jacklyn.notification.entities.NotificationChannelQuery;
import com.tcdng.jacklyn.notification.entities.NotificationQuery;
import com.tcdng.jacklyn.notification.entities.NotificationRecipient;
import com.tcdng.jacklyn.notification.entities.NotificationTemplate;
import com.tcdng.jacklyn.notification.entities.NotificationTemplateQuery;
import com.tcdng.jacklyn.notification.utils.NotificationUtils;
import com.tcdng.jacklyn.shared.notification.NotificationStatus;
import com.tcdng.jacklyn.shared.notification.NotificationType;
import com.tcdng.jacklyn.system.business.SystemModule;
import com.tcdng.jacklyn.system.constants.SystemModuleNameConstants;

/**
 * Notification business module tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NotificationModuleTest extends AbstractJacklynTest {

	private static final String NOTIFICATION_TEMPLATE_NAME = NotificationUtils
			.getGlobalTemplateName(SystemModuleNameConstants.SYSTEM_MODULE, "ACC-SMRY-001");

	private static final String NOTIFICATION_CHANNEL_NAME = "testNotificationChannel";

	@Test
	public void testCreateNotificationChannel() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationChannel notificationChannelData = getNotificationChannelData();
		Long id = notificationModule.createNotificationChannel(notificationChannelData);
		assertNotNull(id);
	}

	@Test
	public void testFindNotificationChannel() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationChannel notificationChannelData = getNotificationChannelData();
		Long id = notificationModule.createNotificationChannel(notificationChannelData);

		NotificationChannel fetchedNotificationChannelData
				= notificationModule.findNotificationChannel(id);
		assertEquals(notificationChannelData, fetchedNotificationChannelData);
	}

	@Test
	public void testFindNotificationChannels() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationChannel notificationChannelData = getNotificationChannelData();
		Long id = notificationModule.createNotificationChannel(notificationChannelData);

		NotificationChannel notificationChannel1 = notificationModule.findNotificationChannel(id);

		NotificationChannelQuery query = new NotificationChannelQuery();
		query.name(NOTIFICATION_CHANNEL_NAME);
		List<NotificationChannel> outwardNotificationChannelList
				= notificationModule.findNotificationChannels(query);
		assertNotNull(outwardNotificationChannelList);
		assertEquals(1, outwardNotificationChannelList.size());
		NotificationChannel notificationChannel2 = outwardNotificationChannelList.get(0);
		assertEquals(notificationChannel1.getName(), notificationChannel2.getName());
		assertEquals(notificationChannel1.getDescription(), notificationChannel2.getDescription());
		assertEquals(notificationChannel1.getNotificationType(),
				notificationChannel2.getNotificationType());
	}

	@Test
	public void testUpdateNotificationChannel() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationChannel notificationChannelData = getNotificationChannelData();
		Long id = notificationModule.createNotificationChannel(notificationChannelData);

		NotificationChannel fetchedNotificationChannelData
				= notificationModule.findNotificationChannel(id);
		fetchedNotificationChannelData.setDescription("Online Credit Alert Channel");
		int count = notificationModule.updateNotificationChannel(fetchedNotificationChannelData);
		assertEquals(1, count);

		NotificationChannel updatedNotificationChannelData
				= notificationModule.findNotificationChannel(id);
		assertFalse(fetchedNotificationChannelData.equals(notificationChannelData));
		assertEquals(fetchedNotificationChannelData, updatedNotificationChannelData);
	}

	@Test
	public void testDeleteNotificationChannel() throws Exception {
		NotificationModule notificationModule = getNotificationModule();

		NotificationChannel notificationChannelData = getNotificationChannelData();
		Long id = notificationModule.createNotificationChannel(notificationChannelData);

		int count = notificationModule.deleteNotificationChannel(id);
		assertEquals(1, count);
	}

	@Test
	public void testCreateNotificationTemplate() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationTemplate notificationTemplateData = getNotificationTemplateData();
		Long id = notificationModule.createNotificationTemplate(notificationTemplateData);
		assertNotNull(id);
	}

	@Test
	public void testFindNotificationTemplate() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationTemplate notificationTemplateData = getNotificationTemplateData();
		Long id = notificationModule.createNotificationTemplate(notificationTemplateData);

		NotificationTemplate fetchedNotificationTemplateData
				= notificationModule.findNotificationTemplate(id);
		assertEquals(notificationTemplateData, fetchedNotificationTemplateData);
	}

	@Test
	public void testFindNotificationTemplates() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationTemplate notificationTemplateData = getNotificationTemplateData();
		Long id = notificationModule.createNotificationTemplate(notificationTemplateData);

		NotificationTemplate notificationTemplate1
				= notificationModule.findNotificationTemplate(id);

		NotificationTemplateQuery query = new NotificationTemplateQuery();
		query.moduleName(SystemModuleNameConstants.SYSTEM_MODULE);
		query.name("ACC-SMRY-001");
		List<NotificationTemplate> outwardNotificationTemplateList
				= notificationModule.findNotificationTemplates(query);
		assertNotNull(outwardNotificationTemplateList);
		assertEquals(1, outwardNotificationTemplateList.size());
		NotificationTemplate notificationTemplate2 = outwardNotificationTemplateList.get(0);
		assertEquals(notificationTemplate1.getModuleId(), notificationTemplate2.getModuleId());
		assertEquals(notificationTemplate1.getName(), notificationTemplate2.getName());
		assertEquals(notificationTemplate1.getDescription(),
				notificationTemplate2.getDescription());
		assertEquals(notificationTemplate1.getSubject(), notificationTemplate2.getSubject());
		assertNull(notificationTemplate2.getTemplate());
	}

	@Test
	public void testUpdateNotificationTemplate() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationTemplate notificationTemplateData = getNotificationTemplateData();
		Long id = notificationModule.createNotificationTemplate(notificationTemplateData);

		NotificationTemplate fetchedNotificationTemplateData
				= notificationModule.findNotificationTemplate(id);
		fetchedNotificationTemplateData.setDescription("Online Credit Alert");
		fetchedNotificationTemplateData.setSubject("Credit Alert");
		int count = notificationModule.updateNotificationTemplate(fetchedNotificationTemplateData);
		assertEquals(1, count);

		NotificationTemplate updatedNotificationTemplateData
				= notificationModule.findNotificationTemplate(id);
		assertFalse(fetchedNotificationTemplateData.equals(notificationTemplateData));
		assertEquals(fetchedNotificationTemplateData, updatedNotificationTemplateData);
	}

	@Test
	public void testDeleteNotificationTemplate() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		NotificationTemplateQuery query = new NotificationTemplateQuery();
		query.moduleName("test");

		NotificationTemplate notificationTemplateData = getNotificationTemplateData();
		Long id = notificationModule.createNotificationTemplate(notificationTemplateData);

		int count = notificationModule.deleteNotificationTemplate(id);
		assertEquals(1, count);
	}

	@Test
	public void testSendNotification() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		notificationModule.createNotificationChannel(getNotificationChannelData());
		notificationModule.createNotificationTemplate(getNotificationTemplateData());

		com.tcdng.jacklyn.notification.data.Message notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("info", "info@tcdng.com")
						.toRecipient("Altair", "altair.assassins@creed.com")
						.usingDictionaryEntry("balance", Double.valueOf(25000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);
	}

	@Test
	public void testFindNotifications() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		notificationModule.createNotificationChannel(getNotificationChannelData());
		notificationModule.createNotificationTemplate(getNotificationTemplateData());

		com.tcdng.jacklyn.notification.data.Message notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("info", "info@tcdng.com")
						.toRecipient("Altair", "altair.assassins@creed.com")
						.usingDictionaryEntry("balance", Double.valueOf(25000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);

		notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("info", "info@tcdng.com")
						.toRecipient("Tom and Jerry", "tom.jerry@cartoons.com")
						.usingDictionaryEntry("balance", Double.valueOf(542000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);

		NotificationQuery query = new NotificationQuery();
		query.moduleName(SystemModuleNameConstants.SYSTEM_MODULE);
		query.notificationTemplateName("ACC-SMRY-001");
		query.orderById();
		List<Notification> outwardMessageList = notificationModule.findNotifications(query);
		assertNotNull(outwardMessageList);
		assertEquals(2, outwardMessageList.size());

		Notification messageData = outwardMessageList.get(0);
		assertEquals("info", messageData.getSenderName());
		assertEquals("info@tcdng.com", messageData.getSenderContact());
		assertEquals(Integer.valueOf(0), messageData.getAttempts());
		assertNotNull(messageData.getDueDt());
		assertNull(messageData.getSentDt());
		assertEquals(NotificationStatus.NOT_SENT, messageData.getStatus());

		messageData = outwardMessageList.get(1);
		assertEquals("info", messageData.getSenderName());
		assertEquals("info@tcdng.com", messageData.getSenderContact());
		assertEquals(Integer.valueOf(0), messageData.getAttempts());
		assertNotNull(messageData.getDueDt());
		assertNull(messageData.getSentDt());
		assertEquals(NotificationStatus.NOT_SENT, messageData.getStatus());
	}

	@Test
	public void testFindNotification() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		notificationModule.createNotificationChannel(getNotificationChannelData());
		notificationModule.createNotificationTemplate(getNotificationTemplateData());

		com.tcdng.jacklyn.notification.data.Message notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("info", "info@tcdng.com")
						.toRecipient("Altair", "altair.assassins@creed.com")
						.usingDictionaryEntry("balance", Double.valueOf(25000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);

		NotificationQuery query = new NotificationQuery();
		query.moduleName(SystemModuleNameConstants.SYSTEM_MODULE);
		query.notificationTemplateName("ACC-SMRY-001");
		query.orderById();
		List<Notification> outwardMessageList = notificationModule.findNotifications(query);

		Notification messageData
				= notificationModule.findNotification(outwardMessageList.get(0).getId());
		assertEquals("info", messageData.getSenderName());
		assertEquals("info@tcdng.com", messageData.getSenderContact());
		assertEquals(Integer.valueOf(0), messageData.getAttempts());
		assertNotNull(messageData.getDueDt());
		assertNull(messageData.getSentDt());
		assertEquals(NotificationStatus.NOT_SENT, messageData.getStatus());
	}

	@Test
	public void testSetNotificationStatus() throws Exception {
		NotificationModule notificationModule = getNotificationModule();
		notificationModule.createNotificationChannel(getNotificationChannelData());
		notificationModule.createNotificationTemplate(getNotificationTemplateData());

		com.tcdng.jacklyn.notification.data.Message notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("info", "info@tcdng.com")
						.toRecipient("Altair", "altair.assassins@creed.com")
						.usingDictionaryEntry("balance", Double.valueOf(25000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);

		notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("info", "info@tcdng.com")
						.toRecipient("Tom and Jerry", "tom.jerry@cartoons.com")
						.usingDictionaryEntry("balance", Double.valueOf(542000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);

		notification
				= new com.tcdng.jacklyn.notification.data.Message.Builder(NOTIFICATION_TEMPLATE_NAME)
						.fromSender("Edward Banfa", "edward.banfa@gmail.com")
						.toRecipient("Ernie", "ernie@seasamestreet.com")
						.usingDictionaryEntry("balance", Double.valueOf(542000.00))
						.sendVia(NOTIFICATION_CHANNEL_NAME).build();
		notificationModule.sendNotification(notification);

		NotificationQuery query = new NotificationQuery();
		query.moduleName(SystemModuleNameConstants.SYSTEM_MODULE);
		query.notificationTemplateName("ACC-SMRY-001");
		query.orderById();
		List<Notification> outwardMessageList = notificationModule.findNotifications(query);
		List<Long> outwardMessageIds = new ArrayList<Long>();
		outwardMessageIds.add(outwardMessageList.get(0).getId());
		outwardMessageIds.add(outwardMessageList.get(2).getId());

		int count = notificationModule.setNotificationStatus(outwardMessageIds,
				NotificationStatus.SENT);
		assertEquals(2, count);

		outwardMessageList = notificationModule.findNotifications(query);
		assertEquals(NotificationStatus.SENT, outwardMessageList.get(0).getStatus());
		assertEquals(NotificationStatus.NOT_SENT, outwardMessageList.get(1).getStatus());
		assertEquals(NotificationStatus.SENT, outwardMessageList.get(2).getStatus());
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onTearDown() throws Exception {
		deleteAll(NotificationRecipient.class, Notification.class, NotificationTemplate.class,
				NotificationChannel.class);
	}

	private NotificationModule getNotificationModule() throws Exception {
		NotificationModule notificationModule = (NotificationModule) getComponent(
				NotificationModuleNameConstants.NOTIFICATIONBUSINESSMODULE);
		notificationModule.clearCache();
		return notificationModule;
	}

	private NotificationChannel getNotificationChannelData() throws Exception {
		NotificationChannel notificationChannelData = new NotificationChannel();
		notificationChannelData.setName(NOTIFICATION_CHANNEL_NAME);
		notificationChannelData.setDescription("Message Messaging Channel");
		notificationChannelData.setNotificationType(NotificationType.SYSTEM);
		return notificationChannelData;
	}

	private NotificationTemplate getNotificationTemplateData() throws Exception {
		SystemModule systemModule
				= (SystemModule) getComponent(SystemModuleNameConstants.SYSTEMBUSINESSMODULE);
		Long moduleId = systemModule.getModuleId(SystemModuleNameConstants.SYSTEM_MODULE);

		NotificationTemplate notificationTemplateData = new NotificationTemplate();
		notificationTemplateData.setModuleId(moduleId);
		notificationTemplateData.setName("ACC-SMRY-001");
		notificationTemplateData.setDescription("Monthly Account Summary");
		notificationTemplateData.setSubject("Account Summary");
		notificationTemplateData.setTemplate("Your balance is {balance}");
		notificationTemplateData.setHtmlFlag(Boolean.FALSE);
		notificationTemplateData.setStatus(RecordStatus.ACTIVE);
		return notificationTemplateData;
	}
}
