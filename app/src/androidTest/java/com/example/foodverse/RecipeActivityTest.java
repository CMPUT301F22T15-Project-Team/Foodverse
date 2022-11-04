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
     * Test adding a new recipe. Check to ensure it is still there after
     * an activity restart. Run as part of testRecipeActions.
     */
    public void testAddRecipe() {
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);
        solo.clickOnButton("Add Recipe");
        // Add recipe
        solo.clearEditText((EditText) solo.getView(R.id.recipe_title_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.comments_editText));
        solo.clearEditText((EditText) solo.getView(R.id.serving_size_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.prep_time_edit_text));

        solo.enterText((EditText) solo.getView(R.id.recipe_title_edit_text),
                "IntentTest Recipe");
        solo.enterText((EditText) solo.getView(R.id.comments_editText),
                "IntentTest Comment");
        solo.clickOnRadioButton(1);
        solo.enterText((EditText) solo.getView(R.id.serving_size_edit_text),
                "2");
        solo.enterText((EditText) solo.getView(R.id.prep_time_edit_text),
                "5");

        solo.clickOnButton("Ok");
        // Assert the recipe does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Recipe", true));

        // Assert ingredient members are as we have entered
        solo.clickOnText("IntentTest Recipe");
        assertTrue(solo.searchText("IntentTest Recipe", true));
        assertTrue(solo.searchText("IntentTest Comment", true));
        solo.isRadioButtonChecked(1);
        assertTrue(solo.searchText("2", true));
        assertTrue(solo.searchText("5", true));
        solo.clickOnButton("Cancel");

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Recipes");
        assertTrue(solo.searchText("IntentTest Recipe", true));
    }

    /**
     * Test editing an recipe. Check to ensure edit persists after
     * an activity restart. Run as part of testRecipeActions.
     */
    public void testEditRecipe() {
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);
        // Assert recipe does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Recipe", true));

        solo.clickOnText("IntentTest Recipe");
        solo.clearEditText((EditText) solo.getView(R.id.recipe_title_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.comments_editText));
        solo.clearEditText((EditText) solo.getView(R.id.serving_size_edit_text));
        solo.clearEditText((EditText) solo.getView(R.id.prep_time_edit_text));

        solo.enterText((EditText) solo.getView(R.id.recipe_title_edit_text),
                "IntentTest Edit");
        solo.enterText((EditText) solo.getView(R.id.comments_editText),
                "IntentTest Comment Edit");
        solo.clickOnRadioButton(2);
        solo.enterText((EditText) solo.getView(R.id.serving_size_edit_text),
                "20");
        solo.enterText((EditText) solo.getView(R.id.prep_time_edit_text),
                "50");
        solo.clickOnButton("Ok");

        // Assert recipe does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest Recipe", true));

        // Assert recipe members are as we have entered
        solo.clickOnText("IntentTest Edit");
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertTrue(solo.searchText("IntentTest Comment Edit", true));
        solo.isRadioButtonChecked(2);
        assertTrue(solo.searchText("20", true));
        assertTrue(solo.searchText("50", true));
        solo.clickOnButton("Cancel");

        // Navigate off activity and back to check to make sure Firebase worked.
        solo.clickOnImageButton(0);
        solo.clickOnText("Shopping List");
        solo.clickOnImageButton(0);
        solo.clickOnText("Recipes");
        assertTrue(solo.searchText("IntentTest Edit", true));
        assertFalse(solo.searchText("IntentTest Recipe", true));
    }

    /**
     * Test deleting a recipe. Check to ensure recipe does not
     * reappear after an activity restart. Run as part of testRecipeActions.
     */
    public void testDeleteRecipe() {
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);
        // Assert ingredient does appear in list, look for description
        assertTrue(solo.searchText("IntentTest Edit", true));

        solo.clickOnText("IntentTest Edit");
        solo.clickOnButton("Delete");

        // Assert ingredient does appear in list, look for description
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
     * Test all recipe actions, defined above. Due to firebase functions,
     * we expect these to be sequential actions, so that extra data is not
     * left over after tests and the same test can be expanded upon.
     */
    @Test
    public void testRecipeActions() {
        testAddRecipe();
        testEditRecipe();
        testDeleteRecipe();
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
