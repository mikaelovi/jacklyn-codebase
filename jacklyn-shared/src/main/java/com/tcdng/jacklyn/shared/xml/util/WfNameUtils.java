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
package com.tcdng.jacklyn.shared.xml.util;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.PackableDoc;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.StringUtils.StringToken;

/**
 * Workflow name utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class WfNameUtils {

    private static FactoryMap<String, DocNameParts> docNames;

    private static FactoryMap<String, TemplateNameParts> templateNames;

    private static FactoryMap<String, ProcessNameParts> processNames;

    private static FactoryMap<String, StepNameParts> stepNames;

    static {
        docNames = new FactoryMap<String, DocNameParts>() {

            @Override
            protected DocNameParts create(String globalName, Object... params) throws Exception {
                String[] names = StringUtils.dotSplit(globalName);
                return new DocNameParts(names[0], names[1]);
            }

        };

        templateNames = new FactoryMap<String, TemplateNameParts>() {

            @Override
            protected TemplateNameParts create(String globalName, Object... params) throws Exception {
                String[] names = StringUtils.dotSplit(globalName);
                return new TemplateNameParts(names[0], names[1]);
            }

        };

        processNames = new FactoryMap<String, ProcessNameParts>() {

            @Override
            protected ProcessNameParts create(String globalName, Object... params) throws Exception {
                String[] names = StringUtils.dotSplit(globalName);
                return new ProcessNameParts(names[0], names[1], names[2], getTemplateGlobalName(names[0], names[1]),
                        getDocGlobalName(names[0], names[2]));
            }

        };

        stepNames = new FactoryMap<String, StepNameParts>() {

            @Override
            protected StepNameParts create(String globalName, Object... params) throws Exception {
                String[] names = StringUtils.dotSplit(globalName);
                return new StepNameParts(names[0], names[1], getTemplateGlobalName(names[0], names[1]), names[2]);
            }

        };
    }

    private WfNameUtils() {

    }

    public static boolean isValidName(String name) {
        return StringUtils.isNotBlank(name) && !StringUtils.containsWhitespace(name);
    }

    public static String getDocGlobalName(String categoryName, String docName) {
        return StringUtils.dotify(categoryName, docName);
    }

    public static String getTemplateGlobalName(String categoryName, String templateName) {
        return StringUtils.dotify(categoryName, templateName);
    }

    public static String getMessageGlobalName(String categoryName, String messageName) {
        return StringUtils.dotify(categoryName, messageName);
    }

    public static String getProcessGlobalName(String categoryName, String templateName, String docName) {
        return StringUtils.dotify(categoryName, templateName, docName);
    }

    public static String getStepGlobalName(String categoryName, String templateName, String stepName) {
        return StringUtils.dotify(categoryName, templateName, stepName);
    }

    public static DocNameParts getDocNameParts(String globalName) throws UnifyException {
        return docNames.get(globalName);
    }

    public static TemplateNameParts getTemplateNameParts(String globalName) throws UnifyException {
        return templateNames.get(globalName);
    }

    public static ProcessNameParts getProcessNameParts(String globalName) throws UnifyException {
        return processNames.get(globalName);
    }

    public static StepNameParts getStepNameParts(String globalName) throws UnifyException {
        return stepNames.get(globalName);
    }

    public static String describe(List<StringToken> itemDescFormat, PackableDoc packableDoc) throws UnifyException {
        if (itemDescFormat.isEmpty()) {
            return DataUtils.EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder();
        for (StringToken stringToken : itemDescFormat) {
            if (stringToken.isParam()) {
                sb.append(packableDoc.read(String.class, stringToken.getToken()));
            } else {
                sb.append(stringToken.getToken());
            }
        }

        return sb.toString();
    }

    public static class DocNameParts {

        private String categoryName;

        private String docName;

        public DocNameParts(String categoryName, String docName) {
            this.categoryName = categoryName;
            this.docName = docName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getDocName() {
            return docName;
        }

    }

    public static class FormNameParts {

        private String categoryName;

        private String formName;

        public FormNameParts(String categoryName, String formName) {
            this.categoryName = categoryName;
            this.formName = formName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getFormName() {
            return formName;
        }

    }

    public static class TemplateNameParts {

        private String categoryName;

        private String templateName;

        public TemplateNameParts(String categoryName, String templateName) {
            this.categoryName = categoryName;
            this.templateName = templateName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    public static class ProcessNameParts {

        private String categoryName;

        private String templateName;

        private String docName;

        private String templateGlobalName;

        private String docGlobalName;

        public ProcessNameParts(String categoryName, String templateName, String docName, String templateGlobalName,
                String docGlobalName) {
            this.categoryName = categoryName;
            this.templateName = templateName;
            this.docName = docName;
            this.templateGlobalName = templateGlobalName;
            this.docGlobalName = docGlobalName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getTemplateName() {
            return templateName;
        }

        public String getDocName() {
            return docName;
        }

        public String getTemplateGlobalName() {
            return templateGlobalName;
        }

        public String getDocGlobalName() {
            return docGlobalName;
        }

    }

    public static class StepNameParts {

        private String categoryName;

        private String templateName;

        private String templateGlobalName;

        private String stepName;

        public StepNameParts(String categoryName, String templateName, String templateGlobalName, String stepName) {
            this.categoryName = categoryName;
            this.templateName = templateName;
            this.templateGlobalName = templateGlobalName;
            this.stepName = stepName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getTemplateName() {
            return templateName;
        }

        public String getTemplateGlobalName() {
            return templateGlobalName;
        }

        public String getStepName() {
            return stepName;
        }

    }
}
