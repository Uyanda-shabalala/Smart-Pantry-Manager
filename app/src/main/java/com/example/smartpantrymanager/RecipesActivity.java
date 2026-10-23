package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
public class RecipesActivity extends BaseActivity {
    private DBHelper dbHelper;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private List<Recipe> recipes = new ArrayList<>();
    private List<Ingredient> pantryIngredients = new ArrayList<>();

    protected void onCreate(Bundle state) {
        super.onCreate(state);
        dbHelper = new DBHelper(this);
        showScreen(R.layout.activity_recipes,R.id.nav_recipes,false);
        SwitchMaterial samples=findViewById(R.id.samples);
        samples.setChecked(state==null || state.getBoolean("samples",true));
        samples.setOnCheckedChangeListener((button,checked) -> display(checked));
        display(samples.isChecked());
        databaseExecutor.execute(() -> {
            try {
                List<Recipe> loaded = dbHelper.getAllRecipes();
                List<Ingredient> loadedPantry = dbHelper.getAllIngredients();
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    recipes = loaded;
                    pantryIngredients = loadedPantry;
                    display(samples.isChecked());
                });
            } catch (SQLiteException ex) {
                 runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed())
                        Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("samples",((SwitchMaterial)findViewById(R.id.samples)).isChecked());
        super.onSaveInstanceState(state);
    }
    private void display(boolean show) {
        ArrayList<CardAdapter.Row> rows=new ArrayList<>();


        if(show) for(Recipe recipe : recipes) {

            // check if the pantry has enough of each ingredient , if it does add the recipe to the card.

            boolean canMake=true;
            for (Recipe.Requirement requiredIngredient: recipe.ingredients) {

                   // clean up both names so plurals and extra spaces do not stop them from matching.
                   String requiredName = normalizeIngredientName(requiredIngredient.name);
                   double availableQuantity = 0;

                   for (Ingredient pantryItem : pantryIngredients){

                       String pantryName = normalizeIngredientName(pantryItem.name);
                       if (requiredName.equals(pantryName)&& requiredIngredient.unit.equalsIgnoreCase(pantryItem.unit)){
                           availableQuantity+=pantryItem.quantity;
                       }
                   }

                   if (availableQuantity<requiredIngredient.quantity){
                       canMake=false;
                       break;
                   }

            }
            if (canMake){
                rows.add(new CardAdapter.Row(recipe.name, getString(R.string.recipe_info, recipe.minutes, recipe.servings),
                        () -> startActivity(new Intent(this,RecipeDetailActivity.class)
                                .putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe.id))));
            }

        }
        RecyclerView list=findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CardAdapter(rows));
        findViewById(R.id.empty).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
        findViewById(R.id.emptyPreview).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
    }

    private String normalizeIngredientName(String name) {
        // remove extra spaces and make the name lowercase before checking the plural.
        String normalized = name.trim()
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("\\s+", " ");

        // only check the last word so burger patties becomes burger patty.
        int lastSpace = normalized.lastIndexOf(' ');
        String prefix = normalized.substring(0, lastSpace + 1);
        String word = normalized.substring(lastSpace + 1);

        // handle common plurals that do not fit the general rules.
        switch (word) {
            case "tomatoes": word = "tomato"; break;
            case "potatoes": word = "potato"; break;
            case "cookies": word = "cookie"; break;
            case "brownies": word = "brownie"; break;
            case "leaves": word = "leaf"; break;
            case "loaves": word = "loaf"; break;

            // these names should stay as they are.
            case "oats":
            case "couscous":
            case "asparagus":
            case "molasses":
            case "hummus":
                break;

            default:
                // for other names , use common endings to get the singular.
                if (word.length() > 3 && word.endsWith("ies")) {
                    word = word.substring(0, word.length() - 3) + "y";
                } else if (word.endsWith("ches")
                        || word.endsWith("shes")
                        || word.endsWith("sses")
                        || word.endsWith("xes")
                        || word.endsWith("zzes")) {
                    word = word.substring(0, word.length() - 2);
                } else if (word.length() > 3
                        && word.endsWith("s")
                        && !word.endsWith("ss")
                        && !word.endsWith("us")
                        && !word.endsWith("is")) {
                    word = word.substring(0, word.length() - 1);
                }
        }

        return prefix + word;
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.execute(() -> dbHelper.close());
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
