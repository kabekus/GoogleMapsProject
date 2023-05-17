package com.kabekus.googlemapsproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabekus.googlemapsproject.databinding.RecyclerRowBinding
import com.kabekus.googlemapsproject.model.Place
import com.kabekus.googlemapsproject.view.MapsActivity

class PlaceAdapter(val placeList : List<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceHoler>() {
    class PlaceHoler(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHoler {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaceHoler(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: PlaceHoler, position: Int) {
        holder.recyclerRowBinding.recyclerTxt.text = placeList.get(position).name

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }
}


