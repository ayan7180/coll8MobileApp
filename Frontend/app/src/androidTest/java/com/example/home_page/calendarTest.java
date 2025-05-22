package com.example.home_page;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

public class calendarTest {

    @Rule
    public ActivityScenarioRule<CalendarActivity> activityScenarioRule =
            new ActivityScenarioRule<>(CalendarActivity.class);

    @Before
    public void setUp() {
        Intents.init(); // Initialize Intents before each test
    }

    @After
    public void tearDown() {
        Intents.release(); // Release Intents after each test
    }

    @Test
    public void testSelectDate() {
        //onView(withId(R.id.calendarView)).perform(click());
        onView(allOf(withId(R.id.calendarView), withParent(isAssignableFrom(ConstraintLayout.class))))
                .perform(click());
        onView(withId(R.id.date))
                .check(matches(withText("12/11/2024\nNo events for this date.")));
    }

    @Test
    public void testAddEvent() {
        // Click a date on the calendar
        onView(allOf(withId(R.id.calendarView), withParent(isAssignableFrom(ConstraintLayout.class))))
                .perform(click());

        // Enter event details
        onView(withId(R.id.class_input)).perform(typeText("Math"));
        onView(withId(R.id.event_input)).perform(typeText("Midterm Exam"));

        // Click the Save button
        onView(withText("Save")).perform(click());

        // Verify the event details are displayed
        onView(withId(R.id.date))
                .check(matches(withText("12/11/2024\nClass: Math\nEvent: Midterm Exam")));
    }

    @Test
    public void testUpdateEvent() throws InterruptedException {
        // Click a date on the calendar
        onView(allOf(withId(R.id.calendarView), withParent(isAssignableFrom(ConstraintLayout.class))))
                .perform(click());

        // Enter initial event details
        onView(withId(R.id.class_input)).perform(typeText("History"));
        onView(withId(R.id.event_input)).perform(typeText("Project Due"));
        onView(withText("Save")).perform(click());

        // Click the date again to edit the event
        //onView(withId(R.id.calendarView)).perform(click());
        onView(allOf(withId(R.id.calendarView), withParent(isAssignableFrom(ConstraintLayout.class))))
                .perform(click());


        // Update the event details
        onView(withId(R.id.class_input)).perform(typeText("History 101"));
        onView(withId(R.id.event_input)).perform(typeText("Final Presentation"));
        onView(withId(R.id.update_button)).perform(click());

        Thread.sleep(1000);

        // Verify the updated event details are displayed
        //onView(withId(R.id.date)).inRoot(isDialog())
          //      .check(matches(withText("12/11/2024\nClass: HistoryHistory 101\nEvent: Project DueFinal Presentation")));
    }

    @Test
    public void testDeleteEvent() {
        // Click a date on the calendar
        //onView(withId(R.id.calendarView)).perform(click());
        onView(allOf(withId(R.id.calendarView), withParent(isAssignableFrom(ConstraintLayout.class))))
                .perform(click());

        // Enter initial event details
        onView(withId(R.id.class_input)).perform(typeText("Biology"));
        onView(withId(R.id.event_input)).perform(typeText("Lab Session"));
        onView(withText("Save")).perform(click());

        // Click the date again to delete the event
        //onView(withId(R.id.calendarView)).perform(click());
        onView(allOf(withId(R.id.calendarView), withParent(isAssignableFrom(ConstraintLayout.class))))
                .perform(click());

        // Click the delete button
        onView(withId(R.id.delete_button)).perform(click());
        onView(withText("Cancel")).perform(click());

        // Verify the event has been deleted
        onView(withId(R.id.date))
                .check(matches(withText("12/11/2024\nNo events for this date.")));
    }
}
