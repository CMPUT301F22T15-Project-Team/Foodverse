package com.example.foodverse;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.widget.EditText;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<RecipeActivity> rule =
            new ActivityTestRule<>(RecipeActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
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
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);
    }

    /**
     * Test opening and closing navigation drawer.
     */
    @Test
    public void testNavDrawer() {
        /*
         * Open up navigation drawer, with reference to:
         * https://stackoverflow.com/questions/23053593/correct-way-to-open-navigationdrawer-and-select-items-in-robotium
         * Answer by Vassilis Zafeiris (2015). Accessed 2022-11-02.
         */
        DrawerLayout drawer =
                solo.getCurrentActivity().findViewById(R.id.recipe_drawer);
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
     * an activity restart.
     */
    @Test
    public void testAddIngredient() {
        solo.clickOnButton("Add Ingredient");
        solo.sleep(1000);
        solo.enterText((EditText) solo.getView(R.id.description_edit_text),
                "IntentTest Ingredient");
        solo.enterText((EditText) solo.getView(R.id.count_text_view),
                "IntentTest Ingredient");
    }

    /**
     * Test editing an ingredient. Check to ensure edit persists after
     * an activity restart.
     */
    @Test
    public void testEditIngredient() {

    }

    /**
     * Test deleting an ingredient. Check to ensure ingredient does not
     * reappear after an activity restart.
     */
    @Test
    public void testDeleteIngredient() {

    }


    /**
     * Check item taken from the listview
     */
    /*@Test
    public void checkCiyListItem(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
        solo.clickOnButton("CONFIRM");
        solo.waitForText("Edmonton", 1, 2000);
        // Get MainActivity to access its variables and methods.
        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        final ListView cityList = activity.cityList; // Get the listview
        String city = (String) cityList.getItemAtPosition(0); // Get item from first position
        assertEquals("Edmonton", city);
    }*/

    /**
     * Check that we switch to the showActivity when clicking a city
     */
    /*@Test
    public void checkSwitchActivity() {
        // Setup a city, click to switch
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
        solo.clickOnButton("CONFIRM");
        solo.waitForText("Edmonton", 1, 2000);
        solo.clickOnText("Edmonton");
        solo.assertCurrentActivity("Did not switch", showActivity.class);
    }*/

    /**
     * Check that the city displayed is the same as the one clicked
     */
    /*@Test
    public void checkConsistentCity() {
        // Setup two cities, click one to switch
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
        solo.clickOnButton("CONFIRM");
        solo.waitForText("Edmonton", 1, 2000);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Calgary");
        solo.clickOnButton("CONFIRM");
        solo.waitForText("Calgary", 1, 2000);
        solo.clickOnText("Edmonton");
        solo.assertCurrentActivity("Did not switch", showActivity.class);

        // Only Edmonton should be displayed. Only check in current activity view
        assertTrue(solo.searchText("Edmonton", true));
        assertFalse(solo.searchText("Calgary", true));
    }*/

    /**
     * Check that the back button works, that is we return to the MainActivity
     * and the cities displayed are the same
     */
    /*@Test
    public void checkBackButton() {
        // Setup two cities, click one to switch
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
        solo.clickOnButton("CONFIRM");
        solo.waitForText("Edmonton", 1, 2000);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Calgary");
        solo.clickOnButton("CONFIRM");
        solo.waitForText("Calgary", 1, 2000);
        solo.clickOnText("Edmonton");
        solo.assertCurrentActivity("Did not switch", showActivity.class);

        // Only Edmonton should be displayed. Only check current activity
        assertTrue(solo.searchText("Edmonton", true));
        assertFalse(solo.searchText("Calgary", true));
        solo.clickOnButton("Back");

        // Check activity, cities displayed in main activity
        solo.assertCurrentActivity("Did not go back", MainActivity.class);
        assertTrue(solo.searchText("Edmonton", true));
        assertTrue(solo.searchText("Calgary", true));
    }*/

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
