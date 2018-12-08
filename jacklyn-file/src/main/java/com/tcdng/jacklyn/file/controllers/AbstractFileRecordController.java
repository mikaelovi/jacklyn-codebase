/*
 * Copyright 2018 The Code Department
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
package com.tcdng.jacklyn.file.controllers;

import com.tcdng.jacklyn.common.controllers.ManageRecordController;
import com.tcdng.jacklyn.file.business.FileModule;
import com.tcdng.jacklyn.file.constants.FileModuleNameConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.Entity;

/**
 * Convenient abstract file module record management page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractFileRecordController<T extends Entity>
		extends ManageRecordController<T, Long> {

	@Configurable(FileModuleNameConstants.FILEBUSINESSMODULE)
	private FileModule fileModule;

	public AbstractFileRecordController(Class<T> entityClass, String hintKey, int modifier) {
		super(entityClass, hintKey, modifier);
	}

	protected FileModule getFileModule() throws UnifyException {
		return fileModule;
	}
}