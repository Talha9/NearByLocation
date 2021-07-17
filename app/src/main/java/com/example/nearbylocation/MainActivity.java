package com.example.nearbylocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.nearbylocation.nearByLocationModel.Model;
import com.example.nearbylocation.nearByLocationModel.Venue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.Task;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;


public class MainActivity extends AppCompatActivity implements MapboxMap.OnMapClickListener{

    MapView mapView;
    public static final int PERMISSION_REQUEST_CODE = 9001;
    public static final int REQUEST_CODE = 9002;
    private FusedLocationProviderClient mLocationClient;
    Location userLoc;
    EditText SrchText;
    MapboxMap ownMapboxMap;
    private static final String TAG = "DirectionsActivity";
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    LocationRequest mLocationRequest;
    Marker marker;
    Marker IdleMarker;
    Location loc=null;
    CameraPosition position;
    Button button;
    ProgressDialog dialog;
    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        // NavBtn.setVisibility(View.INVISIBLE);
        SrchText = findViewById(R.id.SearchTxt);
        userLoc = new Location("service Provider");
        mLocationClient = new FusedLocationProviderClient(this);
        LocationPermission();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        if (isServiceOk()) {
            if (isGPSEnabled()) {

                MapBoxInitialization();

            } else {

                requestLocationPermission();

            }


        }


        SrchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String LocationName = SrchText.getText().toString().trim().toLowerCase(Locale.getDefault());
                if (marker != null) {
                    ownMapboxMap.removeMarker(marker);
                }
                geoLocate(LocationName);
            }

            return true;
        });


    }


    private void MapBoxInitialization() {

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull MapboxMap mapboxMap) {
                    ownMapboxMap = mapboxMap;
                    mapboxMap.getUiSettings().setCompassEnabled(false);

                    ownMapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            boolean providerEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
                            if(providerEnabled) {
                                enableLocationComponent(style);
                            }
                            addDestinationIconSymbolLayer(style);

                            mapboxMap.addOnMapClickListener(MainActivity.this);
                            button = findViewById(R.id.NavigateBtn);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean simulateRoute = false;
                                    NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                            .directionsRoute(currentRoute)
                                            .shouldSimulateRoute(simulateRoute)
                                            .build();
                                    // Call this method with Context from within an Activity
                                    NavigationLauncher.startNavigation(MainActivity.this, options);
                                }
                            });


