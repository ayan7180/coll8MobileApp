package com.example.home_page;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class createAnnounceTest {

    private static final String TEST_ABSTRACT = "Test Announcement Title";
    private static final String TEST_BODY = "Test Announcement Body";

    @Rule
    public ActivityScenarioRule<createAnnounceActivity> activityScenarioRule =
            new ActivityScenarioRule<>(createAnnounceActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.Abstract_Input)).check(matches(isDisplayed()));
        onView(withId(R.id.body_Input)).check(matches(isDisplayed()));
        onView(withId(R.id.create_announcebtn)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateAnnouncementButtonNavigatesToGenAnnounceActivity() {
        onView(withId(R.id.Abstract_Input)).perform(typeText(TEST_ABSTRACT));
        onView(withId(R.id.body_Input)).perform(typeText(TEST_BODY));

        onView(withId(R.id.create_announcebtn)).perform(click());

        intended(hasComponent(genAnnounceActivity.class.getName()));
    }

    @Test
    public void testHomeButtonNavigatesToMainActivity() {
        onView(withId(R.id.homeButton)).perform(click());

        intended(hasComponent(mainActivity.class.getName()));
    }
}
