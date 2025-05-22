package com.example.home_page;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class settingsTest {

    @Rule
    public ActivityScenarioRule<settings> activityScenarioRule = new ActivityScenarioRule<>(settings.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void darkModeTest() {
        onView(withId(R.id.darkModeButton)).perform(click());
    }

    @Test
    public void thumbnailTest() {
        onView(withId(R.id.cameraIcon)).perform(click());

        // Verify that an intent with the action PICK is fired
        intended(hasAction("android.intent.action.PICK"));
    }

    @Test
    public void homebuttonTest() {
        onView(withId(R.id.homeButton)).perform(click());
    }
}
