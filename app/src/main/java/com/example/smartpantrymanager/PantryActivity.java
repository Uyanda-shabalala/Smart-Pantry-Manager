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
import java.time.LocalDate;
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
        boolean metric = AppSettings.unitsPreference(this) == 0;
        for (Ingredient item : items) {
            String amount = IngredientUnits.display(item.quantity, item.unit, metric);
            rows.add(new CardAdapter.Row(item.name,
                    amount + (item.expiry.isEmpty() ? "" : " \u2022 " + item.expiry),
                    () -> startActivity(new Intent(this, IngredientActivity.class)
                            .putExtra(IngredientActivity.EXTRA_ID, item.id))));
        }
        // use the real dates and saved setting instead of showing a fixed expiry count.
        int dueSoon = ExpirySummary.dueSoon(items, LocalDate.now());
        TextView summary = findViewById(R.id.expirySummary);
        summary.setText(getResources().getQuantityString(R.plurals.expiry_summary, dueSoon, dueSoon));
        summary.setVisibility(AppSettings.showExpirySummary(this) && dueSoon > 0 ? View.VISIBLE : View.GONE);
        RecyclerView list=findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CardAdapter(rows));
        ((TextView)findViewById(R.id.count)).setText(getString(R.string.count,rows.size()));
        findViewById(R.id.empty).setVisibility(rows.isEmpty()?View.VISIBLE:View.GONE);
    }




}
