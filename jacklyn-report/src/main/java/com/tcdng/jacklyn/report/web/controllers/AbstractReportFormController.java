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

package com.tcdng.jacklyn.report.web.controllers;

import com.tcdng.jacklyn.common.entities.BaseEntity;
import com.tcdng.jacklyn.common.web.beans.BaseEntityPageBean;
import com.tcdng.jacklyn.common.web.controllers.BaseEntityFormController;
import com.tcdng.jacklyn.report.business.ReportService;
import com.tcdng.unify.core.annotation.Configurable;

/**
 * Abstract base report module record management page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractReportFormController<T extends BaseEntityPageBean<U>, U extends BaseEntity>
        extends BaseEntityFormController<T, Long, U> {

    @Configurable
    private ReportService reportService;

    public AbstractReportFormController(Class<T> pageBeanClass, Class<U> entityClass, int modifier) {
        super(pageBeanClass, entityClass, modifier);
    }

    protected ReportService getReportService() {
        return reportService;
    }

}
