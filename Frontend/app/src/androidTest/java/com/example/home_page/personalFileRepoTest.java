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

public class personalFileRepoTest {

    @Before
    public void setUp() {
        UserSingleton.getInstance().setUserType("User");
    }

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<personalFileRepoActivity> activityScenarioRule =
            new ActivityScenarioRule<>(personalFileRepoActivity.class);

    @Test
    public void fetchFiles(){
        onView(withId(R.id.fetchFilesButton)).perform(click());
    }

    @Test
    public void homeButton(){
        onView(withId(R.id.homeButton)).perform(click());
    }


}
