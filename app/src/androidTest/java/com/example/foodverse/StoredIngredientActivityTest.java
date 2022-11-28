package com.example.foodverse;

import android.app.Activity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class StoredIngredientActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<StoredIngredientActivity> rule =
            new ActivityTestRule<>(StoredIngredientActivity.class, true, true);

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
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
    }

    /**
     * Test opening and closing navigation drawer.
     */
    @Test
    public void testNavDrawer() {
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        DrawerLayout drawer =
                solo.getCurrentActivity().findViewById(R.id.stored_ingredient_drawer);
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
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
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
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
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
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
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
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
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
     * Test adding a new ingredient. Check to ensure it is still there after
     * an activity restart. Run as part of testIngredientActions.
     */
    public void testAddIngredient() {
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
        solo.clickOnButton("Add Ingredient");
        // Add ingredient
        solo.clearEditText((EditText) solo.getView(R.id.description_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.count_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.cost_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.unit_edit_text));
        solo.enterText((EditText) solo.getView(R.id.description_edit_text),
                "IntentTest Ingredient");
        solo.enterText((EditText) solo.getView(R.id.count_edit_text),
                "1");
        solo.enterText((EditText) solo.getView(R.id.cost_edit_text),
                "2");
        solo.enterText((EditText) solo.getView(R.id.unit_edit_text),
                "cups");
        /*
         * Set date to something unique for testing. Referenced:
         * https://stackoverflow.com/questions/6837012/robotium-how-to-set-a-date-in-date-picker-using-robotium
         * Answer by Jean-Philippe Roy (2012).
         */
        solo.clickOnView(solo.getView(R.id.expiry_button));
        solo.setDatePicker(0, 1900, 0, 1);
        solo.clickOnButton("OK");
        solo.clickOnButton("Confirm");
        // Assert ingredient does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Ingredient", true));

        // Assert ingredient members are as we have entered
        solo.clickOnText("IntentTest Ingredient");
        assertTrue(solo.searchText("IntentTest Ingredient", true));
        assertTrue(solo.searchText("1", true));
        assertTrue(solo.searchText("2", true));
        assertTrue(solo.searchText("cups", true));
        assertTrue(solo.searchText("1900-01-01", true));
        solo.clickOnButton("Cancel");

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
        assertTrue(solo.searchText("IntentTest Ingredient", true));
    }

    /**
     * Test editing an ingredient. Check to ensure edit persists after
     * an activity restart. Run as part of testIngredientActions.
     */
    public void testEditIngredient() {
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
        // Assert ingredient does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Ingredient", true));

        solo.clickOnText("IntentTest Ingredient");
        solo.clearEditText((EditText) solo.getView(R.id.description_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.count_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.cost_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.unit_edit_text));
        solo.enterText((EditText) solo.getView(R.id.description_edit_text),
                "IntentTest Edit");
        solo.enterText((EditText) solo.getView(R.id.count_edit_text),
                "5");
        solo.enterText((EditText) solo.getView(R.id.cost_edit_text),
                "3");
        solo.enterText((EditText) solo.getView(R.id.unit_edit_text),
                "bags");
        solo.clickOnView(solo.getView(R.id.expiry_button));
        solo.setDatePicker(0, 1901, 1, 2);
        solo.clickOnButton("OK");
        solo.clickOnButton("Confirm");

        // Assert ingredient does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest Ingredient", true));

        // Assert ingredient members are as we have entered
        solo.clickOnText("IntentTest Edit");
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertTrue(solo.searchText("5", true));
        assertTrue(solo.searchText("3", true));
        assertTrue(solo.searchText("bags", true));
        assertTrue(solo.searchText("1901-02-02", true));
        solo.clickOnButton("Cancel");

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest Ingredient", true));
    }

    /**
     * Test deleting an ingredient. Check to ensure ingredient does not
     * reappear after an activity restart. Run as part of testIngredientActions.
     */
    public void testDeleteIngredient() {
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
        // Assert ingredient does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Edit", true));

        solo.clickOnText("IntentTest Edit");
        solo.clickOnButton("Delete");

        // Assert ingredient does not appear in list, look for description
        assertFalse(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest Ingredient", true));

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
        assertFalse(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest Ingredient", true));
    }

    /**
     * Test all ingredient actions, defined above. Due to firebase functions,
     * we expect these to be sequential actions, so that extra data is not
     * left over after tests and the same test can be expanded upon.
     */
    @Test
    public void testIngredientActions() {
        testAddIngredient();
        testEditIngredient();
        testDeleteIngredient();
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
