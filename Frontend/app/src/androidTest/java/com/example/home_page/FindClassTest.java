package com.example.home_page;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FindClassTest {

    @Rule
    public ActivityScenarioRule<FindClassActivity> activityRule = new ActivityScenarioRule<>(FindClassActivity.class);

    @Test
    public void testUIElements() {
        // Check if the Home button is displayed
        onView(withId(R.id.homeButton)).check(matches(isDisplayed()));

        // Check if the Add Class button is displayed
        onView(withId(R.id.add_classbtn)).check(matches(isDisplayed()));

        // Check if the Search View is displayed
        onView(withId(R.id.search_class)).check(matches(isDisplayed()));

        // Check if the TextView for classes list is displayed
        onView(withId(R.id.getClassestxt)).check(matches(isDisplayed()));
    }

    @Test
    public void testHomeButtonFunctionality() {
        onView(withId(R.id.homeButton)).perform(click());

        onView(withId(R.id.homeB)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchFunctionality() {
        // Type the query into the SearchView
        onView(withId(R.id.search_class)).perform(ViewActions.typeText("CS 101"));

        // Perform click on the search button to submit the query
        onView(withId(androidx.appcompat.R.id.search_button)).perform(click());

        // Verify if the result is displayed in the TextView
        onView(withId(R.id.getClassestxt)).check(matches(withText("CS 101")));
    }


    @Test
    public void testInvalidSearch() {
        // Type the query into the SearchView
        onView(withId(R.id.search_class)).perform(ViewActions.typeText("CS"));

        // Perform click on the search button to submit the query
        onView(withId(androidx.appcompat.R.id.search_button)).perform(click());

        // Verify if the "Invalid search" Toast is displayed
        onView(withText("Invalid search"))
                .check(matches(isDisplayed()));  // Will check if the message is visible on the screen
    }

        @Test
        public void testAddClassButton() {
            onView(withId(R.id.add_classbtn)).perform(click());

            onView(withId(R.id.getClassestxt)).check(matches(isDisplayed()));
        }
}
