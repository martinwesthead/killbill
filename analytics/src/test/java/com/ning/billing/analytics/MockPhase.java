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

package com.ning.billing.analytics;

import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.Currency;
import com.ning.billing.catalog.api.IDuration;
import com.ning.billing.catalog.api.IInternationalPrice;
import com.ning.billing.catalog.api.IPlan;
import com.ning.billing.catalog.api.IPlanPhase;
import com.ning.billing.catalog.api.IPrice;
import com.ning.billing.catalog.api.PhaseType;

import java.math.BigDecimal;

public class MockPhase implements IPlanPhase
{
    private final PhaseType cohort;
    private final IPlan plan;
    private final IDuration duration;
    private final double price;

    public MockPhase(final PhaseType cohort, final IPlan plan, final IDuration duration, final double price)
    {
        this.cohort = cohort;
        this.plan = plan;
        this.duration = duration;
        this.price = price;
    }

    @Override
    public IInternationalPrice getRecurringPrice()
    {
        return new IInternationalPrice()
        {
            @Override
            public IPrice[] getPrices()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public BigDecimal getPrice(final Currency currency)
            {
                return BigDecimal.valueOf(price);
            }
        };
    }

    @Override
    public IInternationalPrice getFixedPrice()
    {
        return new IInternationalPrice()
        {
            @Override
            public IPrice[] getPrices()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public BigDecimal getPrice(final Currency currency)
            {
                return BigDecimal.valueOf(price);
            }
        };
    }

    @Override
    public BillingPeriod getBillingPeriod()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName()
    {
        return plan.getName() + "-" + cohort;
    }

    @Override
    public IPlan getPlan()
    {
        return plan;
    }

    @Override
    public IDuration getDuration()
    {
        return duration;
    }

    @Override
    public PhaseType getPhaseType()
    {
        return cohort;
    }
}
