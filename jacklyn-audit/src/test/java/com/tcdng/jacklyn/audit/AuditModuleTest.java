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
package com.tcdng.jacklyn.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.jacklyn.audit.business.AuditService;
import com.tcdng.jacklyn.audit.business.EventLoggerService;
import com.tcdng.jacklyn.audit.constants.AuditModuleNameConstants;
import com.tcdng.jacklyn.audit.entities.AuditDefinition;
import com.tcdng.jacklyn.audit.entities.AuditDefinitionQuery;
import com.tcdng.jacklyn.audit.entities.AuditDetail;
import com.tcdng.jacklyn.audit.entities.AuditTrail;
import com.tcdng.jacklyn.audit.entities.AuditTrailQuery;
import com.tcdng.jacklyn.common.AbstractJacklynTest;
import com.tcdng.jacklyn.common.constants.RecordStatus;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.logging.EventType;

/**
 * Audit business service tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class AuditModuleTest extends AbstractJacklynTest {

    @Test
    public void testFindAuditTypes() throws Exception {
        AuditService auditService = (AuditService) getComponent(AuditModuleNameConstants.AUDITSERVICE);
        AuditDefinitionQuery query = new AuditDefinitionQuery();
        query.moduleName("customer");
        List<AuditDefinition> auditTypeList = auditService.findAuditTypes(query);
        assertNotNull(auditTypeList);
        assertEquals(3 + 5, auditTypeList.size());

        query.clear();
        query.name("customer-search");
        query.moduleName("customer");
        auditTypeList = auditService.findAuditTypes(query);
        assertNotNull(auditTypeList);
        assertEquals(1, auditTypeList.size());
        AuditDefinition auditDefinition = auditTypeList.get(0);
        assertEquals("customer-search", auditDefinition.getName());
        assertEquals("Customer Search", auditDefinition.getDescription());
        assertNull(auditDefinition.getRecordName());
        assertEquals(EventType.SEARCH, auditDefinition.getEventType());
        assertEquals(RecordStatus.ACTIVE, auditDefinition.getStatus());
    }

    @Test
    public void testFindAuditType() throws Exception {
        AuditService auditService = (AuditService) getComponent(AuditModuleNameConstants.AUDITSERVICE);
        AuditDefinitionQuery query = new AuditDefinitionQuery();
        query.name("customer-search");
        query.moduleName("customer");
        AuditDefinition auditDefinition = auditService.findAuditType(query);
        auditDefinition = auditService.findAuditType(auditDefinition.getId());
        assertNotNull(auditDefinition);
        assertEquals("customer-search", auditDefinition.getName());
        assertEquals("Customer Search", auditDefinition.getDescription());
        assertNull(auditDefinition.getRecordName());
        assertEquals(EventType.SEARCH, auditDefinition.getEventType());
        assertEquals(RecordStatus.ACTIVE, auditDefinition.getStatus());
    }

    @Test
    public void testSetAuditTypeStatus() throws Exception {
        AuditService auditService = (AuditService) getComponent(AuditModuleNameConstants.AUDITSERVICE);
        AuditDefinitionQuery query = new AuditDefinitionQuery();
        query.nameIn(Arrays.asList(new String[] { "customer-create", "customer-view" }));
        query.moduleName("customer");
        auditService.setAuditTypeStatus(query, RecordStatus.INACTIVE);

        // Test statuses
        query.clear();
        query.name("customer-search");
        query.moduleName("customer");
        AuditDefinition auditDefinition = auditService.findAuditType(query);
        assertEquals(RecordStatus.ACTIVE, auditDefinition.getStatus());

        query.clear();
        query.name("customer-create");
        query.moduleName("customer");
        auditDefinition = auditService.findAuditType(query);
        assertEquals(RecordStatus.INACTIVE, auditDefinition.getStatus());

        query.clear();
        query.name("customer-view");
        query.moduleName("customer");
        auditDefinition = auditService.findAuditType(query);
        assertEquals(RecordStatus.INACTIVE, auditDefinition.getStatus());

        // Flip status back
        query.clear();
        query.name("customer-view");
        query.moduleName("customer");
        auditService.setAuditTypeStatus(query, RecordStatus.ACTIVE);

        // Test statuses
        query.clear();
        query.name("customer-search");
        query.moduleName("customer");
        auditDefinition = auditService.findAuditType(query);
        assertEquals(RecordStatus.ACTIVE, auditDefinition.getStatus());

        query.clear();
        query.name("customer-create");
        query.moduleName("customer");
        auditDefinition = auditService.findAuditType(query);
        assertEquals(RecordStatus.INACTIVE, auditDefinition.getStatus());

        query.clear();
        query.name("customer-view");
        query.moduleName("customer");
        auditDefinition = auditService.findAuditType(query);
        assertEquals(RecordStatus.ACTIVE, auditDefinition.getStatus());
    }

    @Test
    public void testFindAuditTrail() throws Exception {
        AuditService auditService = (AuditService) getComponent(AuditModuleNameConstants.AUDITSERVICE);
        // Update all statuses to active
        AuditDefinitionQuery query = new AuditDefinitionQuery();
        query.moduleName("customer");
        auditService.setAuditTypeStatus(query, RecordStatus.ACTIVE);

        EventLoggerService eventLoggerService = (EventLoggerService) this
                .getComponent(ApplicationComponents.APPLICATION_EVENTSLOGGER);

        boolean success = eventLoggerService.logUserEvent("customer-create");
        assertTrue(success);

        success = eventLoggerService.logUserEvent("customer-view");
        assertTrue(success);

        AuditTrailQuery trailQuery = new AuditTrailQuery();
        trailQuery.moduleName("customer");
        List<AuditTrail> auditTrailList = auditService.findAuditTrail(trailQuery);
        assertNotNull(auditTrailList);
        assertEquals(2, auditTrailList.size());

        AuditTrail auditTrail = auditService.findAuditTrail(auditTrailList.get(0).getId());
        assertNotNull(auditTrail);
    }

    @Test
    public void testFindAuditDetails() throws Exception {
        EventLoggerService eventLoggerService = (EventLoggerService) this
                .getComponent(ApplicationComponents.APPLICATION_EVENTSLOGGER);
        eventLoggerService.logUserEvent("customer-view", "On a Sunday", "By age");

        AuditService auditService = (AuditService) getComponent(AuditModuleNameConstants.AUDITSERVICE);
        AuditTrailQuery query = new AuditTrailQuery();
        query.moduleName("customer");
        query.auditDefinitionName("customer-view");
        List<AuditTrail> auditTrailList = auditService.findAuditTrail(query);
        assertNotNull(auditTrailList);

        Long auditTrailId = auditTrailList.get(0).getId();
        List<AuditDetail> auditDetailList = auditService.findAuditDetails(auditTrailId);
        assertNotNull(auditDetailList);
        assertEquals(2, auditDetailList.size());
        assertEquals("On a Sunday", auditDetailList.get(0).getDetail());
        assertEquals("By age", auditDetailList.get(1).getDetail());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onSetup() throws Exception {
        deleteAll(AuditDetail.class, AuditTrail.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(AuditDetail.class, AuditTrail.class);
    }
}
