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
package com.tcdng.jacklyn.organization.entities;

import com.tcdng.jacklyn.common.entities.BaseVersionedStatusEntityQuery;

/**
 * Query class for hubs.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class HubQuery extends BaseVersionedStatusEntityQuery<Hub> {

    public HubQuery() {
        super(Hub.class);
    }

    public HubQuery name(String name) {
        return (HubQuery) addEquals("name", name);
    }

    public HubQuery nameLike(String name) {
        return (HubQuery) addLike("name", name);
    }

    public HubQuery descriptionLike(String description) {
        return (HubQuery) addLike("description", description);
    }
}
