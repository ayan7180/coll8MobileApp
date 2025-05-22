package com.example.home_page;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.home_page.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class home_classesTest {



    private ActivityScenario<home_classes> scenario;

    @Before
    public void setUp() {

        UserSingleton.getInstance().setUserType("Admin");

        // Create an Intent similar to loginActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), home_classes.class);
        intent.putExtra("USERNAME_KEY", "testuser");
        intent.putExtra("PASSWORD_KEY", "password");


        ActivityScenario<home_classes> scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void testHomeClassesUI() {

        onView(withId(R.id.homeB)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void testUIElementsDisplayCorrectly() {
        // Verify the main UI elements are displayed
        onView(withId(R.id.homeB)).check(matches(isDisplayed()));
        onView(withId(R.id.clickAnn)).check(matches(isDisplayed()));
        onView(withId(R.id.addClasses)).check(matches(isDisplayed()));
        onView(withId(R.id.deleteClasses)).check(matches(isDisplayed()));
        onView(withId(R.id.classes_layout)).check(matches(isDisplayed()));
    }


    @Test
    public void testNavigationToUserPage() {

        onView(withId(R.id.userPageButton))
                .perform(ViewActions.click());

        // Verify a unique element in userPage is displayed
        onView(withId(R.id.homeBtn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddClassNavigation() {

        onView(withId(R.id.addClasses)).perform(ViewActions.click());

        // Verify the createClassActivity is launched
        onView(withId(R.id.homeButton))
                .check(matches(isDisplayed()));
    }




}

