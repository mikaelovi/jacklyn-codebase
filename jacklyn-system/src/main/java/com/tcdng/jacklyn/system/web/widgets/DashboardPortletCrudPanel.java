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

package com.tcdng.jacklyn.system.web.widgets;

import com.tcdng.jacklyn.system.entities.DashboardPortlet;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.ui.panel.AbstractInMemoryTableCrudPanel;

/**
 * Dashboard portlet CRUD panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-dashboardportletcrudpanel")
@UplBinding("web/system/upl/dashboardportletcrudpanel.upl")
public class DashboardPortletCrudPanel extends AbstractInMemoryTableCrudPanel<DashboardPortlet> {

    public DashboardPortletCrudPanel() {
        super(DashboardPortlet.class, "$m{system.dashboard.dashboardportlet}", false);
    }
}
