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
package com.tcdng.jacklyn.shared.xml.config.module;

import java.util.ArrayList;
import java.util.List;

/**
 * Managed record configuration.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public class ManagedConfig {

    private String type;

    private List<FieldConfig> fieldList;

    public ManagedConfig() {
        this.fieldList = new ArrayList<FieldConfig>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FieldConfig> getFieldList() {
        return fieldList;
    }

    public void addFieldConfig(FieldConfig fieldConfig) {
        this.fieldList.add(fieldConfig);
    }
}
