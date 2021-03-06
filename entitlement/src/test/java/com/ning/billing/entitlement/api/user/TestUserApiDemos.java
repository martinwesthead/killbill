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

package com.ning.billing.entitlement.api.user;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.IDuration;
import com.ning.billing.catalog.api.IPlan;
import com.ning.billing.catalog.api.IPlanPhase;
import com.ning.billing.catalog.api.PhaseType;
import com.ning.billing.catalog.api.ProductCategory;
import com.ning.billing.entitlement.api.ApiTestListener.NextEvent;
import com.ning.billing.entitlement.glue.EngineModuleSqlMock;
import com.ning.billing.util.clock.Clock;

public class TestUserApiDemos extends TestUserApiBase {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT, new EngineModuleSqlMock());
    }

    /**
     *  Initial demo for BP entitlement:
     *  1. Create a Subscription
     *  2. ChangePlan while in trial
     *     -> Change is IMM
     *     -> Trial is still 30 days long
     *  3. Move through 2nd Phase
     *  4. ChangePlan EOT
     *     -> Show Change pending
     *  5. Other ChangePlan EOT
     *     -> Show it supercedes the first one
     *  6. Move to EOT
     *  7. Move to next Phase
     *  8. Cancel EOT
     */
    @Test(enabled=true, groups="demos")
    public void testDemo1() {

        try {
            System.out.println("DEMO 1 START");

            /* STEP 1. CREATE SUBSCRIPTION */
            Subscription subscription = createSubscription("Assault-Rifle", BillingPeriod.MONTHLY, "gunclubDiscount");
            IPlanPhase trialPhase = subscription.getCurrentPhase();
            assertEquals(trialPhase.getPhaseType(), PhaseType.TRIAL);

            displayState(subscription.getId(), "STEP 1. CREATED SUBSCRIPTION");

            /* STEP 2. CHANGE PLAN WHILE IN TRIAL */
            testListener.pushExpectedEvent(NextEvent.CHANGE);
            subscription.changePlan("Assault-Rifle", BillingPeriod.ANNUAL, "gunclubDiscount");
            assertTrue(testListener.isCompleted(3000));

            displayState(subscription.getId(), "STEP 2. CHANGED PLAN WHILE IN TRIAL");

            /* STEP 3. MOVE TO DISCOUNT PHASE */
            testListener.pushExpectedEvent(NextEvent.PHASE);
            clock.setDeltaFromReality(trialPhase.getDuration(), DAY_IN_MS);
            assertTrue(testListener.isCompleted(3000));

            displayState(subscription.getId(), "STEP 3. MOVE TO DISCOUNT PHASE");

            /* STEP 4. SET CTD AND CHANGE PLAN EOT */
            List<IDuration> durationList = new ArrayList<IDuration>();
            durationList.add(trialPhase.getDuration());
            DateTime startDiscountPhase = Clock.addDuration(subscription.getStartDate(), durationList);

            IDuration ctd = getDurationMonth(1);
            DateTime newChargedThroughDate = Clock.addDuration(startDiscountPhase, ctd);
            billingApi.setChargedThroughDate(subscription.getId(), newChargedThroughDate);
            subscription = (Subscription) entitlementApi.getSubscriptionFromId(subscription.getId());

            testListener.pushExpectedEvent(NextEvent.CHANGE);
            subscription.changePlan("Shotgun", BillingPeriod.ANNUAL, "gunclubDiscount");
            assertFalse(testListener.isCompleted(2000));
            testListener.reset();

            displayState(subscription.getId(), "STEP 4. SET CTD AND CHANGE PLAN EOT (Shotgun)");

            /* STEP 5. CHANGE AGAIN */
            testListener.pushExpectedEvent(NextEvent.CHANGE);
            subscription.changePlan("Pistol", BillingPeriod.ANNUAL, "gunclubDiscount");
            assertFalse(testListener.isCompleted(2000));
            testListener.reset();

            displayState(subscription.getId(), "STEP 5. CHANGE AGAIN EOT (Pistol)");

            /* STEP 6. MOVE TO EOT AND CHECK CHANGE OCCURED */
            testListener.pushExpectedEvent(NextEvent.CHANGE);
            clock.addDeltaFromReality(ctd);
            assertTrue(testListener.isCompleted(2000));

            IPlan currentPlan = subscription.getCurrentPlan();
            assertNotNull(currentPlan);
            assertEquals(currentPlan.getProduct().getName(), "Pistol");
            assertEquals(currentPlan.getProduct().getCategory(), ProductCategory.BASE);
            assertEquals(currentPlan.getBillingPeriod(), BillingPeriod.ANNUAL);

            IPlanPhase currentPhase = subscription.getCurrentPhase();
            assertNotNull(currentPhase);
            assertEquals(currentPhase.getPhaseType(), PhaseType.DISCOUNT);

            displayState(subscription.getId(), "STEP 6. MOVE TO EOT");

            /* STEP 7.  MOVE TO NEXT PHASE */
            testListener.pushExpectedEvent(NextEvent.PHASE);
            clock.addDeltaFromReality(currentPhase.getDuration());
            assertTrue(testListener.isCompleted(3000));
            subscription = (Subscription) entitlementApi.getSubscriptionFromId(subscription.getId());

            currentPlan = subscription.getCurrentPlan();
            assertNotNull(currentPlan);
            assertEquals(currentPlan.getProduct().getName(), "Pistol");
            assertEquals(currentPlan.getProduct().getCategory(), ProductCategory.BASE);
            assertEquals(currentPlan.getBillingPeriod(), BillingPeriod.ANNUAL);

            currentPhase = subscription.getCurrentPhase();
            assertNotNull(currentPhase);
            assertEquals(currentPhase.getPhaseType(), PhaseType.EVERGREEN);

            displayState(subscription.getId(), "STEP 7.  MOVE TO NEXT PHASE");

            /* STEP 8. CANCEL IMM (NO CTD) */
            testListener.pushExpectedEvent(NextEvent.CANCEL);
            subscription.cancel();

            displayState(subscription.getId(), "STEP 8.  CANCELLATION");

        } catch (EntitlementUserApiException e) {
            Assert.fail(e.getMessage());
        }

    }


    private void displayState(UUID subscriptionId, String stepMsg) {

        System.out.println("");
        System.out.println("******\t STEP " + stepMsg + " **************");

        Subscription subscription = (Subscription) entitlementApi.getSubscriptionFromId(subscriptionId);


        IPlan currentPlan = subscription.getCurrentPlan();
        IPlanPhase currentPhase = subscription.getCurrentPhase();
        String priceList = subscription.getCurrentPriceList();
        System.out.println("");
        System.out.println("\t CURRENT TIME = " + clock.getUTCNow());
        System.out.println("");
        System.out.println("\t CURRENT STATE = " +  subscription.getState());
        System.out.println("\t CURRENT PRODUCT = " +  ((currentPlan == null) ? "NONE" : currentPlan.getProduct().getName()));
        System.out.println("\t CURRENT TERM = " +  ((currentPlan == null) ? "NONE" : currentPlan.getBillingPeriod().toString()));
        System.out.println("\t CURRENT PHASE = " +  ((currentPhase == null) ? "NONE" : currentPhase.getPhaseType()));
        System.out.println("\t CURRENT PRICE LIST = " + ((priceList == null) ? "NONE" : priceList));
        System.out.println("\t CURRENT \'SLUG\' = " +  ((currentPhase == null) ? "NONE" : currentPhase.getName()));

        System.out.println("");

    }

    @Test(enabled= true, groups={"stress"})
    public void stressTest() {
        for (int i = 0; i < 100; i++) {
            cleanupTest();
            setupTest();
            testDemo1();
        }
    }
}
