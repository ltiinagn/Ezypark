package com.ezypark;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class Carpark extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.century_sqr_floor_tabs);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        View centurySquareB1Include = findViewById(R.id.century_sqr_b1_include);
        View centurySquareB2Include = findViewById(R.id.century_sqr_b2_include);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String level = tab.getText().toString();
                if (level.equals("Basement 1")) {
                    centurySquareB1Include.setVisibility(View.VISIBLE);
                    centurySquareB2Include.setVisibility(View.INVISIBLE);
                }
                else if (level.equals("Basement 2")) {
                    centurySquareB1Include.setVisibility(View.INVISIBLE);
                    centurySquareB2Include.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
