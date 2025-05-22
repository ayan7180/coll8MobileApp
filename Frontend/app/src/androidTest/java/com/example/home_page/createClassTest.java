package com.example.home_page;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.android.volley.VolleyError;
import android.view.View;
import android.widget.Toast;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import static androidx.test.espresso.intent.Intents.intended;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class createClassTest {

    private static final String TEST_ABBREVIATION = "CS";
    private static final String TEST_COURSE_NUMBER = "3090";
    private static final String TEST_TITLE = "Mobile App Development";

    @Rule
    public ActivityScenarioRule<createClassActivity> activityScenarioRule =
            new ActivityScenarioRule<>(createClassActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testUIElements() {
        onView(withId(R.id.classification_intxt)).check(matches(isDisplayed()));
        onView(withId(R.id.course_intxt)).check(matches(isDisplayed()));
        onView(withId(R.id.title_intxt)).check(matches(isDisplayed()));
        onView(withId(R.id.create_classbtn)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFieldToast() {
        onView(withId(R.id.create_classbtn)).perform(click());

        onView(withText("Please fill all fields")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateClassSuccess() {
        UserSingleton mockUserSingleton = mock(UserSingleton.class);
        when(mockUserSingleton.getUsername()).thenReturn("testuser");
        when(mockUserSingleton.getPassword()).thenReturn("testpassword");

        onView(withId(R.id.classification_intxt)).perform(typeText(TEST_ABBREVIATION));
        onView(withId(R.id.course_intxt)).perform(typeText(TEST_COURSE_NUMBER));
        onView(withId(R.id.title_intxt)).perform(typeText(TEST_TITLE));

        onView(withId(R.id.create_classbtn)).perform(click());

        intended(IntentMatchers.hasComponent(home_classes.class.getName()));
    }

    @Test
    public void testHomeButtonNavigation() {
        onView(withId(R.id.homeButton)).perform(click());

        intended(IntentMatchers.hasComponent(home_classes.class.getName()));
    }

    @Test
    public void testNetworkError() {
        VolleyError mockError = mock(VolleyError.class);

        onView(withId(R.id.create_classbtn)).perform(click());

        onView(withText("Error creating class")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    public static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        protected boolean matchesSafely(Root root) {
            if (root.getClass().getSimpleName().equals("Toast$TN")) {
                try {
                    Object toastTN = root.getClass().getDeclaredField("mTN").get(root);
                    if (toastTN != null) {
                        View view = (View) toastTN.getClass().getDeclaredField("mNextView").get(toastTN);
                        return view != null && view.isShown();
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is a Toast and it is shown");
        }
    }
}
