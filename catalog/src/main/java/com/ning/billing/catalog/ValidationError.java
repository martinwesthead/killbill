/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ning.billing.catalog;

import java.net.URL;

public class ValidationError {
	private final String description;
	private final String catalogURL;
	private final Class<? extends ValidatingConfig> objectType;
	private final String objectName;
	public ValidationError(String description, String catalogURL,
			Class<? extends ValidatingConfig> objectType, String objectName) {
		super();
		this.description = description;
		this.catalogURL = catalogURL;
		this.objectType = objectType;
		this.objectName = objectName;
	}
	public String getDescription() {
		return description;
	}
	public String getCatalogURL() {
		return catalogURL;
	}
	public Class<? extends ValidatingConfig> getObjectType() {
		return objectType;
	}
	public String getObjectName() {
		return objectName;
	}
	
	
}
