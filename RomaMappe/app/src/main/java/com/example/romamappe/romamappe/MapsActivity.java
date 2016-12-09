package com.example.romamappe.romamappe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;

import static com.example.romamappe.romamappe.R.id.spinner1;
import static com.example.romamappe.romamappe.TipoScelto.qualeImmagine;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback,
                    GoogleApiClient.ConnectionCallbacks,
                    GoogleApiClient.OnConnectionFailedListener,
                    LocationListener,
                    View.OnClickListener,
                    AdapterView.OnItemSelectedListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private LocationRequest mLocationRequest;

    PendingResult<LocationSettingsResult> result;

    private boolean gpsAttivo;

    private double LongitudineCorrente;
    private double LatitudineCorrente;

    private ImageButton btnClick;
    private Spinner spinner1;

    private TipoScelto.OggettoPubblico tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnClick = (ImageButton) findViewById(R.id.button1) ;
        //btnClick.setOnClickListener(this);
        //((ImageButton) view).setImageResource(R.drawable.icon2);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        new AlertDialog.Builder(this)
                //.setTitle("Delete entry")
                .setMessage("Benvenuto in RomaMappe: gira per la città e aggiungi luoghi!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                //  .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                //      public void onClick(DialogInterface dialog, int which) {
                //          // do nothing
                //      }
                //  })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    protected void onPause() {
        super.onPause();
        //setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                nuovaPosizione();
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } catch (SecurityException e) {
        }
        startLocationUpdates();
    }

    private LatLng nuovaPosizione() {
        if(mLastLocation!= null) {
            LatitudineCorrente = /*String.valueOf(*/mLastLocation.getLatitude();
            LongitudineCorrente =/* String.valueOf(*/mLastLocation.getLongitude();
            LatLng ll = new LatLng(LatitudineCorrente, LongitudineCorrente);
            //MarkerOptions options = new MarkerOptions().position(ll).title("Aggiungi un elemento quì");
            //mMap.addMarker(options);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            return ll;
        }
        else {
            return null;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        createLocationRequest();
        boolean attivo = checkGPSattivo();
        if (!attivo) {

        }

        try{
                mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException e)
        {
            new AlertDialog.Builder(this)
                    .setMessage("Errore:" + e.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private boolean checkGPSattivo() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locSetResult) {
                final Status status = locSetResult.getStatus();
                final LocationSettingsStates lls = locSetResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        gpsAttivo=true;
                        createLocationRequest();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        gpsAttivo = false;

               /* try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            OuterClass.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (SendIntentException e) {
                    // Ignore the error.
                }*/
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.
                        gpsAttivo=false;
                        break;
        }
            }
        });
        return gpsAttivo;
    }


    @Override
    public void onLocationChanged(Location loc)
    {
        if (mLastLocation != null) {
            nuovaPosizione();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        //if (v == btnClick) {
        //  new AlertDialog.Builder(this)
        //          .setMessage("Pulsante cliccato")
        //          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        //              public void onClick(DialogInterface dialog, int which) {
        //              }
        //          })
        //          .show();
        //}
        try{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng ll =
                    nuovaPosizione();
            if(ll!= null) {
                //FileInputStream fis = new FileInputStream (new File("@drawable/home"));
                MarkerOptions options = new MarkerOptions().position(ll).title("Aggiungi un elemento quì")
                        //.icon(BitmapDescriptorFactory.fromFile());
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                .icon(BitmapDescriptorFactory.fromResource(qualeImmagine(tipo)));
                mMap.addMarker(options);
            }
            else
            {
              //MarkerOptions options = new MarkerOptions().position(new LatLng(0,0)).title("Aggiungi un elemento quì")
              //        .icon(BitmapDescriptorFactory.fromResource(qualeImmagine(tipo)));
              //mMap.addMarker(options);
            }
        } catch (SecurityException e) {

        }
        catch (Exception ex){
            new AlertDialog.Builder(this)
                    .setMessage("Errore:" + ex.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            String selezionato = parent.getItemAtPosition(position).toString();
            tipo = TipoScelto.OggettoPubblico.valueOf(selezionato);
            btnClick.setImageResource(qualeImmagine(tipo));
        }
        catch (Exception e){
            new AlertDialog.Builder(this)
                    .setMessage("Errore:" + e.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }





}
