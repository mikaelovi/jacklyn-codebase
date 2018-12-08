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
package com.tcdng.jacklyn.file.lists;

import com.tcdng.jacklyn.file.business.FileTransferPolicy;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.list.AbstractTypeListCommand;

/**
 * List command for file transfer policies.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("filetransferpolicylist")
public class FilePolicyListCommand extends AbstractTypeListCommand<FileTransferPolicy> {

	public FilePolicyListCommand() {
		super(FileTransferPolicy.class);
	}
}