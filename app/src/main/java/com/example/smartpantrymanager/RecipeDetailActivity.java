package com.example.smartpantrymanager;
import android.os.Bundle;
import android.widget.TextView;
public class RecipeDetailActivity extends BaseActivity {
    public static final String EXTRA_RECIPE="recipe_index";
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        int index=getIntent().getIntExtra(EXTRA_RECIPE,-1);
        if(index<0 || index>=RecipeSamples.NAMES.length) {
            finish();
            return;
        }
        showScreen(R.layout.activity_recipe_detail,0,true);
        ((TextView)findViewById(R.id.recipeTitle)).setText(RecipeSamples.NAMES[index]);
        ((TextView)findViewById(R.id.ingredients)).setText(RecipeSamples.INGREDIENTS[index]);
        ((TextView)findViewById(R.id.method)).setText(RecipeSamples.METHOD[index]);
    }
}
