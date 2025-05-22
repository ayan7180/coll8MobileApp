package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

    @RunWith(AndroidJUnit4ClassRunner.class)
    @LargeTest
    public class announceDetailsTest {

        private static final String TEST_ANNOUNCEMENT_TEXT = "This is a test announcement.";
        private static final String TEST_ANNOUNCEMENT_ID = "12345";

        @Rule
        public ActivityScenarioRule<announceDetailsActivity> activityScenarioRule =
                new ActivityScenarioRule<>(announceDetailsActivity.class);

        @Before
        public void setUp() {
            Intents.init();
        }

        @After
        public void tearDown() {
            Intents.release();
        }

        @Test
        public void testAnnouncementTextDisplayed() {
            Intent intent = new Intent();
            intent.putExtra("announcement_text", TEST_ANNOUNCEMENT_TEXT);
            intent.putExtra("id", TEST_ANNOUNCEMENT_ID);
            ActivityScenario.launch(announceDetailsActivity.class, intent.getExtras());

            // Check that the TextView displays the announcement text
            onView(withId(R.id.announce_detailstxt)).check(matches(withText(TEST_ANNOUNCEMENT_TEXT)));
        }

        @Test
        public void testHomeButtonNavigatesToHomeActivity() {
            // Launch the activity with test data in the Intent
            Intent intent = new Intent();
            intent.putExtra("announcement_text", TEST_ANNOUNCEMENT_TEXT);
            intent.putExtra("id", TEST_ANNOUNCEMENT_ID);
            ActivityScenario.launch(announceDetailsActivity.class, intent.getExtras());

            onView(withId(R.id.homeButton)).perform(click());

            intended(hasComponent(home_classes.class.getName()));
        }

        @Test
        public void testUnderstoodButtonNavigatesToGenAnnounceActivity() {
            Intent intent = new Intent();
            intent.putExtra("announcement_text", TEST_ANNOUNCEMENT_TEXT);
            intent.putExtra("id", TEST_ANNOUNCEMENT_ID);
            ActivityScenario.launch(                                              announceDetailsActivity.class, intent.getExtras());

            onView(withId(R.id.finish_announcebtn)).perform(click());

            intended(hasComponent(genAnnounceActivity.class.getName()));
        }

        @Test
        public void testUpdateButtonNavigatesToUpdateAnnounceActivity() {
            Intent intent = new Intent();
            intent.putExtra("announcement_text", TEST_ANNOUNCEMENT_TEXT);
            intent.putExtra("id", TEST_ANNOUNCEMENT_ID);
            ActivityScenario.launch(announceDetailsActivity.class, intent.getExtras());

            onView(withId(R.id.updatebtn)).perform(click());

            intended(hasComponent(updateAnnounceActivity.class.getName()));
        }

        @Test
        public void testDeleteButtonTriggersAnnouncementDeletion() {
            Intent intent = new Intent();
            intent.putExtra("announcement_text", TEST_ANNOUNCEMENT_TEXT);
            intent.putExtra("id", TEST_ANNOUNCEMENT_ID);
            ActivityScenario.launch(announceDetailsActivity.class, intent.getExtras());

            onView(withId(R.id.delete_announce)).perform(click());

            intended(hasComponent(genAnnounceActivity.class.getName()));
        }
    }

