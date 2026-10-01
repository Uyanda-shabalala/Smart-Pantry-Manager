package com.example.smartpantrymanager;
import android.os.Bundle;
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
 private long id; private EditText name,quantity,expiry; private Spinner unit;
 protected void onCreate(Bundle state) {
  super.onCreate(state); showScreen(R.layout.activity_ingredient,0,true);
  name=findViewById(R.id.ingredientName); quantity=findViewById(R.id.quantity); expiry=findViewById(R.id.expiry); unit=findViewById(R.id.unit);
  id=getIntent().getLongExtra(EXTRA_ID,-1); PreviewStore.Ingredient item=PreviewStore.find(id);
  if(id!=-1 && item==null) { Toast.makeText(this,R.string.missing_item,Toast.LENGTH_LONG).show(); finish(); return; }
  findViewById(R.id.delete).setVisibility(item==null?View.GONE:View.VISIBLE);
  if(item!=null) {
   ((TextView)findViewById(R.id.formTitle)).setText(R.string.edit_title);
   if(state==null) { name.setText(item.name); quantity.setText(String.valueOf(item.quantity)); expiry.setText(item.expiry); String[] units=getResources().getStringArray(R.array.units); for(int i=0;i<units.length;i++) if(units[i].equals(item.unit)) unit.setSelection(i); }
  }
  findViewById(R.id.save).setOnClickListener(v -> save());
  findViewById(R.id.delete).setOnClickListener(v -> new AlertDialog.Builder(this).setTitle(R.string.delete_title).setMessage(R.string.delete_message).setNegativeButton(R.string.cancel,null).setPositiveButton(R.string.delete,(d,w) -> { PreviewStore.items.remove(PreviewStore.find(id)); finish(); }).show());
 }
 private void save() {
  String n=name.getText().toString().trim(), e=expiry.getText().toString().trim();
  if(n.isEmpty()) { name.setError(getString(R.string.invalid_name)); name.requestFocus(); return; }
  double q;
  try { q=Double.parseDouble(quantity.getText().toString().trim().replace(',','.')); if(!Double.isFinite(q)||q<=0||q>1000000) throw new NumberFormatException(); }
  catch(NumberFormatException ex) { quantity.setError(getString(R.string.invalid_quantity)); quantity.requestFocus(); return; }
  if(!e.isEmpty()) try { if(!e.matches("\\d{4}-\\d{2}-\\d{2}")) throw new DateTimeParseException("format",e,0); LocalDate.parse(e); } catch(DateTimeParseException ex) { expiry.setError(getString(R.string.invalid_date)); expiry.requestFocus(); return; }
  String u=unit.getSelectedItem().toString();
  if(id==-1) PreviewStore.add(n,q,u,e);
  else { PreviewStore.Ingredient item=PreviewStore.find(id); if(item==null) { Toast.makeText(this,R.string.missing_item,Toast.LENGTH_LONG).show(); finish(); return; } item.name=n; item.quantity=q; item.unit=u; item.expiry=e; }
  Toast.makeText(this,R.string.saved,Toast.LENGTH_SHORT).show(); finish();
 }
}
