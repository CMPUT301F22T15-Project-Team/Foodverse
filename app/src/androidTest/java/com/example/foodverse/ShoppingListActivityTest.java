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

import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ShoppingListActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<ShoppingListActivity> rule =
            new ActivityTestRule<>(ShoppingListActivity.class, true, true);

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
        solo.assertCurrentActivity("Wrong Activity", ShoppingListActivity.class);
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
                solo.getCurrentActivity().findViewById(R.id.shopping_list_drawer);
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
     * Adds a test ingredient we can add to the meal for testing.
     */
    public void addTestIng() {
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
                "IntentTest List");
        solo.enterText((EditText) solo.getView(R.id.count_edit_text),
                "1");
        solo.enterText((EditText) solo.getView(R.id.cost_edit_text),
                "2");
        solo.enterText((EditText) solo.getView(R.id.unit_edit_text),
                "cups");
        solo.clickOnButton("Confirm");
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
    }

    /**
     * Add a meal for testing the shopping list.
     */
    public void addTestMeal() {
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        solo.clickOnButton("Add Meal");
        solo.clickOnView(solo.getView(R.id.meal_ingredient_spinner));
        solo.clickOnText("IntentTest List");
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
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
    }

    /**
     * Cleanup test ingredient
     */
    public void delTestIng() {
        solo.clickOnImageButton(0);
        solo.clickOnText("Ingredients");
        solo.assertCurrentActivity("Wrong Activity", StoredIngredientActivity.class);
        solo.clickOnText("IntentTest List");
        solo.clickOnButton("Delete");
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
    }

    /**
     * Cleanup test meal
     */
    public void delTestMeal() {
        solo.clickOnImageButton(0);
        solo.clickOnText("Meal Planner");
        solo.assertCurrentActivity("Wrong Activity", MealPlanActivity.class);
        solo.clickOnText("1900");
        solo.clickOnButton("Delete");
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
    }

    /**
     * Test that a item is correctly added to the shopping list. Do this by
     * adding a test ingredient, adding a test meal, deleting the test ingredient
     * and ensuring the test ingredient appears in the shopping list. Cleanup
     * afterwards.
     */
    @Test
    public void testShoppingListPopulate() {
        solo.assertCurrentActivity("Wrong Activity", ShoppingListActivity.class);
        addTestIng();
        addTestMeal();
        delTestIng();
        solo.assertCurrentActivity("Wrong Activity", ShoppingListActivity.class);
        // Check to make sure the test ingredient is there
        assertTrue(solo.searchText("IntentTest List", 1, true, true));
        // Clean up, re-add test ingredient to remove from shopping list
        addTestIng();
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        assertFalse(solo.searchText("IntentTest List", 1, true, true));
        delTestMeal();
        delTestIng();
        solo.assertCurrentActivity("Wrong Activity", ShoppingListActivity.class);
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
