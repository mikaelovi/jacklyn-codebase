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

package com.tcdng.jacklyn.workflow.data;

import com.tcdng.jacklyn.shared.workflow.WorkflowRecordActionType;
import com.tcdng.unify.core.data.Document;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Workflow record action definition.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class WfRecordActionDef extends BaseWfDef {

    private String docName;

    private WorkflowRecordActionType actionType;

    private Class<? extends Document> beanClass;

    public WfRecordActionDef(String name, String description, String docName, WorkflowRecordActionType actionType,
            Class<? extends Document> beanClass) {
        super(name, description);
        this.docName = docName;
        this.actionType = actionType;
        this.beanClass = beanClass;
    }

    public String getDocName() {
        return docName;
    }

    public WorkflowRecordActionType getActionType() {
        return actionType;
    }
    
    public Class<? extends Document> getBeanClass() {
        return beanClass;
    }

    public boolean isDoc() {
        return StringUtils.isNotBlank(docName);
    }
}
