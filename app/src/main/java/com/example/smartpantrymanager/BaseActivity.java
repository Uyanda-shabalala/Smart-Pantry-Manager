package com.example.smartpantrymanager;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public abstract class BaseActivity extends AppCompatActivity {
    protected void showScreen(int layout, int selected, boolean child) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_shell);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (view, insets) -> {
            Insets bars=insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()); view.setPadding(bars.left,bars.top,bars.right,bars.bottom); return insets;
        }
        );
        getLayoutInflater().inflate(layout,(FrameLayout)findViewById(R.id.content),true);
        MaterialToolbar toolbar=findViewById(R.id.toolbar);
        if(child) {
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationContentDescription(R.string.back);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        BottomNavigationView nav=findViewById(R.id.navigation);
        if(child) nav.setVisibility(View.GONE);
        else {
            nav.setSelectedItemId(selected);
            nav.setOnItemSelectedListener(item -> {
                if(item.getItemId()==selected) return true; Class<?> target=item.getItemId()==R.id.nav_pantry ? PantryActivity.class : item.getItemId()==R.id.nav_recipes ? RecipesActivity.class : SettingsActivity.class; startActivity(new Intent(this,target).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)); return true;
            }
            );
        }
    }
}
