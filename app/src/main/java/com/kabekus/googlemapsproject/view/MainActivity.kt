package com.kabekus.googlemapsproject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.kabekus.googlemapsproject.R
import com.kabekus.googlemapsproject.adapter.PlaceAdapter
import com.kabekus.googlemapsproject.databinding.ActivityMainBinding
import com.kabekus.googlemapsproject.model.Place
import com.kabekus.googlemapsproject.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val db = Room.databaseBuilder(applicationContext, PlaceDatabase::class.java,"PlaceDB").build()
        val placeDao = db.placeDAO()

        compositeDisposable.add(
            placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this :: handleResponse)
        )
    }

    private fun handleResponse(placeList : List<Place>){
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)
        val adapter = PlaceAdapter(placeList)
        binding.recyclerViewMain.adapter = adapter

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.place_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addPlace) {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}