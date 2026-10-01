package com.example.smartpantrymanager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.google.android.material.switchmaterial.SwitchMaterial;
public class SettingsActivity extends BaseActivity {
 protected void onCreate(Bundle state) {
  super.onCreate(state); showScreen(R.layout.activity_settings,R.id.nav_settings,false);
  SwitchMaterial alerts=findViewById(R.id.alerts); alerts.setChecked(PreviewStore.alerts); alerts.setOnCheckedChangeListener((b,value) -> PreviewStore.alerts=value);
  Spinner units=findViewById(R.id.preference); units.setSelection(PreviewStore.unitsPreference);
  units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { public void onItemSelected(AdapterView<?> p,View v,int position,long id) { PreviewStore.unitsPreference=position; } public void onNothingSelected(AdapterView<?> p) {} });
 }
}
