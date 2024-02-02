package com.imsoft.kotlinmapapp

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.imsoft.kotlinmapapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean: Boolean? = null
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher()

        sharedPreferences = this.getSharedPreferences("com.imsoft.kotlinmapapp", MODE_PRIVATE)
        trackBoolean = false

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        selectedLat = 0.0
        selectedLng = 0.0
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this@MapsActivity)

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { location ->

            trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)

            if (trackBoolean == false) {
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
            }


        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Snackbar.make(binding.root, "Permission Needed!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK") {
                        // request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
            } else {
                // request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

        } else {
            // permission granted
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0f, locationListener
            )

            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (lastLocation != null) {
                val userLastLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 15f))
            }

            mMap.isMyLocationEnabled = true
        }
    }

    private fun registerLauncher() {

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

                if (result) {

                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 1000, 0f, locationListener
                        )

                        val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                        if (lastLocation != null) {
                            val userLastLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 15f))
                        }

                        mMap.isMyLocationEnabled = true
                    }

                } else {
                    Toast.makeText(this@MapsActivity, "Permission Needed!", Toast.LENGTH_LONG)
                        .show()
                }

            }

    }

    override fun onMapLongClick(p0: LatLng) {

        mMap.clear()

        mMap.addMarker(MarkerOptions().position(p0))

        selectedLat = p0.latitude
        selectedLng = p0.longitude

    }

    fun saveBtnClick(view: View) {}
    fun deleteBtnClick(view: View) {}
}