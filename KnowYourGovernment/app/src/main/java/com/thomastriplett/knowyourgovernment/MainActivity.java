package com.thomastriplett.knowyourgovernment;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private List<Official> officialList = new ArrayList<>();  // Main content is here

    private RecyclerView recyclerView; // Layout's recyclerview

    private OfficialAdapter mAdapter; // Data to recyclerview adapter

    private Locator locator;

    private String m_Text;

    public TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = findViewById(R.id.location_view);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        mAdapter = new OfficialAdapter(officialList, this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locator = new Locator(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    public void doLocationWork(double latitude, double longitude){
        Geocoder geo = new Geocoder(this);
        try{
            List<Address> address = geo.getFromLocation(latitude, longitude, 1);
            String zipCode = address.get(0).getPostalCode();
            // Create and execute CivicInfoDownloader
            CivicInfoDownloader.running = true;
            new CivicInfoDownloader(this).execute(zipCode);
        }catch (Exception e){
            Toast.makeText(this, "No address can be acquired form those coordinates", Toast.LENGTH_SHORT).show();
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.about_item){
            // send user to about screen
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.search_item){
            // create Alert Dialog that allows user to input city & state or zipcode
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Selection");
            builder.setMessage("Please enter an Address or Zipcode");

            // Set up the input
            final EditText input = new EditText(this);
            //final EditText input2 = new EditText(this);
            builder.setView(input);
            //builder.setView(input2);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    Log.d(TAG, "The input is: "+m_Text);
                    if (!m_Text.equals("")) {
                        Geocoder geo = new Geocoder(MainActivity.this);
                        try{
                            List<Address> address = geo.getFromLocationName(m_Text,1);
                            String zipCode = address.get(0).getPostalCode();
                            if (zipCode != null){
                                Log.d(TAG, "The Geo Result is: "+zipCode);
                                CivicInfoDownloader.running = true;
                                new CivicInfoDownloader(MainActivity.this).execute(zipCode);
                            }
                            else{
                                String city = address.get(0).getLocality();
                                Log.d(TAG, "The Geo Result is: "+city);
                                CivicInfoDownloader.running = true;
                                new CivicInfoDownloader(MainActivity.this).execute(city);

                            }

                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Not a valid address or zipcode", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            m_Text = "";
            return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 1){
            for (int i=0; i<permissions.length; i++){
                if(permissions[i].equals("ACCESS_FINE_LOCATION")){
                    if(grantResults[i] == 1){
                        // Call a Locator method
                        locator.determineLocation();
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                        return;
                    }
                }
            }
        }

    }

    public void setOfficialList(Object[] results){
        if (results != null){
            location.setText((String)results[0]);
            officialList.clear();
            ArrayList<Official> tempList = (ArrayList<Official>)results[1];
            for(int i=0; i<tempList.size(); i++){
                officialList.add(tempList.get(i));
            }
        }
        else{
            location.setText("No Data For Location");
            officialList.clear();
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Official o = officialList.get(pos);
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra("Location",location.getText());
        intent.putExtra("Official", o);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        onClick(view);
        return true;
    }
}
