package com.example.smartpantrymanager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class RecipeDetailActivity extends BaseActivity {
    public static final String EXTRA_RECIPE="recipe_id";
    private DBHelper dbHelper;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        long id=getIntent().getLongExtra(EXTRA_RECIPE,-1);
        if(id<=0) {
            finish();
            return;
        }
        showScreen(R.layout.activity_recipe_detail,0,true);
        dbHelper = new DBHelper(this);
        databaseExecutor.execute(() -> {
            try {
                Recipe recipe = dbHelper.getRecipe(id);

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) return;
                    if (recipe == null) { finish(); return; }
                    ((TextView)findViewById(R.id.recipeTitle)).setText(recipe.name);
                    ((TextView)findViewById(R.id.recipeInfo)).setText(
                            getString(R.string.recipe_info, recipe.minutes, recipe.servings));
                    ((TextView)findViewById(R.id.ingredients)).setText(recipe.ingredientsText());
                    ((TextView)findViewById(R.id.method)).setText(recipe.methodText());
                });
            } catch (SQLiteException ex) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.execute(() -> { if (dbHelper != null) dbHelper.close(); });
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
