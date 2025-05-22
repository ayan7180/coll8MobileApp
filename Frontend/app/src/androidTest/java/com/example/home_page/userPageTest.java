package com.example.home_page;

import androidx.test.espresso.intent.Intents;
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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class userPageTest {

    @Rule
    public ActivityScenarioRule<userPage> activityScenarioRule = new ActivityScenarioRule<>(userPage.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void settings(){
        onView(withId(R.id.settingsBtn)).perform(click());
        intended(hasComponent(settings.class.getName()));
    }

    @Test
    public void classLib(){
        onView(withId(R.id.classLibBtn)).perform(click());
        intended(hasComponent(classLib.class.getName()));
    }

    @Test
    public void peronalFileRepo(){
        onView(withId(R.id.personalFileRepoBtn)).perform(click());
        intended(hasComponent(personalFileRepoActivity.class.getName()));
    }



    @Test
    public void classNews(){
        onView(withId(R.id.classNewsButton)).perform(click());
        intended(hasComponent(classNews.class.getName()));
    }

    @Test
    public void calendar(){
        onView(withId(R.id.calendarButton)).perform(click());
        intended(hasComponent(CalendarActivity.class.getName()));
    }

    @Test
    public void classArchive(){
        onView(withId(R.id.classArchive)).perform(click());
        intended(hasComponent(classArchiveActivity.class.getName()));
    }


}
