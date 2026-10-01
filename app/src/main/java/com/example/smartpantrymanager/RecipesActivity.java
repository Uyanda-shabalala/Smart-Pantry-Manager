package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
public class RecipesActivity extends BaseActivity {
 protected void onCreate(Bundle state) {
  super.onCreate(state); showScreen(R.layout.activity_recipes,R.id.nav_recipes,false);
  SwitchMaterial samples=findViewById(R.id.samples); samples.setChecked(state==null || state.getBoolean("samples",true));
  samples.setOnCheckedChangeListener((button,checked) -> display(checked)); display(samples.isChecked());
 }
 protected void onSaveInstanceState(Bundle state) { state.putBoolean("samples",((SwitchMaterial)findViewById(R.id.samples)).isChecked()); super.onSaveInstanceState(state); }
 private void display(boolean show) {
  ArrayList<CardAdapter.Row> rows=new ArrayList<>();
  if(show) for(int i=0;i<RecipeSamples.NAMES.length;i++) { final int index=i; rows.add(new CardAdapter.Row(RecipeSamples.NAMES[i],RecipeSamples.INFO[i],() -> startActivity(new Intent(this,RecipeDetailActivity.class).putExtra(RecipeDetailActivity.EXTRA_RECIPE,index)))); }
  RecyclerView list=findViewById(R.id.list); list.setLayoutManager(new LinearLayoutManager(this)); list.setAdapter(new CardAdapter(rows));
  findViewById(R.id.empty).setVisibility(show?View.GONE:View.VISIBLE); findViewById(R.id.emptyPreview).setVisibility(show?View.GONE:View.VISIBLE);
 }
}
