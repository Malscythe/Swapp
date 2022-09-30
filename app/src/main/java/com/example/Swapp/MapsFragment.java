package com.example.Swapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Swapp.chat.Chat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        binding.confirmBtn.setVisibility(View.GONE);

        Bundle bundle = getArguments();

        String category = bundle.getString("category");
        String from = bundle.getString("from");

        mMap = googleMap;

        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}


        if(!gps_enabled) {
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

                                    String hideSearch = bundle.getString("from");

                                    Log.w("TAG", "" + location);

                                    if (hideSearch.equals("categories")) {

                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                        binding.getRadius.setVisibility(View.VISIBLE);
                                        binding.currentAddressLayout.setVisibility(View.GONE);

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                        if (category.equals("all")) {

                                            databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                        binding.sliderRadius.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
                                                            @Override
                                                            public void onStartTrackingTouch(@NonNull Slider slider) {

                                                                binding.confirmBtn.setVisibility(View.VISIBLE);

                                                            }

                                                            @Override
                                                            public void onStopTrackingTouch(@NonNull Slider slider) {


                                                                float[] distance = new float[2];

                                                                if (circle != null) {
                                                                    circle.remove();
                                                                }

                                                                float newRadius = slider.getValue();

                                                                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                circle = googleMap.addCircle(circleOptions);

                                                                LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot.child("lat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("long").getValue(String.class)));

                                                                Location.distanceBetween(Double.parseDouble(dataSnapshot.child("lat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("long").getValue(String.class)),
                                                                        circle.getCenter().latitude, circle.getCenter().longitude, distance);

                                                                if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(dataSnapshot.getKey())) {
                                                                    markerOptions = new MarkerOptions();
                                                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                    markerOptions.position(latLng1);

                                                                    marker = mMap.addMarker(markerOptions);
                                                                    hashMapMarker.put(dataSnapshot.getKey(), marker);

                                                                    keys.add(dataSnapshot.getKey());

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);

                                                                } else if (hashMapMarker.containsKey(dataSnapshot.getKey()) && distance[0] > circle.getRadius()) {
                                                                    marker = hashMapMarker.get(dataSnapshot.getKey());
                                                                    marker.remove();
                                                                    hashMapMarker.remove(dataSnapshot.getKey());
                                                                    keys.remove(dataSnapshot.getKey());
                                                                }


                                                                if (newRadius == 0f) {
                                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                                                                    mMap.clear();
                                                                    circle.remove();

                                                                    keys.add(dataSnapshot.getKey());

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);
                                                                } else {

                                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circle.getCenter(), getZoomLevel(circle)));
                                                                }
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {

                                            databaseReference.child("items").orderByChild("Item_Category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                        binding.sliderRadius.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
                                                            @Override
                                                            public void onStartTrackingTouch(@NonNull Slider slider) {

                                                                binding.confirmBtn.setVisibility(View.VISIBLE);

                                                            }

                                                            @Override
                                                            public void onStopTrackingTouch(@NonNull Slider slider) {

                                                                float[] distance = new float[2];

                                                                if (circle != null) {
                                                                    circle.remove();
                                                                }

                                                                float newRadius = slider.getValue();

                                                                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(newRadius).fillColor(0x4406E8F1).strokeColor(0xFF06E8F1).strokeWidth(8);
                                                                circle = googleMap.addCircle(circleOptions);

                                                                LatLng latLng1 = new LatLng(Double.parseDouble(dataSnapshot.child("lat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("long").getValue(String.class)));

                                                                Location.distanceBetween(Double.parseDouble(dataSnapshot.child("lat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("long").getValue(String.class)),
                                                                        circle.getCenter().latitude, circle.getCenter().longitude, distance);


                                                                if (distance[0] < circle.getRadius() && !hashMapMarker.containsKey(dataSnapshot.getKey())) {
                                                                    markerOptions = new MarkerOptions();
                                                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                                    markerOptions.position(latLng1);

                                                                    marker = mMap.addMarker(markerOptions);
                                                                    hashMapMarker.put(dataSnapshot.getKey(), marker);

                                                                    keys.add(dataSnapshot.getKey());

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);

                                                                } else if (hashMapMarker.containsKey(dataSnapshot.getKey()) && distance[0] > circle.getRadius()) {
                                                                    marker = hashMapMarker.get(dataSnapshot.getKey());
                                                                    marker.remove();
                                                                    hashMapMarker.remove(dataSnapshot.getKey());
                                                                    keys.remove(dataSnapshot.getKey());
                                                                }

                                                                if (newRadius == 0f) {
                                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                                                                    mMap.clear();
                                                                    circle.remove();

                                                                    keys.add(dataSnapshot.getKey());

                                                                    Set<String> set = new HashSet<>(keys);
                                                                    keys.clear();
                                                                    keys.addAll(set);
                                                                } else {

                                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circle.getCenter(), getZoomLevel(circle)));
                                                                }
                                                            }
                                                        });

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
                            String pinAddress = addresses.get(0).getAddressLine(0);

                            Intent intent = new Intent(getContext(), PostItem.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("address", pinAddress);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            startActivity(intent);
                            CustomIntent.customType(getContext(), "left-to-right");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
    }


    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 940;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
}