/*
 * Copyright 2018-2019 The Code Department.
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

package com.tcdng.jacklyn.workflow.entities;

import java.util.List;

import com.tcdng.jacklyn.common.entities.BaseEntity;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;

/**
 * Workflow document bean mapping definition entity.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "JKWFDOCBEANMAPPING", uniqueConstraints = { @UniqueConstraint({ "wfDocId", "name" }),
        @UniqueConstraint({ "wfDocId", "description" }) })
public class WfDocBeanMapping extends BaseEntity {

    @ForeignKey(WfDoc.class)
    private Long wfDocId;

    @Column(name = "BEANMAPPING_NM", length = 32)
    private String name;

    @Column(name = "BEANMAPPING_DESC", length = 64)
    private String description;

    @Column(name = "BEAN_TY", length = 128)
    private String beanType;

    @Column(name = "RECEPTACLE_MAPPING_FG")
    private Boolean receptacleMapping;

    @Column(name = "PRIMARY_MAPPING_FG")
    private Boolean primaryMapping;
    
    @ListOnly(key = "wfDocId", property = "name")
    private String wfDocName;

    @ListOnly(key = "wfDocId", property = "description")
    private String wfDocDesc;

    @ChildList
    private List<WfDocFieldMapping> fieldMappingList;

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getWfDocId() {
        return wfDocId;
    }

    public void setWfDocId(Long wfDocId) {
        this.wfDocId = wfDocId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeanType() {
        return beanType;
    }

    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    public Boolean getReceptacleMapping() {
        return receptacleMapping;
    }

    public void setReceptacleMapping(Boolean receptacleMapping) {
        this.receptacleMapping = receptacleMapping;
    }

    public Boolean getPrimaryMapping() {
        return primaryMapping;
    }

    public void setPrimaryMapping(Boolean primaryMapping) {
        this.primaryMapping = primaryMapping;
    }

    public String getWfDocName() {
        return wfDocName;
    }

    public void setWfDocName(String wfDocName) {
        this.wfDocName = wfDocName;
    }

    public String getWfDocDesc() {
        return wfDocDesc;
    }

    public void setWfDocDesc(String wfDocDesc) {
        this.wfDocDesc = wfDocDesc;
    }

    public List<WfDocFieldMapping> getFieldMappingList() {
        return fieldMappingList;
    }

    public void setFieldMappingList(List<WfDocFieldMapping> fieldMappingList) {
        this.fieldMappingList = fieldMappingList;
    }

}
