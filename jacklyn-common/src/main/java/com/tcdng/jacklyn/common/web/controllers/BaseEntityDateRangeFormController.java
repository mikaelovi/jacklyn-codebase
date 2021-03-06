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
package com.tcdng.jacklyn.common.web.controllers;

import java.util.Date;

import com.tcdng.jacklyn.common.web.beans.BaseEntityDateRangePageBean;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.data.MessageIcon;
import com.tcdng.unify.web.ui.data.MessageMode;

/**
 * Convenient abstract base class for form date range CRUD controllers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class BaseEntityDateRangeFormController<T extends BaseEntityDateRangePageBean<U>, U extends Entity>
        extends BaseEntityFormController<T, Long, U> {

    public BaseEntityDateRangeFormController(Class<T> pageBeanClass, Class<U> entityClass, int modifier) {
        super(pageBeanClass, entityClass, modifier);
    }

    @Action
    public String findRecords() throws UnifyException {
        if (ManageRecordModifier.isLimitDateRange(getModifier())) {
            BaseEntityDateRangePageBean<U> pageBean = getPageBean();
            Date searchFromDate = pageBean.getSearchFromDate();
            Date searchToDate = pageBean.getSearchToDate();
            int searchLimitDays = 0;
            if (searchFromDate == null || searchToDate == null || CalendarUtils.getDaysDifference(searchFromDate,
                    searchToDate) > (searchLimitDays = getNormalizedSearchDateRangeLimitDays())) {
                return showMessageBox(MessageIcon.ERROR, MessageMode.OK,
                        this.resolveSessionMessage("$m{managerecord.hint.daterangebeyondlimit}", searchLimitDays));
            }
        }

        return super.findRecords();
    }

    @Override
    protected void onOpenPage() throws UnifyException {
        super.onOpenPage();

        if (ManageRecordModifier.isInitDateRange(getModifier())) {
            BaseEntityDateRangePageBean<U> pageBean = getPageBean();
            if (pageBean.getSearchFromDate() == null) {
                Date now = CalendarUtils.getMidnightDate(new Date());
                pageBean.setSearchFromDate(now);
                pageBean.setSearchToDate(now);
            }
        }
    }

    protected abstract int getSearchDateRangeLimitDays() throws UnifyException;

    private int getNormalizedSearchDateRangeLimitDays() throws UnifyException {
        int searchLimitDays = getSearchDateRangeLimitDays();
        if (searchLimitDays <= 0) {
            return 1;
        }

        return searchLimitDays;
    }
}
