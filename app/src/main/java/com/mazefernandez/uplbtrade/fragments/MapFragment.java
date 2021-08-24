package com.mazefernandez.uplbtrade.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.mazefernandez.uplbtrade.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* shows the map of UPLB and allows a user to choose a meet up venue */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener{
    private GoogleMap map;
    private Marker marker;
    private String venue;

    public MapFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // setup address searching
        SearchView searchView = view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchVenue(map, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    /* Search map for user's query */
    private void searchVenue(GoogleMap map, String string) {
        venue = string;
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(venue, 4);
        }catch(IOException e){
            e.printStackTrace();
        }

        if(list.size() != 0) {
            Address address = list.get(0);
            System.out.println("Location: " + address.toString());
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            float zoom = 18;
            venue = address.getAddressLine(0);
            moveCamera(latLng,zoom,venue,map);
        }
    }

    /* shifts camera and pin */
    private void moveCamera(LatLng latLng, float zoom, String title, GoogleMap map){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        marker.remove();
        marker = map.addMarker(new MarkerOptions()
            .position(latLng)
            .title(title));
        assert marker != null;
        marker.showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // UPLB coordinates
        LatLng uplb = new LatLng(14.1648, 121.2413);
        CameraPosition cp = new CameraPosition(uplb, 16, 0,0);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        googleMap.setOnPoiClickListener(this);
        googleMap.setBuildingsEnabled(true);

        // Marker for UPLB coordinates
        marker = googleMap.addMarker(new MarkerOptions()
                .position(uplb)
                .title("UP Los Baños"));

        map = googleMap;
    }

    public String getVenue() {
        return venue;
    }

    // shifts view to POI when clicked by user
    @Override
    public void onPoiClick(PointOfInterest poi) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocation(poi.latLng.latitude, poi.latLng.longitude,1);
        }catch(IOException e){
            e.printStackTrace();
        }

        if(list.size() != 0) {
            Address address = list.get(0);
            System.out.println("Location: " + address.toString());
            float zoom = 18;
            venue = poi.name;
            moveCamera(poi.latLng,zoom,venue,map);
        }
    }
}
