package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
public class PantryActivity extends BaseActivity {

    private DBHelper dbHelper;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    protected void onCreate(Bundle state) {
        super.onCreate(state);
        dbHelper = new DBHelper(this);
        showScreen(R.layout.activity_pantry,R.id.nav_pantry,false);
        findViewById(R.id.addItem).setOnClickListener(v -> startActivity(new Intent(this,IngredientActivity.class)));
    }
    protected void onResume() {
        super.onResume();
        refresh();
    }
    private void refresh() {
        databaseExecutor.execute(() -> {
            try {
                List<Ingredient> items = dbHelper.getAllIngredients();
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) display(items);
                });
            } catch (SQLiteException ex) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed())
                        Toast.makeText(this,R.string.database_error,Toast.LENGTH_LONG).show();
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

    private void display(List<Ingredient> items) {
        ArrayList<CardAdapter.Row> rows=new ArrayList<>();
        for(Ingredient item:items) rows.add(new CardAdapter.Row(item.name,new DecimalFormat("0.###").format(item.quantity)+" "+item.unit+(item.expiry.isEmpty()?"":" • "+item.expiry),() -> startActivity(new Intent(this,IngredientActivity.class).putExtra(IngredientActivity.EXTRA_ID,item.id))));
        RecyclerView list=findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CardAdapter(rows));
        ((TextView)findViewById(R.id.count)).setText(getString(R.string.count,rows.size()));
        findViewById(R.id.empty).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
    }




}
