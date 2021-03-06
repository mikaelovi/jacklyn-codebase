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
package com.tcdng.jacklyn.common.entities;

import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.annotation.Version;

/**
 * Base class for entity that require a version number and a time stamp.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Tooling(name = "baseVersionTStmpEntity", description = "Base Versioned Timestamped")
@Policy("versionedtimestampedentity-policy")
public abstract class BaseVersionedTimestampedEntity extends BaseTimestampedEntity {

    @Version
    private long versionNo;

    public long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(long versionNo) {
        this.versionNo = versionNo;
    }
}
