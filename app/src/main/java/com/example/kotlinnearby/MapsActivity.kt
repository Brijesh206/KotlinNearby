package com.example.kotlinnearby

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlinnearby.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker?=null

    // Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object{
        private const val MY_PERMISSION_CODE: Int = 200
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Request runtime permission
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallBack()
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
            else{
                buildLocationRequest()
                buildLocationCallBack()
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }

    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {
        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?keyword=cruise&location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=10000") //10km
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&key=AIzaSyC5AiKZrCm5SvdHBFTMyn2bWyO9937NW4M")
        Log.d("url_debug",googlePlaceUrl.toString())
        return googlePlaceUrl.toString()


    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0!!.locations.get(p0
                !!.locations.size-1) // Get last location
                if (mMarker!=null)
                {
                    mMarker!!.remove()
                }
                latitude=mLastLocation.latitude
                longitude=mLastLocation.longitude

                val latlng = LatLng(latitude,longitude)
                val markerOptions = MarkerOptions()
                    .position(latlng)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap!!.addMarker(markerOptions)

                //Move Camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            return false
        }
        else
            return true
    }

    // Override onRequestPermissionResult

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            MY_PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission()){
                            buildLocationRequest()
                            buildLocationCallBack()
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                            mMap!!.isMyLocationEnabled=true
                        }
                }
                else
                    Toast.makeText(this,"location Service Not Enabled", Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap!!.isMyLocationEnabled = true
            }
        } else
            mMap!!.isMyLocationEnabled = true
    }
}

