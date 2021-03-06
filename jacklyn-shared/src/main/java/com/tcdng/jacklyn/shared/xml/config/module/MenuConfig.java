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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.tcdng.jacklyn.shared.xml.config.BaseConfig;

/**
 * MenuItemSet configuration.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public class MenuConfig extends BaseConfig {

    private String pageCaption;

    private String caption;

    private String path;

    private List<MenuItemConfig> menuItemList;

    public String getPageCaption() {
        return pageCaption;
    }

    @XmlAttribute
    public void setPageCaption(String pageCaption) {
        this.pageCaption = pageCaption;
    }

    public String getCaption() {
        return caption;
    }

    @XmlAttribute(required = true)
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPath() {
        return path;
    }

    @XmlAttribute
    public void setPath(String path) {
        this.path = path;
    }

    public List<MenuItemConfig> getMenuItemList() {
        return menuItemList;
    }

    @XmlElement(name = "menuitem", required = true)
    public void setMenuItemList(List<MenuItemConfig> menuItemList) {
        this.menuItemList = menuItemList;
    }
}
