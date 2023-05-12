package com.kabekus.googlemapsproject

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.kabekus.googlemapsproject.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedpreferences : SharedPreferences
    private var trackBool : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        sharedpreferences = this.getSharedPreferences("com.kabekus.googlemapsperoject", MODE_PRIVATE)
        trackBool = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{

            override fun onLocationChanged(location: Location) {
                trackBool = sharedpreferences.getBoolean("trackBool",false)
                if (!trackBool!!){
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    sharedpreferences.edit().putBoolean("trackBool",true).apply()
                }
            }

        }
         if (ContextCompat.checkSelfPermission(
                 this,Manifest.permission.ACCESS_FINE_LOCATION)
                  != PackageManager.PERMISSION_GRANTED){

             if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                 Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE)
                     .setAction("Give Permission"){
                         permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                 }.show()
             }else{
                 permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
             }
         }else {
             locationManager.requestLocationUpdates(
                 LocationManager.GPS_PROVIDER,
                 0,
                 0f,
                 locationListener
             )
             val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
             if (lastLocation != null){
                 val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
             }
             mMap.isMyLocationEnabled = true
         }

    }


    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                if (ContextCompat.checkSelfPermission(
                        this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null){
                        val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                    }
                    mMap.isMyLocationEnabled = true
                }

            }else{
                Toast.makeText(this@MapsActivity,"Permission Needed !",Toast.LENGTH_LONG).show()
            }

        }
    }
}