package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
public class PantryActivity extends BaseActivity {
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showScreen(R.layout.activity_pantry,R.id.nav_pantry,false);
        findViewById(R.id.add).setOnClickListener(v -> startActivity(new Intent(this,IngredientActivity.class)));
    }
    protected void onResume() {
        super.onResume();
        refresh();
    }
    private void refresh() {
        ArrayList<CardAdapter.Row> rows=new ArrayList<>();
        for(PreviewStore.Ingredient item:PreviewStore.items) rows.add(new CardAdapter.Row(item.name,new DecimalFormat("0.###").format(item.quantity)+" "+item.unit+(item.expiry.isEmpty()?"":" • "+item.expiry),() -> startActivity(new Intent(this,IngredientActivity.class).putExtra(IngredientActivity.EXTRA_ID,item.id))));
        RecyclerView list=findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CardAdapter(rows));
        ((TextView)findViewById(R.id.count)).setText(getString(R.string.count,rows.size()));
        findViewById(R.id.empty).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
    }
}
