package com.example.electronicmagazine.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Class.DateEstimation
import com.example.electronicmagazine.Class.Estimation2
import com.example.electronicmagazine.R

class Adapter_DateCurator(val api: ArrayList<DateEstimation>): RecyclerView.Adapter<Adapter_DateCurator.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter_DateCurator.MyViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.date_spisok, parent, false)
        return MyViewHolder(listItemView)
    }

    override fun onBindViewHolder(holder: Adapter_DateCurator.MyViewHolder, position: Int) {
        val apis = api.get(position)
        holder.date.text = apis.Дата
    }

    override fun getItemCount(): Int {
        return api.size
    }
}