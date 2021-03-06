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

import java.io.File;

import com.ning.billing.catalog.api.ICatalog;
import com.ning.billing.catalog.api.ICatalogUserApi;
import com.ning.billing.catalog.io.XMLReader;

public class CatalogUserApi implements ICatalogUserApi {

    @Override
    public ICatalog getCatalog(final String catalogName) {
    	String name = catalogName; 
        try {
            return XMLReader.getCatalogFromName(new File(name).toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
