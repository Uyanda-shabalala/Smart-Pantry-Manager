package com.example.smartpantrymanager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.google.android.material.switchmaterial.SwitchMaterial;
public class SettingsActivity extends BaseActivity {
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showScreen(R.layout.activity_settings,R.id.nav_settings,false);
        SwitchMaterial alerts=findViewById(R.id.alerts);
        alerts.setChecked(AppSettings.showExpirySummary(this));
        alerts.setOnCheckedChangeListener((b,value) -> AppSettings.setExpirySummary(this, value));
        Spinner units=findViewById(R.id.preference);
        units.setSelection(AppSettings.unitsPreference(this));
        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p,View v,int position,long id) {
                AppSettings.setUnitsPreference(SettingsActivity.this, position);
            }
            public void onNothingSelected(AdapterView<?> p) {
            }
        }
        );
    }
}
