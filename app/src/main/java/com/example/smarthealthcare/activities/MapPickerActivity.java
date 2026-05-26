package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.io.IOException;
import java.util.List;

import com.example.smarthealthcare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.Manifest;
import android.location.Location;
import android.widget.Toast;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnConfirm;
    private FusedLocationProviderClient fusedLocationClient;
    private SearchView svLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnConfirm = findViewById(R.id.btn_confirm_location);

        svLocation = findViewById(R.id.sv_location);
        svLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (mMap != null) {
                LatLng center = mMap.getCameraPosition().target;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat", center.latitude);
                resultIntent.putExtra("lng", center.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        checkPermissionAndCenterMap();
        
        // Add a small delay check to see if map is actually rendering
        new android.os.Handler().postDelayed(() -> {
            if (mMap == null) {
                Toast.makeText(this, "Map is taking longer than expected. Please check your internet and API Key.", Toast.LENGTH_LONG).show();
            }
        }, 5000);
    }

    private void checkPermissionAndCenterMap() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        try {
                            mMap.setMyLocationEnabled(true);
                            getCurrentLocation();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // Default to India if no permission
                        LatLng defaultLoc = new LatLng(20.5937, 78.9629);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 5f));
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void getCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15f));
                } else {
                    // Default to India if location not found
                    LatLng defaultLoc = new LatLng(20.5937, 78.9629);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 5f));
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show();
        }
    }
}