//                        IconFactory factory = IconFactory.getInstance(MainActivity.this);
//                        Icon icon = factory.fromResource(R.drawable.mapbox_marker_icon_default);
//                        ownMapboxMap.addMarker(new MarkerOptions().position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude())).icon(icon));

                        }
                    });

                }
            });
       

    }


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    private void geoLocate(String LocationName) {
        //NavBtn.setVisibility(View.VISIBLE);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List< Address > addressList = geocoder.getFromLocationName(LocationName, 1);
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                gotoLocation(address.getLongitude(), address.getLatitude());
                //mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                Toast.makeText(this, address.getLocality(), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void gotoLocation(double longitude, double latitude) {

        IconFactory factory = IconFactory.getInstance(MainActivity.this);
        Icon icon = factory.fromResource(R.drawable.ic_action_name2);
        if ((IdleMarker != null)) {
            ownMapboxMap.removeMarker(IdleMarker);
        }
        IdleMarker = ownMapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(icon));

        position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        ownMapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 7000);
        String latlong = String.valueOf(latitude) + "," + String.valueOf(longitude);
        if (marker != null) {
            ownMapboxMap.removeMarker(marker);
        }
        foursquareApiHit(latlong);

        Point OrigenPoint = Point.fromLngLat(userLoc.getLongitude(), userLoc.getLatitude());
        Point destinationPoint = Point.fromLngLat(longitude, latitude);
        GeoJsonSource source = ownMapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }
        getRoute(OrigenPoint, destinationPoint);


    }


    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.getLastLocation().addOnCompleteListener((Task< Location > task) -> {

                if (task.isSuccessful()) {
                    LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                            .elevation(5)
                            .accuracyAlpha(.6f)
                            .accuracyColor(Color.BLUE)
                            .foregroundDrawable(R.drawable.ic_baseline_my_location_24)
                            .build();

                    // Get an instance of the component
                    locationComponent = ownMapboxMap.getLocationComponent();

                    LocationComponentActivationOptions locationComponentActivationOptions =
                            LocationComponentActivationOptions.builder(this, loadedMapStyle)
                                    .locationComponentOptions(customLocationComponentOptions)
                                    .build();

                    // Activate with options
                    locationComponent.activateLocationComponent(locationComponentActivationOptions);
                    // Enable to make component visible
                    locationComponent.setLocationComponentEnabled(true);
                    loc=task.getResult();

                         position = new CameraPosition.Builder()
                                 .target(new LatLng(loc.getLatitude(), loc.getLongitude())) // Sets the new camera position
                                 .zoom(17) // Sets the zoom
                                 .bearing(180) // Rotate the camera
                                 .tilt(30) // Set the camera tilt
                                 .build(); // Creates a CameraPosition from the builder

                         ownMapboxMap.animateCamera(CameraUpdateFactory
                                 .newCameraPosition(position), 7000);




                    }

                    // Add the location icon click listener
                    locationComponent.addOnLocationClickListener(new OnLocationClickListener() {
                        @Override
                        public void onLocationComponentClick() {
                            if (locationComponent.getLastKnownLocation() != null) {
                                Toast.makeText(MainActivity.this, String.format("Current Location",
                                        locationComponent.getLastKnownLocation().getLatitude(),
                                        locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            });


        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }


    private void foursquareApiHit(String latlang) {


        NearbyApi api = RetrofitInstance.getRetrofitInstance().create(NearbyApi.class);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd  ", Locale.getDefault());
        String dateFormat = sdf.format(c);
        Call< Model > ModelCall = api.getFourSqurePlace(latlang, "03KLBFBSAZVS4GHXLXZWVY23TU0ZEAGLZ3EBRZNA1CBE4TT1", "ZGFPAR5MONUQGURUPI0WYJUTCKMZ50U2TLBTAFBXEIGYJ5LE", dateFormat, String.valueOf(50), String.valueOf(10000));
        ModelCall.enqueue(new Callback< Model >() {
            @Override
            public void onResponse(@NotNull Call< Model > call, @NotNull Response< Model > response) {
//                if (!(response.isSuccessful())) {
//                    Toast.makeText(MainActivity.this, response.code() + " ", Toast.LENGTH_LONG).show();
//                    return;
//                }
                List< Venue > list = new ArrayList<>();
                list = response.body().getResponse().getVenues();
                AddMarkerThroughList(list);

            }

            @Override
            public void onFailure(Call< Model > call, Throwable t) {
                Log.d("error", ":::" + t);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private void AddMarkerThroughList(List< Venue > list) {
        if (marker != null) {
            ownMapboxMap.removeMarker(marker);
        }
        for (int i = 0; i < list.size(); i++) {
            double CurrentLat = list.get(i).getLocation().getLat();
            double CurrentLong = list.get(i).getLocation().getLng();
            String CurrentName = String.valueOf(list.get(i).getName());
            // String CurrentIcon=String.valueOf(list.get(i).getCategories());
            //Toast.makeText(this, CurrentIcon, Toast.LENGTH_SHORT).show();
            IconFactory factory = IconFactory.getInstance(MainActivity.this);
            Icon icon = factory.fromResource(R.drawable.ic_action_name);
            marker = ownMapboxMap.addMarker(new MarkerOptions().position(new LatLng(CurrentLat, CurrentLong)).
                    icon(icon).
                    title(CurrentName));


            ownMapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                @Nullable
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.marker_view_layout, null);
                    v.setLayoutParams(new ViewGroup.LayoutParams(100, 150));
                    ImageView MarkerImage = v.findViewById(R.id.MarkerViewImageView);
                    ImageButton MarkerShareBtn = v.findViewById(R.id.appCompatImageButton);
                    TextView MarkerText = v.findViewById(R.id.MarkerViewTextView);
                    MarkerText.setText(marker.getTitle());
                    Glide.with(MainActivity.this).load(R.drawable.ic_baseline_panorama_24).
                            into(MarkerImage);
                    MarkerShareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                                String shareMessage = "\nLet me recommend you this application\n\n";
                                shareMessage = shareMessage + "http://maps.google.com/maps?saddr=" + CurrentLat + "," + CurrentLong;
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                            } catch (Exception e) {
                                //e.toString();
                            }
                        }
                    });


                    return v;
                }
            });

        }


    }

    private void LocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(5000);
           // mLocationRequest.setSmallestDisplacement(10);
            getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            userLoc.setLongitude(locationResult.getLastLocation().getLongitude());
                            userLoc.setLatitude(locationResult.getLastLocation().getLatitude());
                            //userLoc.setAltitude(location.getAltitude());
                            Toast.makeText(getApplicationContext(),locationResult.getLastLocation().getLatitude()+" "+locationResult.getLastLocation().getLongitude(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    Looper.myLooper());

//            mLocationClient.getLastLocation().addOnCompleteListener((Task< Location > task) -> {
//                if (task.isSuccessful()) {
//
//                    location = task.getResult();
//                    userLoc.setLongitude(location.getLongitude());
//                    userLoc.setLatitude(location.getLatitude());
//                    userLoc.setAltitude(location.getAltitude());
//                     }
//            });
           // Toast.makeText(this, userLoc.getLongitude()+"-"+userLoc.getLatitude(), Toast.LENGTH_SHORT).show();


        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }


    private boolean isServiceOk() {

        GoogleApiAvailability GoogleApi = GoogleApiAvailability.getInstance();
        int result = GoogleApi.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApi.isUserResolvableError(result)) {
            Dialog dialog = GoogleApi.getErrorDialog(this, result, PERMISSION_REQUEST_CODE, task ->
                    Toast.makeText(this, "Dialogue is cancelled by user", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(this, "Google Play Service are Required by this application", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permissions")
                    .setMessage("Please Enabled GPS")
                    .setPositiveButton("Yes", (((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE);
                    })))
                    .setCancelable(false)
                    .show();
        }

        return false;
    }


    private boolean checkLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (!checkLocationPermissionGranted()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            } else {
                requestLocationPermission();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.show();
        if (requestCode == REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            if (providerEnabled) {
                Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS is not Enabled, Unable to show User Location", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        MapBoxInitialization();



    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        // Toast.makeText(this, String.valueOf(point.getLongitude())+"-"+String.valueOf(point.getLatitude())+"-"+String.valueOf(point.getAltitude()), Toast.LENGTH_SHORT).show();

        GeoJsonSource source = ownMapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        button.setEnabled(true);
        return true;
    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback< DirectionsResponse >() {
                    @Override
                    public void onResponse(Call< DirectionsResponse > call, Response< DirectionsResponse > response) {
// You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, ownMapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call< DirectionsResponse > call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }
}