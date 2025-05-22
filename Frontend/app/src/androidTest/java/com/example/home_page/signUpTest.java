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


public class signUpTest {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

    @Test
    public void testSignupPasswordMismatch() {
        // Type username, password, and mismatched confirm password
        onView(withId(R.id.username_input)).perform(typeText("newUser"), closeSoftKeyboard());
        onView(withId(R.id.password_input)).perform(typeText("newPassword"), closeSoftKeyboard());
        onView(withId(R.id.re_enter_pass)).perform(typeText("wrongPassword"), closeSoftKeyboard());


        onView(withId(R.id.login_btn)).perform(click());


        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}


    }

    @Test
    public void testSignupValidDetails() {
        // Type username, password, and confirm password
        onView(withId(R.id.username_input)).perform(typeText("newUser"), closeSoftKeyboard());
        onView(withId(R.id.password_input)).perform(typeText("newPassword"), closeSoftKeyboard());
        onView(withId(R.id.re_enter_pass)).perform(typeText("newPassword"), closeSoftKeyboard());


        onView(withId(R.id.login_btn)).perform(click());

        // Simulate delay
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}


    }
}
