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
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    recipes = loaded;
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
            rows.add(new CardAdapter.Row(recipe.name, getString(R.string.recipe_info, recipe.minutes, recipe.servings),
                    () -> startActivity(new Intent(this,RecipeDetailActivity.class)
                            .putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe.id))));
        }
        RecyclerView list=findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CardAdapter(rows));
        findViewById(R.id.empty).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
        findViewById(R.id.emptyPreview).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.execute(() -> dbHelper.close());
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
