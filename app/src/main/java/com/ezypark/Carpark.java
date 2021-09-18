package com.ezypark;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Not enough time, hard coded many things

public class Carpark extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.century_sqr_floor_tabs);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        View centurySquareB1Include = findViewById(R.id.century_sqr_b1_include);
        View centurySquareB2Include = findViewById(R.id.century_sqr_b2_include);

        int[] centurySquareB1LotIDs = new int[] { R.id.CenturySquareB1_lot9, R.id.CenturySquareB1_lot10, R.id.CenturySquareB1_lot11, R.id.CenturySquareB1_lot0, R.id.CenturySquareB1_lot1 };
        String[] centurySquareB1LotNames = new String[] {"lot9", "lot10", "lot11", "lot0", "lot1"};
        int[] centurySquareB2LotIDs = new int[] { R.id.CenturySquareB2_lot9, R.id.CenturySquareB2_lot10, R.id.CenturySquareB2_lot11, R.id.CenturySquareB2_lot0, R.id.CenturySquareB2_lot1 };
        String[] centurySquareB2LotNames = new String[] {"lot9", "lot10", "lot11", "lot0", "lot1"};

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ezypark-49e23-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference reference = database.getReference();

        List<Button> centurySquareB1Buttons = new ArrayList<Button>();
        List<DatabaseReference> centurySquareB1References = new ArrayList<DatabaseReference>();
        for (int i = 0; i < centurySquareB1LotIDs.length; i++) {
            final int j = i;
            centurySquareB1Buttons.add((Button)findViewById(centurySquareB1LotIDs[j]));
            centurySquareB1References.add(reference.child("carparks").child("Century Square").child("B1").child(centurySquareB1LotNames[j]));
        }
        DatabaseReference[] centurySquareB1ReferencesArray = new DatabaseReference[ centurySquareB1References.size() ];
        centurySquareB1References.toArray(centurySquareB1ReferencesArray);
        Button[] centurySquareB1ButtonsArray = new Button[ centurySquareB1Buttons.size() ];
        centurySquareB1Buttons.toArray(centurySquareB1ButtonsArray);

        List<Button> centurySquareB2Buttons = new ArrayList<Button>();
        List<DatabaseReference> centurySquareB2References = new ArrayList<DatabaseReference>();
        for (int i = 0; i < centurySquareB2LotIDs.length; i++) {
            final int j = i;
            centurySquareB2Buttons.add((Button)findViewById(centurySquareB2LotIDs[j]));
            centurySquareB2References.add(reference.child("carparks").child("Century Square").child("B2").child(centurySquareB2LotNames[j]));
        }
        DatabaseReference[] centurySquareB2ReferencesArray = new DatabaseReference[ centurySquareB2References.size() ];
        centurySquareB2References.toArray(centurySquareB2ReferencesArray);
        Button[] centurySquareB2ButtonsArray = new Button[ centurySquareB2Buttons.size() ];
        centurySquareB2Buttons.toArray(centurySquareB2ButtonsArray);

        for (int i = 0; i < centurySquareB1ReferencesArray.length; i++) {
            final int j = i;
            centurySquareB1ReferencesArray[j].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean value = (Boolean) dataSnapshot.getValue();
                    if (value) {
                        ViewCompat.setBackgroundTintList(centurySquareB1ButtonsArray[j], ContextCompat.getColorStateList(Carpark.this, R.color.occupied));
                    }
                    else {
                        ViewCompat.setBackgroundTintList(centurySquareB1ButtonsArray[j], ContextCompat.getColorStateList(Carpark.this, R.color.unoccupied));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Test", "Failed to read value.", error.toException());
                }
            });
        }

        for (int i = 0; i < centurySquareB2ReferencesArray.length; i++) {
            final int j = i;
            centurySquareB2ReferencesArray[j].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean value = (Boolean) dataSnapshot.getValue();
                    if (value) {
                        ViewCompat.setBackgroundTintList(centurySquareB2ButtonsArray[j], ContextCompat.getColorStateList(Carpark.this, R.color.occupied));
                    }
                    else {
                        ViewCompat.setBackgroundTintList(centurySquareB2ButtonsArray[j], ContextCompat.getColorStateList(Carpark.this, R.color.unoccupied));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Test", "Failed to read value.", error.toException());
                }
            });
        }

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
