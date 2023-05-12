package com.kabekus.googlemapsproject

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.kabekus.googlemapsproject.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                println("Location: "+location.toString())
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
                }

            }else{
                Toast.makeText(this@MapsActivity,"Permission Needed !",Toast.LENGTH_LONG).show()
            }

        }
    }
}