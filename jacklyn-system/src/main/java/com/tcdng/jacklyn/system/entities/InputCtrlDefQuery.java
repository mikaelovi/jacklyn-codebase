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
package com.tcdng.jacklyn.system.entities;

import com.tcdng.jacklyn.common.entities.BaseVersionedStatusEntityQuery;

/**
 * Query class for input control definition.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class InputCtrlDefQuery extends BaseVersionedStatusEntityQuery<InputCtrlDef> {

    public InputCtrlDefQuery() {
        super(InputCtrlDef.class);
    }

    public InputCtrlDefQuery moduleId(Long moduleId) {
        return (InputCtrlDefQuery) addEquals("moduleId", moduleId);
    }

    public InputCtrlDefQuery moduleName(String moduleName) {
        return (InputCtrlDefQuery) addEquals("moduleName", moduleName);
    }

    @Override
    public InputCtrlDefQuery addOrder(String field) {
        return (InputCtrlDefQuery) super.addOrder(field);
    }

    @Override
    public InputCtrlDefQuery addSelect(String field) {
        return (InputCtrlDefQuery) super.addSelect(field);
    }

    public InputCtrlDefQuery name(String name) {
        return (InputCtrlDefQuery) addEquals("name", name);
    }

    public InputCtrlDefQuery descriptionLike(String description) {
        return (InputCtrlDefQuery) addLike("description", description);
    }
}
