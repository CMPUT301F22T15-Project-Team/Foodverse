package com.example.foodverse;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class MealPlanActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MealPlanActivity> rule =
            new ActivityTestRule<>(MealPlanActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("tester@email.com", "tester");
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Add a city to the listview and check the city name using assertTrue
     * Clear all the cities from the listview and check again with assertFalse
     */
    @Test
    public void checkActivity(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
    }

    /**
     * Test opening and closing navigation drawer.
     */
    @Test
    public void testNavDrawer() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        DrawerLayout drawer =
                solo.getCurrentActivity().findViewById(R.id.meal_plan_drawer);
        /*
         * Check if drawer is open:
         * https://stackoverflow.com/questions/21633791/how-to-detect-if-navigation-drawer-is-open
         * Answer by Neoh (2014). Accessed 2022-11-03.
         */
        assertFalse(drawer.isDrawerOpen(GravityCompat.START));
        solo.clickOnImageButton(0);
        solo.sleep(1000);
        // Ensure menu options are visible, to assert drawer is open.
        assertTrue(drawer.isDrawerOpen(GravityCompat.START));
        solo.clickOnImageButton(0);
        solo.sleep(1000);
        // Assert options are gone to check drawer is closed.
        assertFalse(drawer.isDrawerOpen(GravityCompat.START));
    }

    /**
     * Test navigation to StoredIngredientActivity.
     */
    @Test
    public void navStoredIngredient() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        solo.clickOnImageButton(0);
        // Ensure menu options are visible
        assertTrue(solo.searchText("Ingredients", true));
        assertTrue(solo.searchText("Recipes", true));
        assertTrue(solo.searchText("Meal Planner", true));
        assertTrue(solo.searchText("Shopping List", true));
        solo.clickOnText("Ingredients");
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
    }

    /**
     * Test navigation to RecipeActivity.
     */
    @Test
    public void navRecipes() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        solo.clickOnImageButton(0);
        // Ensure menu options are visible
        assertTrue(solo.searchText("Ingredients", true));
        assertTrue(solo.searchText("Recipes", true));
        assertTrue(solo.searchText("Meal Planner", true));
        assertTrue(solo.searchText("Shopping List", true));
        solo.clickOnText("Recipes");
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);
    }

    /**
     * Test navigation to MealPlanActivity.
     */
    @Test
    public void navMealPlan() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        solo.clickOnImageButton(0);
        // Ensure menu options are visible
        assertTrue(solo.searchText("Ingredients", true));
        assertTrue(solo.searchText("Recipes", true));
        assertTrue(solo.searchText("Meal Planner", true));
        assertTrue(solo.searchText("Shopping List", true));
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
    }

    /**
     * Test navigation to ShoppingListActivity.
     */
    @Test
    public void navShoppingList() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        solo.clickOnImageButton(0);
        // Ensure menu options are visible
        assertTrue(solo.searchText("Ingredients", true));
        assertTrue(solo.searchText("Recipes", true));
        assertTrue(solo.searchText("Meal Planner", true));
        assertTrue(solo.searchText("Shopping List", true));
        solo.clickOnText("Shopping List");
        solo.assertCurrentActivity("Wrong Activity", ShoppingListActivity.class);
    }

    /**
     * Test adding a new meal. Check to ensure it is still there after
     * an activity restart. Run as part of testMealActions.
     */
    public void testAddMeal() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        solo.clickOnButton("Add Meal");
        solo.enterText((EditText) solo.getView(R.id.meal_edit_name),
                "IntentTest MealPlan");
        solo.clickOnView(solo.getView(R.id.meal_ingredient_spinner));
        solo.clickOnText("IntentTest Meal");
        solo.clickOnButton("+");
        /*
         * Set date to something easy to grab for testing. Referenced:
         * https://stackoverflow.com/questions/6837012/robotium-how-to-set-a-date-in-date-picker-using-robotium
         * Answer by Jean-Philippe Roy (2012).
         */
        solo.clickOnView(solo.getView(R.id.date_button));
        solo.setDatePicker(0, 1900, 0, 1);
        solo.clickOnButton("OK");
        solo.clickOnButton("Confirm");
        // Assert meal does appear in list, look for year
        assertTrue(solo.searchText("IntentTest MealPlan", true));
        solo.clickOnText("IntentTest MealPlan");
        solo.clickOnButton("Edit");

        // Make sure meal date is as we entered, has the IntentTest Meal.
        assertTrue(solo.searchText("1900-01-01", true));
        assertTrue(solo.searchText("IntentTest Meal", true));
        assertTrue(solo.searchText("IntentTest MealPlan", true));
        solo.clickOnButton("Cancel");

        // Make sure meal persists with change in activity
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        assertTrue(solo.searchText("IntentTest MealPlan", true));
    }

    /**
     * Test editing an meal. Check to ensure edit persists after
     * an activity restart. Run as part of testMealActions.
     */
    public void testEditMeal() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        // Assert meal does appear in list, search for year
        assertTrue(solo.searchText("IntentTest MealPlan", true));

        solo.clickOnText("IntentTest MealPlan");
        solo.clickOnButton("Edit");
        solo.clearEditText((EditText) solo.getView(R.id.meal_edit_name));
        solo.enterText((EditText) solo.getView(R.id.meal_edit_name),
                "IntentTest Edit");
        /*
         * Set date to something easy to grab for testing. Referenced:
         * https://stackoverflow.com/questions/6837012/robotium-how-to-set-a-date-in-date-picker-using-robotium
         * Answer by Jean-Philippe Roy (2012).
         */
        solo.clickOnView(solo.getView(R.id.date_button));
        solo.setDatePicker(0, 1901, 1, 2);
        solo.clickOnButton("OK");
        solo.clickOnButton("Confirm");

        // Assert meal does appear in list, look for year
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest MealPlan", true));

        // Assert meal members are as we have entered
        solo.clickOnText("IntentTest Edit");
        solo.clickOnButton("Edit");
        assertTrue(solo.searchText("1901-02-02", true));
        assertTrue(solo.searchText("IntentTest Meal", true));
        assertTrue(solo.searchText("IntentTest Edit", true));
        solo.clickOnButton("Cancel");

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest MealPlan", true));
    }

    /**
     * Test deleting an meal. Check to ensure ingredient does not
     * reappear after an activity restart. Run as part of testMealActions.
     */
   public void testDeleteMeal() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        // Assert meal does appear in list, look for year
        assertTrue(solo.searchText("IntentTest Edit", true));

        solo.clickOnText("IntentTest Edit");
        solo.clickOnButton("Delete");

        // Assert meal does not appear in list, look for year
        assertFalse(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest MealPlan", true));

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        assertFalse(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest MealPlan", true));
    }

    /**
     * Adds a test ingredient we can add to the meal for testing.
     */
    public void addTestIng() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
        solo.clickOnButton("Add Ingredient");
        // Add ingredient
        solo.clearEditText((EditText) solo.getView(R.id.description_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.count_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.cost_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.unit_edit_text));
        solo.enterText((EditText) solo.getView(R.id.description_edit_text),
                "IntentTest Meal");
        solo.enterText((EditText) solo.getView(R.id.count_edit_text),
                "1");
        solo.enterText((EditText) solo.getView(R.id.cost_edit_text),
                "2");
        solo.enterText((EditText) solo.getView(R.id.unit_edit_text),
                "cups");
        solo.clickOnButton("Confirm");
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
    }


    /**
     * Cleans up the test ingredient.
     */
    public void deleteTestIng() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
        solo.clickOnText("IntentTest Meal");
        solo.clickOnButton("Delete");
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
    }

    /**
     * Test all meal actions, defined above. Due to firebase functions,
     * we expect these to be sequential actions, so that extra data is not
     * left over after tests and the same test can be expanded upon. A dummy
     * ingredient is created to add to the meal, and is also cleaned up, but is
     * not the focus of the tests.
     */
    @Test
    public void testMealActions() {
        addTestIng();
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        testAddMeal();
        testEditMeal();
        testDeleteMeal();
        deleteTestIng();
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
