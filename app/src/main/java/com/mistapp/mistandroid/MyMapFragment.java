package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private GoogleMap mGoogleMap;
    private SupportMapFragment googleMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private View view = null;
    private LocationRequest mLocationRequest;

    private LatLng latLng;
    private Marker currLocationMarker;
    private Marker heartyMarker;
    private Marker chapelMarker;
    private Marker northHallMarker;
    private Marker caldwellMarker;
    private Location mBestReading;

    /**
     * Inflate map, calls google map fragment into existence
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        if (googleServicesAvailable()) {
            Toast.makeText(getActivity(), "Zooming in", Toast.LENGTH_LONG).show();
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).build();
            if (mGoogleApiClient != null) {
                Log.i("mGoogleApiClient", " google api client it's not null!");
                mGoogleApiClient.connect();
                initMap();
            }
        } else {
            //No Google Maps Layout
        }
        return view;
    }

    /**
     * Creates map fragment and all of its features
     */
    private void initMap() {
        try {
            googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            googleMapFragment.getMapAsync(this);
        } catch(Exception e) {
            Toast.makeText(getActivity(), "The map can not load on your device", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Checks if can connect to google services
     * @return
     */
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Sets up google map for fragment
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Log.e("TAG", "GPS is on");
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            //debugging: Toast.makeText(getContext(), "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        }
        addMarkers();

    }

    private void addMarkers() {
        //double longitude = location.getLongitude();
        //double latitude = location.getLatitude();

        double CaldwellLat = 33.954921;
        double CaldwellLng = -83.375290;
        double HeartyLat = 33.955946;
        double HeartyLng = -83.375683;
        double ChapelLat = 33.956499;
        double ChapelLng = -83.375148;
        double NorthHallLat = 33.956169;
        double NorthHallLng = -83.372517;

        MarkerOptions Caldwell= new MarkerOptions().title("Caldwell Hall").position(new LatLng(CaldwellLat,CaldwellLng));
        MarkerOptions Hearty= new MarkerOptions().title("Hearty Field").position(new LatLng(HeartyLat,HeartyLng));
        MarkerOptions Chapel = new MarkerOptions().title("Chapel").position(new LatLng(ChapelLat,ChapelLng));
        MarkerOptions NorthHall = new MarkerOptions().title("North Hall Deck").position(new LatLng(NorthHallLat,NorthHallLng));

        heartyMarker = mGoogleMap.addMarker(Hearty);
        chapelMarker = mGoogleMap.addMarker(Chapel);
        northHallMarker = mGoogleMap.addMarker(NorthHall);
        caldwellMarker = mGoogleMap.addMarker(Caldwell);
    }


    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(50000);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);

        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Checks if current location changes and updates marker
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(getActivity(), "Cant get current location", Toast.LENGTH_LONG).show();
        } else {

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 16);
            mGoogleMap.animateCamera(update);
        }  //place marker at current position
        mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        currLocationMarker = mGoogleMap.addMarker(markerOptions);
        currLocationMarker.showInfoWindow();
        double CaldwellLat = 33.954921;
        double CaldwellLng = -83.375290;
        double HeartyLat = 33.955946;
        double HeartyLng = -83.375683;
        double ChapelLat = 33.956499;
        double ChapelLng = -83.375148;
        double NorthHallLat = 33.956169;
        double NorthHallLng = -83.372517;

        MarkerOptions Caldwell= new MarkerOptions().title("Caldwell Hall").position(new LatLng(CaldwellLat,CaldwellLng));
        MarkerOptions Hearty= new MarkerOptions().title("Hearty Field").position(new LatLng(HeartyLat,HeartyLng));
        MarkerOptions Chapel = new MarkerOptions().title("Chapel").position(new LatLng(ChapelLat,ChapelLng));
        MarkerOptions NorthHall = new MarkerOptions().title("North Hall Deck").position(new LatLng(NorthHallLat,NorthHallLng));

        heartyMarker = mGoogleMap.addMarker(Hearty);
        chapelMarker = mGoogleMap.addMarker(Chapel);
        caldwellMarker = mGoogleMap.addMarker(Caldwell);
        northHallMarker = mGoogleMap.addMarker(NorthHall);
        // Toast.makeText(getActivity(),"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(17).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    }



