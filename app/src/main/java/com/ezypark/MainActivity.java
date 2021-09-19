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
        TextView information = (TextView)findViewById(R.id.information);
        FloatingActionButton view_carpark = (FloatingActionButton) findViewById(R.id.view_carpark_button);

        view_carpark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Carpark.class);
                startActivity(myIntent);
            }
        });

        searchCarparks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String carpark = searchCarparks.getText().toString();

                DatabaseReference referenceCarpark = reference.child("carparks").child(carpark);
                DatabaseReference referenceBasicRate = referenceCarpark.child("basic_rate");
                DatabaseReference referenceAvailableLots = referenceCarpark.child("total_avail_lots");
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

                information.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                view_carpark.setVisibility(View.VISIBLE);

                DatabaseReference referencePredData = referenceCarpark.child("pred_data").child("Sun");

                referencePredData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        value = value.substring(2, value.length()-2);
                        String[] valueArray = value.split("\\),\\(");

                        LinearLayout LinearLayout_pred = (LinearLayout)findViewById(R.id.LinearLayout_pred);
                        TextView[] textView_t = new TextView[] {(TextView)findViewById(R.id.textView_t1), (TextView)findViewById(R.id.textView_t2), (TextView)findViewById(R.id.textView_t3)};
                        TextView[] textView_d = new TextView[] {(TextView)findViewById(R.id.textView_d1), (TextView)findViewById(R.id.textView_d2), (TextView)findViewById(R.id.textView_d3)};

                        int currentTime = 1500;
                        int roundedCurrentTime = currentTime/100*100;
                        int count = 0;

                        for (String data : valueArray) {
                            String[] dataArray = data.split(",");
                            int time = Integer.valueOf(dataArray[0]);
                            if (time >= roundedCurrentTime) {
                                String time12Hour = "";
                                if (time >= 1300) {
                                    time12Hour = String.valueOf((time - 1200)/100) + ":" + dataArray[0].substring(2) +"PM";
                                }
                                else if (time < 1300) {
                                    time12Hour = String.valueOf(time/100) + ":" + dataArray[0].substring(2) +"AM";
                                }
                                Log.d("Test", time12Hour);
                                textView_t[count].setText(time12Hour);
                                textView_d[count].setText(dataArray[1].substring(2)+"%");
                                count += 1;
                            }
                            if (count == 3) {
                                break;
                            }
                        }
                        LinearLayout_pred.setVisibility(View.VISIBLE);
//                        if (count < 3) {
//                            DatabaseReference referencePredData = referenceCarpark.child("pred_data").child("Mon");
//
//                            referencePredData.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    String value = dataSnapshot.getValue(String.class);
//                                    value = value.substring(2, value.length()-2);
//                                    String[] valueArray = value.split("\\),\\(");
//
//                                    for (String data : valueArray) {
//                                        String[] dataArray = data.split(",");
//                                        int time = Integer.valueOf(dataArray[0]);
//                                        if (time >= roundedCurrentTime) {
//                                            textView_t[count].setText(dataArray[0]);
//                                            textView_d[count].setText(dataArray[1]);
//                                            count += 1
//                                        }
//                                        if (count == 3) {
//                                            break;
//                                        }
//                                    }
//                                    if (count < 3) {
//                                        DatabaseReference referencePredData = referenceCarpark.child("pred_data").child("Sun");
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError error) {
//                                    Log.w("Test", "Failed to read value.", error.toException());
//                                }
//                            });
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Test", "Failed to read value.", error.toException());
                    }
                });
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