package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    TextView tv0, tv1, tv2, tv3;
    Button btn0;


    public void onlocationChanged(Location location){


            if (location.hasAccuracy()) {
                tv0.setText("Accuracy: " + String.valueOf(location.getAccuracy()));
            }
            if (location.hasAltitude()) {
                tv1.setText("Latitude: " + String.valueOf(location.getLatitude()));
                tv2.setText("Longitude: " + String.valueOf(location.getLongitude()));
            }
            Context context;
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                String address = "";

                List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (listAddress.get(0).getAddressLine(0) != null) {
                    address += ", " + listAddress.get(0).getAddressLine(0).toString();
                }


                tv3.setText("Address: " + address);
            } catch (IOException e) {

                e.printStackTrace();

            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv0 = findViewById(R.id.tv0);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        btn0 = findViewById(R.id.btn0);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                isGPSON();
                onlocationChanged(location);
            }

        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

    }

    boolean isGPSON() {
        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            Context context;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("Settings")
                    .setMessage("Your GPS is off. Do you want to turn it on?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            alertDialogBuilder.show();
        }
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public void turnGPS(View view) {
        if (!isGPSON()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                onlocationChanged(locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER));
            }
        }
        else{
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,10000,1,locationListener);
        }

    }
}