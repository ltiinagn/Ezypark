package com.ezypark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        AppCompatAutoCompleteTextView searchCarparks = (AppCompatAutoCompleteTextView) findViewById(R.id.autocomplete);
        String[] carparks = getResources().getStringArray(R.array.carparks);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, carparks);
        searchCarparks.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ezypark-49e23-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference reference = database.getReference();

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayout_info);

        TextView carpark_name = (TextView)findViewById(R.id.carpark_name);
        TextView basic_rate = (TextView)findViewById(R.id.basic_rate);
        TextView available_lots = (TextView)findViewById(R.id.available_lots);
        TextView waiting_cars = (TextView)findViewById(R.id.waiting_cars);
//        Button view_carpark = (Button)findViewById(R.id.view_carpark_button);
//
//        view_carpark.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent myIntent = new Intent(MainActivity.this, Carpark.class);
//                startActivity(myIntent);
//            }
//        });

        searchCarparks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String carpark = searchCarparks.getText().toString();

                DatabaseReference referenceCarpark = reference.child("carparks").child(carpark);
                DatabaseReference referenceBasicRate = referenceCarpark.child("basic_rate");
                DatabaseReference referenceAvailableLots = referenceCarpark.child("available_lots");
                DatabaseReference referenceWaitingCars = referenceCarpark.child("waiting_cars");

                // Read from the database
                referenceBasicRate.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        basic_rate.setText("Basic Rate: "+value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Test", "Failed to read value.", error.toException());
                    }
                });

                referenceAvailableLots.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer value = dataSnapshot.getValue(Integer.class);
                        available_lots.setText("Available Lots: "+value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Test", "Failed to read value.", error.toException());
                    }
                });

                referenceWaitingCars.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer value = dataSnapshot.getValue(Integer.class);
                        waiting_cars.setText("Waiting Cars: "+value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Test", "Failed to read value.", error.toException());
                    }
                });

                carpark_name.setText(carpark);

                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        // referenceAvailableLots.setValue(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}