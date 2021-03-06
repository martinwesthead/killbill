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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.ning.billing.catalog.api.IPriceList;

@XmlAccessorType(XmlAccessType.NONE)
public class PriceList extends ValidatingConfig implements IPriceList {

	@XmlAttribute(required=true)
	@XmlID
	private String name;

	@XmlElement(required=false) 
	private Boolean isDefault = false;

	@XmlElementWrapper(name="plans", required=true)
	@XmlElement(name="plan", required=true)
	@XmlIDREF
    private Plan[] plans;

    /* (non-Javadoc)
	 * @see com.ning.billing.catalog.IPlanSet#getName()
	 */
    @Override
	public String getName() {
        return name;
    }

    /* (non-Javadoc)
	 * @see com.ning.billing.catalog.IPlanSet#getPlans()
	 */
    @Override
	public Plan[] getPlans() {
        return plans;
    }

    /* (non-Javadoc)
	 * @see com.ning.billing.catalog.IPlanSet#findPlanByProductName(java.lang.String)
	 */
    @Override
	public Plan findPlanByProductName(String productName) {
        for (Plan cur : plans) {
            if (cur.getProduct().getName().equals(productName)) {
                return cur;
            }
        }
        return null;
    }

	public void setName(String name) {
		this.name = name;
	}

	public void setPlans(Plan[] plans) {
		this.plans = plans;
	}

	/* (non-Javadoc)
	 * @see com.ning.billing.catalog.IPlanSet#getProductType()
	 */
	@Override
	public boolean isDefault() {
		return isDefault;
	}

	public void setProductType(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public ValidationErrors validate(Catalog catalog, ValidationErrors errors) {
		return errors;

	}

}
