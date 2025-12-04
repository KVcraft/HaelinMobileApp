package com.haelinmobileapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haelinmobileapp.retrofit.ApiService;
import com.haelinmobileapp.retrofit.Hospital;
import com.haelinmobileapp.retrofit.RetrofitClient;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocMap extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private ApiService apiService;

    private static final int LOCATION_REQUEST_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        mapView = view.findViewById(R.id.map);

        requestLocationPermissions();
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()),
                mapView
        );
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        mapView.getOverlays().add(locationOverlay);

        locationOverlay.runOnFirstFix(() -> {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(() -> {
                GeoPoint userPoint = locationOverlay.getMyLocation();
                if (userPoint != null) {
                    mapView.getController().setZoom(16.0);
                    mapView.getController().animateTo(userPoint);

                    fetchNearbyHospitals(
                            userPoint.getLatitude(),
                            userPoint.getLongitude()
                    );
                } else {
                    Log.e("MAP", "User location is null. Using fallback.");
                    GeoPoint defaultPoint = new GeoPoint(6.9271, 79.8612);
                    mapView.getController().setZoom(12.0);
                    mapView.getController().animateTo(defaultPoint);
                }
            });
        });
    }

    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE
                );
                return;
            }
        }

        setupMap();
    }

    private void fetchNearbyHospitals(double lat, double lon) {
        apiService.getNearbyHospitals(lat, lon, 5000)
                .enqueue(new Callback<List<Hospital>>() {
                    @Override
                    public void onResponse(Call<List<Hospital>> call, Response<List<Hospital>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayHospitalsOnMap(response.body());
                        } else {
                            Log.e("MAP", "Hospital fetch failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Hospital>> call, Throwable t) {
                        Log.e("MAP", "API error: " + t.getMessage());
                    }
                });
    }

    private void displayHospitalsOnMap(List<Hospital> hospitals) {
        for (Hospital h : hospitals) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(h.lat, h.lon));
            marker.setTitle(h.name);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setupMap();
            } else {
                Log.e("MAP", "Location permission denied.");
            }
        }
    }
}
