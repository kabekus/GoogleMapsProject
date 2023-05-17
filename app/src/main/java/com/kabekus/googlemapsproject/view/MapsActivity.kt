package com.kabekus.googlemapsproject.view

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.kabekus.googlemapsproject.R
import com.kabekus.googlemapsproject.databinding.ActivityMapsBinding
import com.kabekus.googlemapsproject.model.Place
import com.kabekus.googlemapsproject.roomdb.PlaceDAO
import com.kabekus.googlemapsproject.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener{


    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedpreferences : SharedPreferences
    private var trackBool : Boolean? = null
    private var selectedLat : Double? = null
    private var selectedLng : Double? = null
    private lateinit var db : PlaceDatabase
    private lateinit var placeDao : PlaceDAO
    val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        sharedpreferences = this.getSharedPreferences("com.kabekus.googlemapsperoject", MODE_PRIVATE)
        trackBool = false
        selectedLat = 0.0
        selectedLng = 0.0

        db = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"PlaceDB").build()
        placeDao = db.placeDAO()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this@MapsActivity)
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{

            override fun onLocationChanged(location: Location) {
                trackBool = sharedpreferences.getBoolean("trackBool",false)
                if (!trackBool!!){
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10f))
                    sharedpreferences.edit().putBoolean("trackBool",true).apply()
                }
            }

        }
         if (ContextCompat.checkSelfPermission(
                 this,Manifest.permission.ACCESS_FINE_LOCATION)
                  != PackageManager.PERMISSION_GRANTED){

             if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                 Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                         permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                 }.show()
             }else{
                 permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
             }
         }else {
             locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
             val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
             if (lastLocation != null){
                 val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,10f))
             }
             mMap.isMyLocationEnabled = true
         }

    }


    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null){
                        val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,10f))
                    }
                    mMap.isMyLocationEnabled = true
                }

            }else{
                Toast.makeText(this@MapsActivity,"Permission Needed !",Toast.LENGTH_LONG).show()
            }

        }
    }

    //Uzun tıklanınca marker ekleme işlemi.
    override fun onMapClick(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))

        selectedLat = latLng.latitude
        selectedLng = latLng.longitude
    }

    fun save(view : View){
        val place = Place(binding.placeNameTxt.text.toString(),selectedLat!!,selectedLng!!)
        compositeDisposable.add(
            placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(){
        val intent = Intent(this@MapsActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

    fun delete(view: View){

    }
}