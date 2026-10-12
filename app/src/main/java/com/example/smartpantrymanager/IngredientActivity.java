package com.example.smartpantrymanager;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
public class IngredientActivity extends BaseActivity {
    public static final String EXTRA_ID="ingredient_id";
    private long id;
    private EditText name,quantity,expiry;
    private Spinner unit;
    private DBHelper dbHelper;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        dbHelper = new DBHelper(this);
        showScreen(R.layout.activity_ingredient,0,true);
        name=findViewById(R.id.ingredientName);
        quantity=findViewById(R.id.quantity);
        expiry=findViewById(R.id.expiry);
        unit=findViewById(R.id.unit);
        id=getIntent().getLongExtra(EXTRA_ID,-1);
        findViewById(R.id.cancel).setOnClickListener(v -> finish());
        findViewById(R.id.save).setOnClickListener(v -> save());
        findViewById(R.id.delete).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title).setMessage(R.string.delete_message)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.delete,(d,w) -> deleteIngredient()).show());
        setBusy(true);
        databaseExecutor.execute(() -> {
            try {
                Ingredient item = id == -1 ? null : dbHelper.getIngredient(id);
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    populate(item, state);
                    setBusy(false);
                });
            } catch (SQLiteException ex) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    Toast.makeText(this,R.string.database_error,Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    private void populate(Ingredient item, Bundle state) {

        if(id!=-1 && item==null) {
            Toast.makeText(this,R.string.missing_item,Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        findViewById(R.id.delete).setVisibility(item==null?View.GONE:View.VISIBLE);
        if(item!=null) {
            ((TextView)findViewById(R.id.formTitle)).setText(R.string.edit_title);
            if(state==null) {
                name.setText(item.name);
                quantity.setText(String.valueOf(item.quantity));
                expiry.setText(item.expiry);
                String[] units=getResources().getStringArray(R.array.units);
                for(int i=0;i<units.length;i++) if(units[i].equals(item.unit)) unit.setSelection(i);
            }
        }
    }
    private void save() {
        String n=name.getText().toString().trim(), e=expiry.getText().toString().trim();
        if(n.isEmpty()) {
            name.setError(getString(R.string.invalid_name));
            name.requestFocus();
            return;
        }
        double q;
        try {
            q=Double.parseDouble(quantity.getText().toString().trim().replace(',','.'));
            if(!Double.isFinite(q)||q<=0||q>1000000) throw new NumberFormatException();
        }
        catch(NumberFormatException ex) {
            quantity.setError(getString(R.string.invalid_quantity));
            quantity.requestFocus();
            return;
        }
        if(!e.isEmpty()) try {
            if(!e.matches("\\d{4}-\\d{2}-\\d{2}")) throw new DateTimeParseException("format",e,0);
            LocalDate.parse(e);
        }
        catch(DateTimeParseException ex) {
            expiry.setError(getString(R.string.invalid_date));
            expiry.requestFocus();
            return;
        }
        String u=unit.getSelectedItem().toString();
        final double amount = q;
        setBusy(true);
        databaseExecutor.execute(() -> {
            try {
                boolean saved;
                if (id == -1) {
                    dbHelper.addIngredient(n,amount,u,e);
                    saved = true;
                } else saved = dbHelper.updateIngredient(id,n,amount,u,e);
                completeWrite(saved ? R.string.saved : R.string.missing_item);
            } catch (SQLiteException ex) {
                showDatabaseError();
            }
        });
    }

    private void deleteIngredient() {
        setBusy(true);
        databaseExecutor.execute(() -> {
            try {
                completeWrite(dbHelper.deleteIngredient(id) ? R.string.deleted : R.string.missing_item);
            } catch (SQLiteException ex) {
                showDatabaseError();
            }
        });
    }

    private void completeWrite(int message) {
        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatabaseError() {
        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            setBusy(false);
            Toast.makeText(this,R.string.database_error,Toast.LENGTH_LONG).show();
        });
    }

    private void setBusy(boolean busy) {
        findViewById(R.id.save).setEnabled(!busy);
        findViewById(R.id.delete).setEnabled(!busy);
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.execute(() -> dbHelper.close());
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
