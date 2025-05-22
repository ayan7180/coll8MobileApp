package com.example.home_page;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;



@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class loginTest {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<loginAcivity> activityScenarioRule =
            new ActivityScenarioRule<>(loginAcivity.class);

    @Test
    public void testLoginValidCredentials() {
        // Type valid username and password
        onView(withId(R.id.username_input)).perform(typeText("Admin"), closeSoftKeyboard());
        onView(withId(R.id.password_input)).perform(typeText("Admin"), closeSoftKeyboard());


        onView(withId(R.id.login_btn)).perform(click());


        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}


    }

    @Test
    public void testLoginInvalidCredentials() {
        // Type invalid username and password
        onView(withId(R.id.username_input)).perform(typeText("invalidUser"), closeSoftKeyboard());
        onView(withId(R.id.password_input)).perform(typeText("invalidPassword"), closeSoftKeyboard());


        onView(withId(R.id.login_btn)).perform(click());


        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}


    }


}
