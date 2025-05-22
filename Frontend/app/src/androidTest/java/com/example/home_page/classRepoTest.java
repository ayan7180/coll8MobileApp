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
public class classRepoTest {

    private static final String TEST_ABBREV = "CS309";
    private static final int TEST_COURSE_NUM = 3090;

    @Rule
    public ActivityScenarioRule<classRepoActivity> activityScenarioRule =
            new ActivityScenarioRule<>(classRepoActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testFilesAreDisplayed() {
        Intent intent = new Intent();
        intent.putExtra("abbreviation", TEST_ABBREV);
        intent.putExtra("courseNum", TEST_COURSE_NUM);
        ActivityScenario.launch(classRepoActivity.class, intent.getExtras());

        onView(withId(R.id.classFilesContainer)).check(matches(isDisplayed()));
        onView(withText("File 1")).check(matches(isDisplayed()));
    }

    @Test
    public void testHomeButtonNavigatesToHomeClasses() {
        Intent intent = new Intent();
        intent.putExtra("abbreviation", TEST_ABBREV);
        intent.putExtra("courseNum", TEST_COURSE_NUM);
        ActivityScenario.launch(classRepoActivity.class, intent.getExtras());

        onView(withId(R.id.homeButton)).perform(click());
        intended(hasComponent(home_classes.class.getName()));
    }

    @Test
    public void testClassChatButtonNavigatesToMainChatActivity() {
        Intent intent = new Intent();
        intent.putExtra("abbreviation", TEST_ABBREV);
        intent.putExtra("courseNum", TEST_COURSE_NUM);
        ActivityScenario.launch(classRepoActivity.class, intent.getExtras());

        onView(withId(R.id.gotoChatBtn)).perform(click());
        intended(IntentMatchers.hasComponent(mainChatActivity.class.getName()));
        intended(IntentMatchers.hasExtra("courseNum", TEST_COURSE_NUM));
        intended(IntentMatchers.hasExtra("abbreviation", TEST_ABBREV));
    }
}
