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

package com.tcdng.jacklyn.shared;

import com.tcdng.jacklyn.shared.constants.DimensionType;

/**
 * Sharesd module utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class SharedUtils {

    private SharedUtils() {
        
    }
    
    public static String getDimensionString(Integer quantity, DimensionType dimensionType) {
        if (quantity != null && dimensionType != null) {
            return String.valueOf(quantity) + dimensionType.symbol();
        }
        
        return "";
    }
}
