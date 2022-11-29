package com.example.mynotes.newnote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mynotes.R;
import com.example.mynotes.databinding.FragmentMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
import java.util.Map;

public class MapsFragment extends Fragment {

    boolean locationPermissionGranted = false;

    NewNoteViewModel newNoteViewModel;

    FragmentMapsBinding binding;

    LatLng latLng;

    GoogleMap map;

    FusedLocationProviderClient fusedLocationProviderClient;

    LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    static final int DEFAULT_ZOOM = 15;

    Location lastKnownLocation;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            getDeviceLocation();

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    map.clear();
                    String lat = String.format(Locale.getDefault(), "%.3f", latLng.latitude);
                    String lng = String.format(Locale.getDefault(), "%.3f", latLng.longitude);
                    MarkerOptions marker = new MarkerOptions().position(latLng).title(lat + "," + lng);
                    MapsFragment.this.latLng = latLng;
                    map.addMarker(marker);
                }
            });
        }
    };

    static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newNoteViewModel = new ViewModelProvider(requireActivity()).get(NewNoteViewModel.class);
        checkGooglePlayServices();
        requestPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.saveMap.setOnClickListener(v -> {
            newNoteViewModel.setLatLng(latLng);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void requestPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        boolean a = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                        if (a) {
                            locationPermissionGranted = true;
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Unable to show location - permission required",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
        String[] arr = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        locationPermissionRequest.launch(arr);


    }

    private void checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode =
                googleApiAvailability.isGooglePlayServicesAvailable(requireContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 257).show();
            } else {
                Log.i(MapsFragment.class.getSimpleName(),
                        "This device must install Google Play Services."
                );
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(
                                                        lastKnownLocation.getLatitude(),
                                                        lastKnownLocation.getLongitude()
                                                ), DEFAULT_ZOOM
                                        )
                                );
                            }
                        } else {

                            map.moveCamera(
                                    CameraUpdateFactory
                                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM)
                            );
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}

