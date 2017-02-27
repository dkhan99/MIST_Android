package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.drive.Drive;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.Plus;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;

public class MyMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private GoogleMap mGoogleMap;
    private SupportMapFragment googleMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private View view = null;
    private LocationRequest mLocationRequest;

    private LatLng latLng;
    private Marker NorthCampusGreenMarker;
    private Marker currLocationMarker;
    private Marker hertyMarker;
    private Marker sanfordMarker;
    private Marker chapelMarker;
    private Marker northDeckMarker;
    private Marker caldwellMarker;
    private Marker eastDeckMarker;
    private Marker classicCenterMarker;
    private Marker ramseyMarker;


    private Location mBestReading;
    private ArrayList<LatLng> arrayPoints = null;

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
        getActivity().setTitle(getResources().getString(R.string.map_page_title));
        Log.i("TAG", " onmapcreate!");

        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Oncreate did not have permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},  MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        else{
            Log.d("TAG", "Oncreate did have permission, initializing map");
            initMap();
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
            addMarkers();
            addLines();
            goToLocationZoom(33.955655, -83.374050, 17);


        } catch (Exception e) {
            Log.e("TAG", "Map cannot be loaded" + e);

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
        Log.i("TAG", " onmapready");

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //debugging: Toast.makeText(getContext(), "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            }
            addMarkers();
            addLines();
            goToLocationZoom(33.955655, -83.374050, 17);

        } else {
            addMarkers();
            addLines();
            goToLocationZoom(33.955655, -83.374050, 17);

        }

    }
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }


    private void addLines(){
//        mGoogleMap.addPolyline();
        // settin polyline in the map
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        arrayPoints = new ArrayList<LatLng>();
        arrayPoints.add(new LatLng(33.953483,-83.375382));
        arrayPoints.add(new LatLng(33.956597,-83.376227));
        arrayPoints.add(new LatLng(33.957399,-83.372573));

        arrayPoints.add(new LatLng(33.955504,-83.371899));
        arrayPoints.add(new LatLng(33.955439,-83.372497));
        arrayPoints.add(new LatLng(33.955913,-83.373452));
        arrayPoints.add(new LatLng(33.955488,-83.375131));
        arrayPoints.add(new LatLng(33.953626,-83.374462));
        arrayPoints.add(new LatLng(33.953468,-83.375287));
        arrayPoints.add(new LatLng(33.953483,-83.375382));

        polylineOptions.addAll(arrayPoints);
        mGoogleMap.addPolyline(polylineOptions);

    }

    private void addMarkers() {
        //double longitude = location.getLongitude();
        //double latitude = location.getLatitude();

        double CaldwellLat = 33.954921;
        double CaldwellLng = -83.375290;
        double HertyLat = 33.955946;
        double HertyLng = -83.375683;
        double ChapelLat = 33.956663;
        double ChapelLng = -83.375181;
        double sanfordLat = 33.953768;
        double sanfordLng = -83.374784;
        double NorthDeckLat = 33.956169;
        double NorthDeckLng = -83.372517;
        double NorthCampusGreenLat = 33.956681;
        double NorthCampusGreenLng = -83.374673;
        double eastParkingDeckLat = 33.938125;
        double eastParkingDeckLng = -83.369314;
        double classicCenterLat = 33.960552;
        double classicCenterLng = -83.372337;
        double ramseyCenterLat = 33.937612;
        double ramseyCenterLng = -83.370851;

        MarkerOptions Caldwell= new MarkerOptions().title("Caldwell Hall").position(new LatLng(CaldwellLat,CaldwellLng));
        MarkerOptions Sanford= new MarkerOptions().title("Sanford Hall").position(new LatLng(sanfordLat,sanfordLng));
        MarkerOptions Herty= new MarkerOptions().title("Herty Field").position(new LatLng(HertyLat,HertyLng));
        MarkerOptions Chapel = new MarkerOptions().title("Chapel").position(new LatLng(ChapelLat,ChapelLng));
        MarkerOptions NorthCampusGreen = new MarkerOptions().title("N. Campus Green").position(new LatLng(NorthCampusGreenLat,NorthCampusGreenLng));
        MarkerOptions NorthHall = new MarkerOptions().title("North Deck").position(new LatLng(NorthDeckLat,NorthDeckLng));
        MarkerOptions EastDeck = new MarkerOptions().title("East Deck").position(new LatLng(eastParkingDeckLat,eastParkingDeckLng));
        MarkerOptions ClassicCenter = new MarkerOptions().title("Classic Center").position(new LatLng(classicCenterLat,classicCenterLng));
        MarkerOptions Ramsey = new MarkerOptions().title("Ramsey Student Center").position(new LatLng(ramseyCenterLat,ramseyCenterLng));

        hertyMarker = mGoogleMap.addMarker(Herty);
        chapelMarker = mGoogleMap.addMarker(Chapel);
        caldwellMarker = mGoogleMap.addMarker(Caldwell);
        sanfordMarker = mGoogleMap.addMarker(Sanford);
        NorthCampusGreenMarker = mGoogleMap.addMarker(NorthCampusGreen);
        northDeckMarker = mGoogleMap.addMarker(NorthHall);
        eastDeckMarker = mGoogleMap.addMarker(EastDeck);
        classicCenterMarker = mGoogleMap.addMarker(ClassicCenter);
        ramseyMarker = mGoogleMap.addMarker(Ramsey);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i("TAG", " onconnected");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED  ) {

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
            //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 16);
            //mGoogleMap.animateCamera(update);
        }  //place marker at current position
        mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(latLng);
        //markerOptions.title("Current Position");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        //currLocationMarker = mGoogleMap.addMarker(markerOptions);
        //currLocationMarker.showInfoWindow();
        double CaldwellLat = 33.954921;
        double CaldwellLng = -83.375290;
        double HertyLat = 33.955946;
        double HertyLng = -83.375683;
        double ChapelLat = 33.956663;
        double ChapelLng = -83.375181;
        double sanfordLat = 33.953768;
        double sanfordLng = -83.374784;
        double NorthDeckLat = 33.956169;
        double NorthDeckLng = -83.372517;
        double NorthCampusGreenLat = 33.956681;
        double NorthCampusGreenLng = -83.374673;
        double eastParkingDeckLat = 33.938125;
        double eastParkingDeckLng = -83.369314;
        double classicCenterLat = 33.960552;
        double classicCenterLng = -83.372337;
        double ramseyCenterLat = 33.937612;
        double ramseyCenterLng = -83.370851;

        MarkerOptions Caldwell= new MarkerOptions().title("Caldwell Hall").position(new LatLng(CaldwellLat,CaldwellLng));
        MarkerOptions Sanford= new MarkerOptions().title("Sanford Hall").position(new LatLng(sanfordLat,sanfordLng));
        MarkerOptions Herty= new MarkerOptions().title("Herty Field").position(new LatLng(HertyLat,HertyLng));
        MarkerOptions Chapel = new MarkerOptions().title("Chapel").position(new LatLng(ChapelLat,ChapelLng));
        MarkerOptions NorthCampusGreen = new MarkerOptions().title("N. Campus Green").position(new LatLng(NorthCampusGreenLat,NorthCampusGreenLng));
        MarkerOptions NorthDeck = new MarkerOptions().title("North Deck").position(new LatLng(NorthDeckLat,NorthDeckLng));
        MarkerOptions EastDeck = new MarkerOptions().title("East Deck").position(new LatLng(eastParkingDeckLat,eastParkingDeckLng));
        MarkerOptions ClassicCenter = new MarkerOptions().title("Classic Center").position(new LatLng(classicCenterLat,classicCenterLng));
        MarkerOptions Ramsey = new MarkerOptions().title("Ramsey Student Center").position(new LatLng(ramseyCenterLat,ramseyCenterLng));

        hertyMarker = mGoogleMap.addMarker(Herty);
        chapelMarker = mGoogleMap.addMarker(Chapel);
        caldwellMarker = mGoogleMap.addMarker(Caldwell);
        sanfordMarker = mGoogleMap.addMarker(Sanford);
        NorthCampusGreenMarker = mGoogleMap.addMarker(NorthCampusGreen);
        northDeckMarker = mGoogleMap.addMarker(NorthDeck);
        eastDeckMarker = mGoogleMap.addMarker(EastDeck);
        classicCenterMarker = mGoogleMap.addMarker(ClassicCenter);
        ramseyMarker = mGoogleMap.addMarker(Ramsey);
        // Toast.makeText(getActivity(),"Location Changed",Toast.LENGTH_SHORT).show();
        addLines();
        //zoom to current position:
        //CameraPosition cameraPosition = new CameraPosition.Builder()
         //       .target(latLng).zoom(17).build();

        //mGoogleMap.animateCamera(CameraUpdateFactory
         //       .newCameraPosition(cameraPosition));

    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomBar bbar = ((MyMistActivity)getActivity()).getBottomBar();
        bbar.selectTabAtPosition(0);
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
            //mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG", " permission granted");

                    mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).build();
                    if (mGoogleApiClient != null) {
                        Log.i("mGoogleApiClient", " accepted and google api client it's not null!");
                        mGoogleApiClient.connect();
                        initMap();
                    }


                    //No Google Maps Layout

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                            .addApi(Plus.API)
                            .build();
                    Log.i("TAG", " permission not granted");
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                        Log.i("mGoogleApiClient", " step 2");
                        Log.i("mGoogleApiClient", " declined to accept and api connected!");
                        initMap();
                    }
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}


