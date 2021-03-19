package ando.indicator.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ExampleMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_main_layout);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.scrollable_tab) {
            startActivity(new Intent(this, ScrollableTabExampleActivity.class));
        } else if (id == R.id.fixed_tab) {
            startActivity(new Intent(this, FixedTabExampleActivity.class));
        } else if (id == R.id.dynamic_tab) {
            startActivity(new Intent(this, DynamicTabExampleActivity.class));
        } else if (id == R.id.no_tab_only_indicator) {
            startActivity(new Intent(this, NoTabOnlyIndicatorExampleActivity.class));
        } else if (id == R.id.tab_with_badge_view) {
            startActivity(new Intent(this, BadgeTabExampleActivity.class));
        } else if (id == R.id.work_with_fragment_container) {
            startActivity(new Intent(this, FragmentContainerExampleActivity.class));
        } else if (id == R.id.load_custom_layout) {
            startActivity(new Intent(this, LoadCustomLayoutExampleActivity.class));
        } else if (id == R.id.custom_navigator) {
            startActivity(new Intent(this, CustomNavigatorExampleActivity.class));
        }
    }
}
