/*
 * Copyright 2018-2019 The Code Department
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

package com.tcdng.jacklyn.notification.data;

/**
 * Notification contact.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NotificationContact {

    private String fullName;
    
    private String contact;

    public NotificationContact(String fullName, String contact) {
        this.fullName = fullName;
        this.contact = contact;
    }

    public String getFullName() {
        return fullName;
    }

    public String getContact() {
        return contact;
    }
    
}
