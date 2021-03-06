/*
 * Copyright 2018-2020 The Code Department
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

package com.tcdng.jacklyn.shared.system.data;

import javax.xml.bind.annotation.XmlElement;

/**
 * Tooling entity field item.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ToolingEntityFieldItem {

    private String name;

    private String type;

    private boolean enumConst;

    public ToolingEntityFieldItem(String name, String type, boolean enumConst) {
        this.name = name;
        this.type = type;
        this.enumConst = enumConst;
    }

    public ToolingEntityFieldItem() {

    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    @XmlElement
    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnumConst() {
        return enumConst;
    }

    @XmlElement
    public void setEnumConst(boolean enumConst) {
        this.enumConst = enumConst;
    }

}
