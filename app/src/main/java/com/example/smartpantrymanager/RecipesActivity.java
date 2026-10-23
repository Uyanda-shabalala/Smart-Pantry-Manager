package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipesActivity extends BaseActivity {
    private DBHelper dbHelper;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        dbHelper = new DBHelper(this);
        showScreen(R.layout.activity_recipes, R.id.nav_recipes, false);
        ((RecyclerView) findViewById(R.id.list)).setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reload the pantry when coming back so edits and deletions also change the suggestions.
        findViewById(R.id.list).setVisibility(View.GONE);
        TextView message = findViewById(R.id.empty);
        message.setText(R.string.loading_recipes);
        message.setVisibility(View.VISIBLE);
        databaseExecutor.execute(() -> {
            try {
                List<Recipe> recipes = dbHelper.getAllRecipes();
                List<Ingredient> pantry = dbHelper.getAllIngredients();
                ArrayList<CardAdapter.Row> rows = new ArrayList<>();
                for (Recipe recipe : recipes) {
                    if (RecipeMatcher.canMake(recipe, pantry)) {
                        rows.add(new CardAdapter.Row(recipe.name,
                                getString(R.string.recipe_info, recipe.minutes, recipe.servings),
                                () -> startActivity(new Intent(this, RecipeDetailActivity.class)
                                        .putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe.id))));
                    }
                }
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    RecyclerView list = findViewById(R.id.list);
                    list.setAdapter(new CardAdapter(rows));
                    list.setVisibility(View.VISIBLE);
                    message.setText(R.string.no_matches);
                    message.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
                });
            } catch (SQLiteException ex) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    message.setText(R.string.database_error);
                    Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.execute(() -> dbHelper.close());
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
