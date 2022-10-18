package com.example.Swapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tonyakitori.inc.easyroutes.EasyRoutesDirections;
import com.tonyakitori.inc.easyroutes.EasyRoutesDrawer;
import com.tonyakitori.inc.easyroutes.enums.TransportationMode;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import Swapp.R;
import Swapp.databinding.FragmentMapsBinding;
import maes.tech.intentanim.CustomIntent;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    FragmentMapsBinding binding;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private MarkerOptions markerOptions;
    private Circle circle;
    FirebaseAuth firebaseAuth;
    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfrag);
        mapFragment.getMapAsync(this);

        mapInitialize();

        return binding.getRoot();
    }

    private void mapInitialize() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(2000);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        binding.confirmBtn.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        bundle = getArguments();

        String category = bundle.getString("category");
        String from = bundle.getString("from");

        mMap = googleMap;

        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }


        if (!gps_enabled) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Location service is required.")
                    .setMessage("Enable your location service in device settings or return to categories.")
                    .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Return to categories", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getContext(), Categories.class));
                            CustomIntent.customType(getContext(), "right-to-left");
                        }
                    })
                    .show();
        } else {
            Dexter.withContext(getContext())
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {

                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }

                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {

                                    ArrayList<String> keys = new ArrayList<>();
                                    HashMap<String, Marker> hashMapMarker = new HashMap<>();

                                    HashMap<String, Marker> hashMapMarker1 = new HashMap<>();

                                    String hideSearch = bundle.getString("from");

                                    if (hideSearch.equals("categories")) {

                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                        binding.getRadius.setVisibility(View.VISIBLE);
                                        binding.currentAddressLayout.setVisibility(View.GONE);
                                        binding.searchLocation.setVisibility(View.GONE);
                                        binding.searchNearByLocation.setVisibility(View.VISIBLE);

                                        binding.searchNearByLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                            @Override
                                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                                                    goToNearbyLocation(mMap, location.getLatitude(), location.getLongitude());
                                                }
                                                return false;
                                            }
                                        });

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                        if (category.equals("all")) {

                                            databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                            binding.confirmBtn.setVisibility(View.VISIBLE);

                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)));

                                                            String oldKey = dataSnapshot.getKey() + "-" + dataSnapshot1.getKey();

                                                            if (!hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                markerOptions = new MarkerOptions();
                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                markerOptions.position(latLng1);

                                                                marker = mMap.addMarker(markerOptions);
                                                                hashMapMarker.put(oldKey, marker);

                                                                keys.add(oldKey);

                                                                Set<String> set = new HashSet<>(keys);
                                                                keys.clear();
                                                                keys.addAll(set);

                                                            } else if (hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                marker = hashMapMarker.get(oldKey);
                                                                marker.remove();
                                                                hashMapMarker.remove(oldKey);
                                                                keys.remove(oldKey);
                                                            }

                                                            binding.goToMyLocation.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    gotoLatLng(location.getLatitude(), location.getLongitude(), 12.5f);
                                                                }
                                                            });

                                                            binding.chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                                                @Override
                                                                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                                                                    if (checkedIds.size() > 0) {
                                                                        Chip selectedChip = group.findViewById(checkedIds.get(0));

                                                                        hashMapMarker.clear();
                                                                        mMap.clear();
                                                                        keys.clear();

                                                                        float[] distance = new float[2];

                                                                        databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                float newRadius = 0f;
                                                                                float newZoom = 0f;

                                                                                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                                                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {

                                                                                        if (circle != null) {
                                                                                            circle.remove();
                                                                                        }

                                                                                        switch (selectedChip.getText().toString()) {
                                                                                            case "5 km":
                                                                                                newRadius = 5000f;
                                                                                                newZoom = 12.5f;
                                                                                                break;
                                                                                            case "10 km":
                                                                                                newRadius = 10000f;
                                                                                                newZoom = 11.5f;
                                                                                                break;
                                                                                            case "15 km":
                                                                                                newRadius = 15000f;
                                                                                                newZoom = 10.8f;
                                                                                                break;
                                                                                            case "20 km":
                                                                                                newRadius = 20000f;
                                                                                                newZoom = 10.4f;
                                                                                                break;
                                                                                            case "25 km":
                                                                                                newRadius = 25000f;
                                                                                                newZoom = 10f;
                                                                                                break;
                                                                                            case "30 km":
                                                                                                newRadius = 30000f;
                                                                                                newZoom = 9.8f;
                                                                                                break;
                                                                                        }

                                                                                        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                                        circle = googleMap.addCircle(circleOptions);

                                                                                        LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)));

                                                                                        Location.distanceBetween(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)),
                                                                                                circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                                                        String oldKey = dataSnapshot2.getKey() + "-" + dataSnapshot3.getKey();

                                                                                        if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                            markerOptions = new MarkerOptions();
                                                                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                                            markerOptions.position(latLng1);

                                                                                            marker = mMap.addMarker(markerOptions);
                                                                                            hashMapMarker.put(oldKey, marker);

                                                                                            keys.add(oldKey);

                                                                                            Set<String> set = new HashSet<>(keys);
                                                                                            keys.clear();
                                                                                            keys.addAll(set);

                                                                                        } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                            marker = hashMapMarker.get(oldKey);
                                                                                            marker.remove();
                                                                                            hashMapMarker.remove(oldKey);
                                                                                            keys.remove(oldKey);
                                                                                        }
                                                                                    }
                                                                                }

                                                                                gotoLatLng(location.getLatitude(), location.getLongitude(), newZoom);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                    } else {

                                                                        circle.remove();

                                                                        hashMapMarker.clear();
                                                                        mMap.clear();

                                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                                        databaseReference1.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                                                    for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {

                                                                                        binding.confirmBtn.setVisibility(View.VISIBLE);

                                                                                        LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot5.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot5.child("Address").child("Longitude").getValue(String.class)));

                                                                                        String oldKey = dataSnapshot4.getKey() + "-" + dataSnapshot5.getKey();

                                                                                        if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                            markerOptions = new MarkerOptions();
                                                                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                                            markerOptions.position(latLng1);

                                                                                            marker = mMap.addMarker(markerOptions);
                                                                                            hashMapMarker.put(oldKey, marker);

                                                                                            keys.add(oldKey);

                                                                                            Set<String> set = new HashSet<>(keys);
                                                                                            keys.clear();
                                                                                            keys.addAll(set);

                                                                                        } else if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                            marker = hashMapMarker.get(oldKey);
                                                                                            marker.remove();
                                                                                            hashMapMarker.remove(oldKey);
                                                                                            keys.remove(oldKey);
                                                                                        }
                                                                                    }
                                                                                }

                                                                                gotoLatLng(location.getLatitude(), location.getLongitude(), 9.7f);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {
                                            databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                            if (dataSnapshot1.child("Item_Category").getValue(String.class).equals(category)) {

                                                                binding.confirmBtn.setVisibility(View.VISIBLE);

                                                                LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)));

                                                                String oldKey = dataSnapshot.getKey() + "-" + dataSnapshot1.getKey();

                                                                if (!hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                    markerOptions = new MarkerOptions();
                                                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                    markerOptions.position(latLng1);

                                                                    marker = mMap.addMarker(markerOptions);
                                                                    hashMapMarker.put(oldKey, marker);

                                                                    keys.add(oldKey);

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);

                                                                } else if (hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                    marker = hashMapMarker.get(oldKey);
                                                                    marker.remove();
                                                                    hashMapMarker.remove(oldKey);
                                                                    keys.remove(oldKey);
                                                                }
                                                            }

                                                            binding.goToMyLocation.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    gotoLatLng(location.getLatitude(), location.getLongitude(), 12.5f);
                                                                }
                                                            });

                                                            binding.chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                                                @Override
                                                                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                                                                    if (checkedIds.size() > 0) {
                                                                        Chip selectedChip = group.findViewById(checkedIds.get(0));

                                                                        hashMapMarker.clear();
                                                                        mMap.clear();
                                                                        keys.clear();

                                                                        float[] distance = new float[2];

                                                                        databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                float newRadius = 0f;
                                                                                float newZoom = 0f;

                                                                                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                                                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                                                                        if (dataSnapshot3.child("Item_Category").getValue(String.class).equals(category)) {

                                                                                            if (circle != null) {
                                                                                                circle.remove();
                                                                                            }

                                                                                            switch (selectedChip.getText().toString()) {
                                                                                                case "5 km":
                                                                                                    newRadius = 5000f;
                                                                                                    newZoom = 12.5f;
                                                                                                    break;
                                                                                                case "10 km":
                                                                                                    newRadius = 10000f;
                                                                                                    newZoom = 11.5f;
                                                                                                    break;
                                                                                                case "15 km":
                                                                                                    newRadius = 15000f;
                                                                                                    newZoom = 10.8f;
                                                                                                    break;
                                                                                                case "20 km":
                                                                                                    newRadius = 20000f;
                                                                                                    newZoom = 10.4f;
                                                                                                    break;
                                                                                                case "25 km":
                                                                                                    newRadius = 25000f;
                                                                                                    newZoom = 10f;
                                                                                                    break;
                                                                                                case "30 km":
                                                                                                    newRadius = 30000f;
                                                                                                    newZoom = 9.8f;
                                                                                                    break;
                                                                                            }

                                                                                            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                                            circle = googleMap.addCircle(circleOptions);

                                                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)));

                                                                                            Location.distanceBetween(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)),
                                                                                                    circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                                                            String oldKey = dataSnapshot2.getKey() + "-" + dataSnapshot3.getKey();

                                                                                            if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                                markerOptions = new MarkerOptions();
                                                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                                                markerOptions.position(latLng1);

                                                                                                marker = mMap.addMarker(markerOptions);
                                                                                                hashMapMarker.put(oldKey, marker);

                                                                                                keys.add(oldKey);

                                                                                                Set<String> set = new HashSet<>(keys);
                                                                                                keys.clear();
                                                                                                keys.addAll(set);

                                                                                            } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                                marker = hashMapMarker.get(oldKey);
                                                                                                marker.remove();
                                                                                                hashMapMarker.remove(oldKey);
                                                                                                keys.remove(oldKey);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }

                                                                                gotoLatLng(location.getLatitude(), location.getLongitude(), newZoom);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                    } else {

                                                                        circle.remove();

                                                                        hashMapMarker.clear();
                                                                        mMap.clear();

                                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                                        databaseReference1.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                                                    for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                                        if (dataSnapshot5.child("Item_Category").getValue(String.class).equals(category)) {

                                                                                            binding.confirmBtn.setVisibility(View.VISIBLE);

                                                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot5.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot5.child("Address").child("Longitude").getValue(String.class)));

                                                                                            String oldKey = dataSnapshot4.getKey() + "-" + dataSnapshot5.getKey();

                                                                                            if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                                markerOptions = new MarkerOptions();
                                                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                                                markerOptions.position(latLng1);

                                                                                                marker = mMap.addMarker(markerOptions);
                                                                                                hashMapMarker.put(oldKey, marker);

                                                                                                keys.add(oldKey);

                                                                                                Set<String> set = new HashSet<>(keys);
                                                                                                keys.clear();
                                                                                                keys.addAll(set);

                                                                                            } else if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                                marker = hashMapMarker.get(oldKey);
                                                                                                marker.remove();
                                                                                                hashMapMarker.remove(oldKey);
                                                                                                keys.remove(oldKey);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }

                                                                                gotoLatLng(location.getLatitude(), location.getLongitude(), 9.7f);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                                    } else if (hideSearch.equals("postitem")) {
                                        binding.getRadius.setVisibility(View.GONE);
                                        binding.currentAddressLayout.setVisibility(View.VISIBLE);
                                        binding.searchLocation.setVisibility(View.VISIBLE);
                                        binding.searchNearByLocation.setVisibility(View.GONE);

                                        binding.searchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                            @Override
                                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                                                    goToSearchLocation();
                                                }

                                                return false;
                                            }
                                        });

                                        try {
                                            Geocoder geo = new Geocoder(getContext());
                                            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                            if (addresses.isEmpty()) {
                                                binding.currentAddress.setText("Waiting for Location");
                                            } else {
                                                if (addresses.size() > 0) {
                                                    binding.currentAddress.setText(addresses.get(0).getAddressLine(0));
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f));

                                        if (marker != null) {

                                            marker.remove();

                                        }

                                        markerOptions = new MarkerOptions();
                                        markerOptions.draggable(true);
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                                        marker = mMap.addMarker(markerOptions);
                                    } else if (hideSearch.equals("getDirection")) {
                                        binding.getRadius.setVisibility(View.GONE);
                                        binding.currentAddressLayout.setVisibility(View.GONE);
                                        binding.searchLocation.setVisibility(View.GONE);
                                        binding.searchNearByLocation.setVisibility(View.GONE);

                                        gotoLatLng(location.getLatitude(), location.getLongitude(), 15f);

                                        binding.goToMyLocation.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                gotoLatLng(location.getLatitude(), location.getLongitude(), 15f);
                                            }
                                        });

                                        Double destLatitude = Double.parseDouble(bundle.getString("latitude"));
                                        Double destLongitude = Double.parseDouble(bundle.getString("longitude"));

                                        LatLng destination = new LatLng(destLatitude, destLongitude);

                                        MarkerOptions destinationMarker = new MarkerOptions();
                                        MarkerOptions originMarker = new MarkerOptions();
                                        destinationMarker.position(destination);
                                        originMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));

                                        mMap.addMarker(destinationMarker);
                                        mMap.addMarker(originMarker);

                                        List<LatLng> path = new ArrayList();

                                        String origin = originMarker.getPosition().latitude + "," + originMarker.getPosition().longitude;
                                        String destination1 = destinationMarker.getPosition().latitude + "," + destinationMarker.getPosition().longitude;

                                        Log.w("Route", destination1);

                                        try {
                                            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + origin + "/" + destination1);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            intent.setPackage("com.google.android.apps.maps");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }


                                    }

                                    binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getContext(), ItemSwipe.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.putExtra("keys", keys);
                                            startActivity(intent);
                                            CustomIntent.customType(getContext(), "left-to-right");
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            Toast.makeText(getContext(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), Categories.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            CustomIntent.customType(getContext(), "right-to-left");
                        }
                    }).check();

            if (mMap != null) {
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(@NonNull Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(@NonNull Marker marker) {
                        Geocoder geocoder = new Geocoder(getContext());
                        List<Address> list = null;
                        LatLng markerPosition = marker.getPosition();
                        try {

                            list = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                            Address address = list.get(0);
                            marker.setTitle(address.getAdminArea());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Geocoder geo = new Geocoder(getContext());
                            List<Address> addresses = geo.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                            if (addresses.isEmpty()) {
                                binding.currentAddress.setText("Waiting for Location");
                            } else {
                                if (addresses.size() > 0) {
                                    binding.currentAddress.setText(addresses.get(0).getAddressLine(0));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull Marker marker) {

                    }
                });
            }

            String hideSearch = bundle.getString("from");

            if (hideSearch.equals("postitem")) {

                String category1 = bundle.getString("category1");

                binding.confirmLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Geocoder geocoder = new Geocoder(getContext());
                        List<Address> list = null;
                        LatLng markerPosition = marker.getPosition();

                        try {
                            List<Address> addresses = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                            String latitude = String.valueOf(markerPosition.latitude);
                            String longitude = String.valueOf(markerPosition.longitude);

                            String isOffer = bundle.getString("Offers");

                            Log.d("TAG", isOffer);
                            if (isOffer.equals("true")) {
                                Intent intent = new Intent(getContext(), offerItem_S3.class);


                                switch (category1) {
                                    case "Men's Apparel":

                                        intent.putExtra("mensSizeChart", bundle.getString("mensSizeChart"));
                                        intent.putExtra("mensClothingType", bundle.getString("mensClothingType"));
                                        intent.putExtra("mensBrand", bundle.getString("mensBrand"));
                                        intent.putExtra("mensColor", bundle.getString("mensColor"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("mensMaterial", bundle.getString("mensMaterial"));
                                        intent.putExtra("mensUsage", bundle.getString("mensUsage"));
                                        intent.putExtra("mensSizes", bundle.getString("mensSizes"));
                                        intent.putExtra("mensDescription", bundle.getString("mensDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Gadgets":

                                        intent.putExtra("gadgetType", bundle.getString("gadgetType"));
                                        intent.putExtra("gadgetBrand", bundle.getString("gadgetBrand"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("gadgetColor", bundle.getString("gadgetColor"));
                                        intent.putExtra("gadgetUsage", bundle.getString("gadgetUsage"));
                                        intent.putExtra("gadgetDescription", bundle.getString("gadgetDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Game":

                                        intent.putExtra("gameType", bundle.getString("gameType"));
                                        intent.putExtra("gameBrand", bundle.getString("gameBrand"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("gameUsage", bundle.getString("gameUsage"));
                                        intent.putExtra("gameDescription", bundle.getString("gameDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Bags":

                                        intent.putExtra("bagType", bundle.getString("bagType"));
                                        intent.putExtra("bagBrand", bundle.getString("bagBrand"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("bagColor", bundle.getString("bagColor"));
                                        intent.putExtra("bagUsage", bundle.getString("bagUsage"));
                                        intent.putExtra("bagDescription", bundle.getString("bagDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Groceries":

                                        intent.putExtra("groceryList", bundle.getString("groceryList"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Furniture":

                                        intent.putExtra("furnitureBrand", bundle.getString("furnitureBrand"));
                                        intent.putExtra("furnitureColor", bundle.getString("furnitureColor"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("furnitureUsage", bundle.getString("furnitureUsage"));
                                        intent.putExtra("furnitureHeight", bundle.getString("furnitureHeight"));
                                        intent.putExtra("furnitureWidth", bundle.getString("furnitureWidth"));
                                        intent.putExtra("furnitureLength", bundle.getString("furnitureLength"));
                                        intent.putExtra("furnitureDescription", bundle.getString("furnitureDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Babies & Kids":

                                        intent.putExtra("bnkAge", bundle.getString("bnkAge"));
                                        intent.putExtra("bnkBrand", bundle.getString("bnkBrand"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("bnkType", bundle.getString("bnkType"));
                                        intent.putExtra("bnkUsage", bundle.getString("bnkUsage"));
                                        intent.putExtra("bnkDescription", bundle.getString("bnkDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Appliances":

                                        intent.putExtra("appliancesType", bundle.getString("appliancesType"));
                                        intent.putExtra("appliancesBrand", bundle.getString("appliancesBrand"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("appliancesColor", bundle.getString("appliancesColor"));
                                        intent.putExtra("appliancesUsage", bundle.getString("appliancesUsage"));
                                        intent.putExtra("appliancesDescription", bundle.getString("appliancesDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Motors":

                                        intent.putExtra("motorModel", bundle.getString("motorModel"));
                                        intent.putExtra("motorBrand", bundle.getString("motorBrand"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("motorColor", bundle.getString("motorColor"));
                                        intent.putExtra("motorUsage", bundle.getString("motorUsage"));
                                        intent.putExtra("motorDescription", bundle.getString("motorDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Audio":

                                        intent.putExtra("audioArtist", bundle.getString("audioArtist"));
                                        intent.putExtra("audioReleaseDate", bundle.getString("audioReleaseDate"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("audioUsage", bundle.getString("audioUsage"));
                                        intent.putExtra("audioDescription", bundle.getString("audioDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "School":

                                        intent.putExtra("schoolType", bundle.getString("schoolType"));
                                        intent.putExtra("schoolBrand", bundle.getString("schoolBrand"));
                                        intent.putExtra("schoolColor", bundle.getString("schoolColor"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("schoolUsage", bundle.getString("schoolUsage"));
                                        intent.putExtra("schoolDescription", bundle.getString("schoolDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Women's Apparel":

                                        intent.putExtra("womensSizeChart", bundle.getString("womensSizeChart"));
                                        intent.putExtra("womensClothingType", bundle.getString("womensClothingType"));
                                        intent.putExtra("womensBrand", bundle.getString("womensBrand"));
                                        intent.putExtra("womensColor", bundle.getString("womensColor"));
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("womensMaterial", bundle.getString("womensMaterial"));
                                        intent.putExtra("womensUsage", bundle.getString("womensUsage"));
                                        intent.putExtra("womensSizes", bundle.getString("womensSizes"));
                                        intent.putExtra("womensDescription", bundle.getString("womensDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Others":

                                        intent.putExtra("otherType", bundle.getString("otherType"));
                                        intent.putExtra("otherDescription", bundle.getString("otherDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("uid", bundle.getString("uid"));
                                        intent.putExtra("itemname", bundle.getString("itemname"));
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));


                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                }
                            } else {
                                Intent intent = new Intent(getContext(), PostItem_S3.class);


                                switch (category1) {
                                    case "Men's Apparel":

                                        intent.putExtra("mensClothingType", bundle.getString("mensClothingType"));
                                        intent.putExtra("mensBrand", bundle.getString("mensBrand"));
                                        intent.putExtra("mensColor", bundle.getString("mensColor"));
                                        intent.putExtra("mensSizeChart", bundle.getString("mensSizeChart"));
                                        intent.putExtra("mensMaterial", bundle.getString("mensMaterial"));
                                        intent.putExtra("mensUsage", bundle.getString("mensUsage"));
                                        intent.putExtra("mensSizes", bundle.getString("mensSizes"));
                                        intent.putExtra("mensDescription", bundle.getString("mensDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Gadgets":

                                        intent.putExtra("gadgetType", bundle.getString("gadgetType"));
                                        intent.putExtra("gadgetBrand", bundle.getString("gadgetBrand"));

                                        intent.putExtra("gadgetColor", bundle.getString("gadgetColor"));
                                        intent.putExtra("gadgetUsage", bundle.getString("gadgetUsage"));
                                        intent.putExtra("gadgetDescription", bundle.getString("gadgetDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Game":

                                        intent.putExtra("gameType", bundle.getString("gameType"));
                                        intent.putExtra("gameBrand", bundle.getString("gameBrand"));

                                        intent.putExtra("gameUsage", bundle.getString("gameUsage"));
                                        intent.putExtra("gameDescription", bundle.getString("gameDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Bags":

                                        intent.putExtra("bagType", bundle.getString("bagType"));
                                        intent.putExtra("bagBrand", bundle.getString("bagBrand"));

                                        intent.putExtra("bagColor", bundle.getString("bagColor"));
                                        intent.putExtra("bagUsage", bundle.getString("bagUsage"));
                                        intent.putExtra("bagDescription", bundle.getString("bagDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Groceries":

                                        intent.putExtra("groceryList", bundle.getString("groceryList"));
                                        intent.putExtra("city", addresses.get(0).getLocality());

                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));

                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));
                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Furniture":

                                        intent.putExtra("furnitureBrand", bundle.getString("furnitureBrand"));
                                        intent.putExtra("furnitureColor", bundle.getString("furnitureColor"));

                                        intent.putExtra("furnitureUsage", bundle.getString("furnitureUsage"));
                                        intent.putExtra("furnitureHeight", bundle.getString("furnitureHeight"));
                                        intent.putExtra("furnitureWidth", bundle.getString("furnitureWidth"));
                                        intent.putExtra("furnitureLength", bundle.getString("furnitureLength"));
                                        intent.putExtra("furnitureDescription", bundle.getString("furnitureDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Babies & Kids":

                                        intent.putExtra("bnkAge", bundle.getString("bnkAge"));
                                        intent.putExtra("bnkBrand", bundle.getString("bnkBrand"));

                                        intent.putExtra("bnkType", bundle.getString("bnkType"));
                                        intent.putExtra("bnkUsage", bundle.getString("bnkUsage"));
                                        intent.putExtra("bnkDescription", bundle.getString("bnkDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Appliances":

                                        intent.putExtra("appliancesType", bundle.getString("appliancesType"));
                                        intent.putExtra("appliancesBrand", bundle.getString("appliancesBrand"));

                                        intent.putExtra("appliancesColor", bundle.getString("appliancesColor"));
                                        intent.putExtra("appliancesUsage", bundle.getString("appliancesUsage"));
                                        intent.putExtra("appliancesDescription", bundle.getString("appliancesDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Motors":

                                        intent.putExtra("motorModel", bundle.getString("motorModel"));
                                        intent.putExtra("motorBrand", bundle.getString("motorBrand"));

                                        intent.putExtra("motorColor", bundle.getString("motorColor"));
                                        intent.putExtra("motorUsage", bundle.getString("motorUsage"));
                                        intent.putExtra("motorDescription", bundle.getString("motorDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Audio":

                                        intent.putExtra("audioArtist", bundle.getString("audioArtist"));
                                        intent.putExtra("audioReleaseDate", bundle.getString("audioReleaseDate"));

                                        intent.putExtra("audioUsage", bundle.getString("audioUsage"));
                                        intent.putExtra("audioDescription", bundle.getString("audioDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));

                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));
                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "School":

                                        intent.putExtra("schoolType", bundle.getString("schoolType"));
                                        intent.putExtra("schoolBrand", bundle.getString("schoolBrand"));
                                        intent.putExtra("schoolColor", bundle.getString("schoolColor"));

                                        intent.putExtra("schoolUsage", bundle.getString("schoolUsage"));
                                        intent.putExtra("schoolDescription", bundle.getString("schoolDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Women's Apparel":

                                        intent.putExtra("womensClothingType", bundle.getString("womensClothingType"));
                                        intent.putExtra("womensBrand", bundle.getString("womensBrand"));
                                        intent.putExtra("womensColor", bundle.getString("womensColor"));
                                        intent.putExtra("womensSizeChart", bundle.getString("womensSizeChart"));
                                        intent.putExtra("womensMaterial", bundle.getString("womensMaterial"));
                                        intent.putExtra("womensUsage", bundle.getString("womensUsage"));
                                        intent.putExtra("womensSizes", bundle.getString("womensSizes"));
                                        intent.putExtra("womensDescription", bundle.getString("womensDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());
                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                    case "Others":

                                        intent.putExtra("otherType", bundle.getString("otherType"));
                                        intent.putExtra("otherDescription", bundle.getString("otherDescription"));
                                        intent.putExtra("city", addresses.get(0).getLocality());

                                        intent.putExtra("state", addresses.get(0).getAdminArea());
                                        intent.putExtra("country", addresses.get(0).getCountryName());
                                        intent.putExtra("houseNo", addresses.get(0).getFeatureName());
                                        intent.putExtra("street", addresses.get(0).getThoroughfare());
                                        intent.putExtra("brgy", addresses.get(0).getSubLocality());
                                        intent.putExtra("currentState", "postLocation");
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        intent.putExtra("item_name", bundle.getString("item_name"));
                                        intent.putExtra("rft", bundle.getString("rft"));
                                        intent.putExtra("pref_item", bundle.getString("pref_item"));

                                        intent.putExtra("category", bundle.getString("category1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(getContext(), "right-to-left");

                                        break;
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
    }

    private void goToSearchLocation() {

        String searchLocation = binding.searchLocation.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (list.size() > 0) {
            Address address = list.get(0);
            String location = address.getAdminArea();
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            gotoLatLng(latitude, longitude, 17f);

            if (marker != null) {
                marker.remove();
            }

            markerOptions = new MarkerOptions();
            markerOptions.title(location);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.position(new LatLng(latitude, longitude));
            marker = mMap.addMarker(markerOptions);
        }

        LatLng markerPosition = marker.getPosition();

        try {
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
            if (addresses.isEmpty()) {
                binding.currentAddress.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    binding.currentAddress.setText(addresses.get(0).getAddressLine(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToNearbyLocation(GoogleMap googleMap, Double myLatitude, Double myLongitude) {

        mMap.clear();
        String searchLocation = binding.searchNearByLocation.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        String uid = firebaseAuth.getCurrentUser().getUid();

        ArrayList<String> keys = new ArrayList<>();
        HashMap<String, Marker> hashMapMarker = new HashMap<>();

        float[] distance = new float[2];

        bundle = getArguments();

        binding.chipGroup.clearCheck();

        String category = bundle.getString("category");

        try {
            list = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            gotoLatLng(latitude, longitude, 12.5f);

            LatLng latLng = new LatLng(latitude, longitude);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            if (category.equals("all")) {

                databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                binding.confirmBtn.setVisibility(View.VISIBLE);

                                if (circle != null) {
                                    circle.remove();
                                }

                                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(5000f).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                circle = googleMap.addCircle(circleOptions);

                                LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)));

                                Location.distanceBetween(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)),
                                        circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                String oldKey = dataSnapshot.getKey() + "-" + dataSnapshot1.getKey();

                                if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                    markerOptions = new MarkerOptions();
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    markerOptions.position(latLng1);

                                    marker = mMap.addMarker(markerOptions);
                                    hashMapMarker.put(oldKey, marker);

                                    keys.add(oldKey);

                                    Set<String> set = new HashSet<>(keys);
                                    keys.clear();
                                    keys.addAll(set);

                                } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                    marker = hashMapMarker.get(oldKey);
                                    marker.remove();
                                    hashMapMarker.remove(oldKey);
                                    keys.remove(oldKey);
                                }

                                binding.goToMyLocation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        mMap.clear();
                                        keys.clear();
                                        hashMapMarker.clear();

                                        LatLng myLatLng = new LatLng(myLatitude, myLongitude);

                                        float[] distance = new float[2];

                                        if (circle != null) {
                                            circle.remove();
                                        }

                                        CircleOptions circleOptions = new CircleOptions().center(myLatLng).radius(5000f).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                        circle = googleMap.addCircle(circleOptions);

                                        LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)));

                                        Location.distanceBetween(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)),
                                                circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                        String oldKey = dataSnapshot.getKey() + "-" + dataSnapshot1.getKey();

                                        if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                            markerOptions = new MarkerOptions();
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                            markerOptions.position(latLng1);

                                            marker = mMap.addMarker(markerOptions);
                                            hashMapMarker.put(oldKey, marker);

                                            keys.add(oldKey);

                                            Set<String> set = new HashSet<>(keys);
                                            keys.clear();
                                            keys.addAll(set);

                                        } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                            marker = hashMapMarker.get(oldKey);
                                            marker.remove();
                                            hashMapMarker.remove(oldKey);
                                            keys.remove(oldKey);
                                        }

                                        binding.chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                                                if (checkedIds.size() > 0) {
                                                    Chip selectedChip = group.findViewById(checkedIds.get(0));

                                                    hashMapMarker.clear();
                                                    mMap.clear();
                                                    keys.clear();

                                                    float[] distance = new float[2];

                                                    databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                            float newRadius = 0f;
                                                            float newZoom = 0f;

                                                            for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                                for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {

                                                                    if (circle != null) {
                                                                        circle.remove();
                                                                    }

                                                                    switch (selectedChip.getText().toString()) {
                                                                        case "5 km":
                                                                            newRadius = 5000f;
                                                                            newZoom = 12.5f;
                                                                            break;
                                                                        case "10 km":
                                                                            newRadius = 10000f;
                                                                            newZoom = 11.5f;
                                                                            break;
                                                                        case "15 km":
                                                                            newRadius = 15000f;
                                                                            newZoom = 10.8f;
                                                                            break;
                                                                        case "20 km":
                                                                            newRadius = 20000f;
                                                                            newZoom = 10.4f;
                                                                            break;
                                                                        case "25 km":
                                                                            newRadius = 25000f;
                                                                            newZoom = 10f;
                                                                            break;
                                                                        case "30 km":
                                                                            newRadius = 30000f;
                                                                            newZoom = 9.8f;
                                                                            break;
                                                                    }

                                                                    CircleOptions circleOptions = new CircleOptions().center(myLatLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                    circle = googleMap.addCircle(circleOptions);

                                                                    LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)));

                                                                    Location.distanceBetween(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)),
                                                                            circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                                    String oldKey = dataSnapshot2.getKey() + "-" + dataSnapshot3.getKey();

                                                                    if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                        markerOptions = new MarkerOptions();
                                                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                        markerOptions.position(latLng1);

                                                                        marker = mMap.addMarker(markerOptions);
                                                                        hashMapMarker.put(oldKey, marker);

                                                                        keys.add(oldKey);

                                                                        Set<String> set = new HashSet<>(keys);
                                                                        keys.clear();
                                                                        keys.addAll(set);

                                                                    } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                        marker = hashMapMarker.get(oldKey);
                                                                        marker.remove();
                                                                        hashMapMarker.remove(oldKey);
                                                                        keys.remove(oldKey);
                                                                    }
                                                                }
                                                            }

                                                            gotoLatLng(myLatitude, myLongitude, newZoom);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                } else {

                                                    circle.remove();

                                                    hashMapMarker.clear();
                                                    mMap.clear();

                                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                    databaseReference1.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                            for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                                for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {

                                                                    binding.confirmBtn.setVisibility(View.VISIBLE);

                                                                    LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot5.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot5.child("Address").child("Longitude").getValue(String.class)));

                                                                    String oldKey = dataSnapshot4.getKey() + "-" + dataSnapshot5.getKey();

                                                                    if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                        markerOptions = new MarkerOptions();
                                                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                        markerOptions.position(latLng1);

                                                                        marker = mMap.addMarker(markerOptions);
                                                                        hashMapMarker.put(oldKey, marker);

                                                                        keys.add(oldKey);

                                                                        Set<String> set = new HashSet<>(keys);
                                                                        keys.clear();
                                                                        keys.addAll(set);

                                                                    } else if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                        marker = hashMapMarker.get(oldKey);
                                                                        marker.remove();
                                                                        hashMapMarker.remove(oldKey);
                                                                        keys.remove(oldKey);
                                                                    }
                                                                }
                                                            }

                                                            gotoLatLng(myLatitude, myLongitude, 9.7f);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        });

                                        gotoLatLng(myLatitude, myLongitude, 12.5f);
                                    }
                                });

                                binding.chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                    @Override
                                    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                                        if (checkedIds.size() > 0) {
                                            Chip selectedChip = group.findViewById(checkedIds.get(0));

                                            hashMapMarker.clear();
                                            mMap.clear();
                                            keys.clear();

                                            float[] distance = new float[2];

                                            databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    float newRadius = 0f;
                                                    float newZoom = 0f;

                                                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {

                                                            if (circle != null) {
                                                                circle.remove();
                                                            }

                                                            switch (selectedChip.getText().toString()) {
                                                                case "5 km":
                                                                    newRadius = 5000f;
                                                                    newZoom = 12.5f;
                                                                    break;
                                                                case "10 km":
                                                                    newRadius = 10000f;
                                                                    newZoom = 11.5f;
                                                                    break;
                                                                case "15 km":
                                                                    newRadius = 15000f;
                                                                    newZoom = 10.8f;
                                                                    break;
                                                                case "20 km":
                                                                    newRadius = 20000f;
                                                                    newZoom = 10.4f;
                                                                    break;
                                                                case "25 km":
                                                                    newRadius = 25000f;
                                                                    newZoom = 10f;
                                                                    break;
                                                                case "30 km":
                                                                    newRadius = 30000f;
                                                                    newZoom = 9.8f;
                                                                    break;
                                                            }

                                                            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                            circle = googleMap.addCircle(circleOptions);

                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)));

                                                            Location.distanceBetween(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)),
                                                                    circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                            String oldKey = dataSnapshot2.getKey() + "-" + dataSnapshot3.getKey();

                                                            if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                markerOptions = new MarkerOptions();
                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                markerOptions.position(latLng1);

                                                                marker = mMap.addMarker(markerOptions);
                                                                hashMapMarker.put(oldKey, marker);

                                                                keys.add(oldKey);

                                                                Set<String> set = new HashSet<>(keys);
                                                                keys.clear();
                                                                keys.addAll(set);

                                                            } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                marker = hashMapMarker.get(oldKey);
                                                                marker.remove();
                                                                hashMapMarker.remove(oldKey);
                                                                keys.remove(oldKey);
                                                            }
                                                        }
                                                    }

                                                    gotoLatLng(latitude, longitude, newZoom);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {

                                            circle.remove();

                                            hashMapMarker.clear();
                                            mMap.clear();

                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                            databaseReference1.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                        for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {

                                                            binding.confirmBtn.setVisibility(View.VISIBLE);

                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot5.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot5.child("Address").child("Longitude").getValue(String.class)));

                                                            String oldKey = dataSnapshot4.getKey() + "-" + dataSnapshot5.getKey();

                                                            if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                markerOptions = new MarkerOptions();
                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                markerOptions.position(latLng1);

                                                                marker = mMap.addMarker(markerOptions);
                                                                hashMapMarker.put(oldKey, marker);

                                                                keys.add(oldKey);

                                                                Set<String> set = new HashSet<>(keys);
                                                                keys.clear();
                                                                keys.addAll(set);

                                                            } else if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                marker = hashMapMarker.get(oldKey);
                                                                marker.remove();
                                                                hashMapMarker.remove(oldKey);
                                                                keys.remove(oldKey);
                                                            }
                                                        }
                                                    }

                                                    gotoLatLng(latitude, longitude, 9.7f);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {

                databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                if (dataSnapshot1.child("Item_Category").getValue(String.class).equals(category)) {

                                    binding.confirmBtn.setVisibility(View.VISIBLE);

                                    if (circle != null) {
                                        circle.remove();
                                    }

                                    CircleOptions circleOptions = new CircleOptions().center(latLng).radius(5000f).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                    circle = googleMap.addCircle(circleOptions);

                                    LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)));

                                    Location.distanceBetween(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)),
                                            circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                    String oldKey = dataSnapshot.getKey() + "-" + dataSnapshot1.getKey();

                                    if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                        markerOptions = new MarkerOptions();
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        markerOptions.position(latLng1);

                                        marker = mMap.addMarker(markerOptions);
                                        hashMapMarker.put(oldKey, marker);

                                        keys.add(oldKey);

                                        Set<String> set = new HashSet<>(keys);
                                        keys.clear();
                                        keys.addAll(set);

                                    } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                        marker = hashMapMarker.get(oldKey);
                                        marker.remove();
                                        hashMapMarker.remove(oldKey);
                                        keys.remove(oldKey);
                                    }

                                    binding.goToMyLocation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            mMap.clear();
                                            keys.clear();
                                            hashMapMarker.clear();

                                            LatLng myLatLng = new LatLng(myLatitude, myLongitude);

                                            float[] distance = new float[2];

                                            if (circle != null) {
                                                circle.remove();
                                            }

                                            CircleOptions circleOptions = new CircleOptions().center(myLatLng).radius(5000f).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                            circle = googleMap.addCircle(circleOptions);

                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)));

                                            Location.distanceBetween(Double.parseDouble(dataSnapshot1.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("Address").child("Longitude").getValue(String.class)),
                                                    circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                            String oldKey = dataSnapshot.getKey() + "-" + dataSnapshot1.getKey();

                                            if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                markerOptions = new MarkerOptions();
                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                markerOptions.position(latLng1);

                                                marker = mMap.addMarker(markerOptions);
                                                hashMapMarker.put(oldKey, marker);

                                                keys.add(oldKey);

                                                Set<String> set = new HashSet<>(keys);
                                                keys.clear();
                                                keys.addAll(set);

                                            } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot1.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                marker = hashMapMarker.get(oldKey);
                                                marker.remove();
                                                hashMapMarker.remove(oldKey);
                                                keys.remove(oldKey);
                                            }

                                            binding.chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                                @Override
                                                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                                                    if (checkedIds.size() > 0) {
                                                        Chip selectedChip = group.findViewById(checkedIds.get(0));

                                                        hashMapMarker.clear();
                                                        mMap.clear();
                                                        keys.clear();

                                                        databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                float newRadius = 0f;
                                                                float newZoom = 0f;

                                                                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                                                        if (dataSnapshot3.child("Item_Category").getValue(String.class).equals(category)) {

                                                                            if (circle != null) {
                                                                                circle.remove();
                                                                            }

                                                                            switch (selectedChip.getText().toString()) {
                                                                                case "5 km":
                                                                                    newRadius = 5000f;
                                                                                    newZoom = 12.5f;
                                                                                    break;
                                                                                case "10 km":
                                                                                    newRadius = 10000f;
                                                                                    newZoom = 11.5f;
                                                                                    break;
                                                                                case "15 km":
                                                                                    newRadius = 15000f;
                                                                                    newZoom = 10.8f;
                                                                                    break;
                                                                                case "20 km":
                                                                                    newRadius = 20000f;
                                                                                    newZoom = 10.4f;
                                                                                    break;
                                                                                case "25 km":
                                                                                    newRadius = 25000f;
                                                                                    newZoom = 10f;
                                                                                    break;
                                                                                case "30 km":
                                                                                    newRadius = 30000f;
                                                                                    newZoom = 9.8f;
                                                                                    break;
                                                                            }

                                                                            CircleOptions circleOptions = new CircleOptions().center(myLatLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                            circle = googleMap.addCircle(circleOptions);

                                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)));

                                                                            Location.distanceBetween(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)),
                                                                                    circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                                            String oldKey = dataSnapshot2.getKey() + "-" + dataSnapshot3.getKey();

                                                                            if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                markerOptions = new MarkerOptions();
                                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                                markerOptions.position(latLng1);

                                                                                marker = mMap.addMarker(markerOptions);
                                                                                hashMapMarker.put(oldKey, marker);

                                                                                keys.add(oldKey);

                                                                                Set<String> set = new HashSet<>(keys);
                                                                                keys.clear();
                                                                                keys.addAll(set);

                                                                            } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                marker = hashMapMarker.get(oldKey);
                                                                                marker.remove();
                                                                                hashMapMarker.remove(oldKey);
                                                                                keys.remove(oldKey);
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                gotoLatLng(myLatitude, myLongitude, newZoom);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    } else {

                                                        circle.remove();

                                                        hashMapMarker.clear();
                                                        mMap.clear();

                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                        databaseReference1.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                                    for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                        if (dataSnapshot5.child("Item_Category").getValue(String.class).equals(category)) {

                                                                            binding.confirmBtn.setVisibility(View.VISIBLE);

                                                                            LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot5.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot5.child("Address").child("Longitude").getValue(String.class)));

                                                                            String oldKey = dataSnapshot4.getKey() + "-" + dataSnapshot5.getKey();

                                                                            if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                markerOptions = new MarkerOptions();
                                                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                                markerOptions.position(latLng1);

                                                                                marker = mMap.addMarker(markerOptions);
                                                                                hashMapMarker.put(oldKey, marker);

                                                                                keys.add(oldKey);

                                                                                Set<String> set = new HashSet<>(keys);
                                                                                keys.clear();
                                                                                keys.addAll(set);

                                                                            } else if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                                marker = hashMapMarker.get(oldKey);
                                                                                marker.remove();
                                                                                hashMapMarker.remove(oldKey);
                                                                                keys.remove(oldKey);
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                gotoLatLng(myLatitude, myLongitude, 9.7f);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                            gotoLatLng(myLatitude, myLongitude, 12.5f);
                                        }
                                    });

                                }

                                binding.chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                    @Override
                                    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                                        if (checkedIds.size() > 0) {
                                            Chip selectedChip = group.findViewById(checkedIds.get(0));

                                            hashMapMarker.clear();
                                            mMap.clear();
                                            keys.clear();

                                            databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    float newRadius = 0f;
                                                    float newZoom = 0f;

                                                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                                            if (dataSnapshot3.child("Item_Category").getValue(String.class).equals(category)) {

                                                                if (circle != null) {
                                                                    circle.remove();
                                                                }

                                                                switch (selectedChip.getText().toString()) {
                                                                    case "5 km":
                                                                        newRadius = 5000f;
                                                                        newZoom = 12.5f;
                                                                        break;
                                                                    case "10 km":
                                                                        newRadius = 10000f;
                                                                        newZoom = 11.5f;
                                                                        break;
                                                                    case "15 km":
                                                                        newRadius = 15000f;
                                                                        newZoom = 10.8f;
                                                                        break;
                                                                    case "20 km":
                                                                        newRadius = 20000f;
                                                                        newZoom = 10.4f;
                                                                        break;
                                                                    case "25 km":
                                                                        newRadius = 25000f;
                                                                        newZoom = 10f;
                                                                        break;
                                                                    case "30 km":
                                                                        newRadius = 30000f;
                                                                        newZoom = 9.8f;
                                                                        break;
                                                                }

                                                                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                circle = googleMap.addCircle(circleOptions);

                                                                LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)));

                                                                Location.distanceBetween(Double.parseDouble(dataSnapshot3.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot3.child("Address").child("Longitude").getValue(String.class)),
                                                                        circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                                String oldKey = dataSnapshot2.getKey() + "-" + dataSnapshot3.getKey();

                                                                if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                    markerOptions = new MarkerOptions();
                                                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                    markerOptions.position(latLng1);

                                                                    marker = mMap.addMarker(markerOptions);
                                                                    hashMapMarker.put(oldKey, marker);

                                                                    keys.add(oldKey);

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);

                                                                } else if (distance[0] > circle.getRadius() && hashMapMarker.containsKey(oldKey) && !dataSnapshot3.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                    marker = hashMapMarker.get(oldKey);
                                                                    marker.remove();
                                                                    hashMapMarker.remove(oldKey);
                                                                    keys.remove(oldKey);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    gotoLatLng(latitude, longitude, newZoom);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {

                                            circle.remove();

                                            hashMapMarker.clear();
                                            mMap.clear();

                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                            databaseReference1.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                        for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                            if (dataSnapshot5.child("Item_Category").getValue(String.class).equals(category)) {

                                                                binding.confirmBtn.setVisibility(View.VISIBLE);

                                                                LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot5.child("Address").child("Latitude").getValue(String.class)), Double.parseDouble(dataSnapshot5.child("Address").child("Longitude").getValue(String.class)));

                                                                String oldKey = dataSnapshot4.getKey() + "-" + dataSnapshot5.getKey();

                                                                if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                    markerOptions = new MarkerOptions();
                                                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                    markerOptions.position(latLng1);

                                                                    marker = mMap.addMarker(markerOptions);
                                                                    hashMapMarker.put(oldKey, marker);

                                                                    keys.add(oldKey);

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);

                                                                } else if (!dataSnapshot5.child("Poster_UID").getValue(String.class).equals(uid)) {
                                                                    marker = hashMapMarker.get(oldKey);
                                                                    marker.remove();
                                                                    hashMapMarker.remove(oldKey);
                                                                    keys.remove(oldKey);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    gotoLatLng(latitude, longitude, 9.7f);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

        }

        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ItemSwipe.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("keys", keys);
                startActivity(intent);
                CustomIntent.customType(getContext(), "left-to-right");
            }
        });
    }

    private void gotoLatLng(double latitude, double longitude, float v) {

        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, v);
        mMap.animateCamera(update);
    }


    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 940;
            zoomLevel = (int) (18 - Math.log(scale) / Math.log(2));
        }

        return zoomLevel;
    }
}