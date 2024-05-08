package com.example.electronicmagazine.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Class.Estimation
import com.example.electronicmagazine.R

class Adapter_Student(val api: ArrayList<Estimation>): RecyclerView.Adapter<Adapter_Student.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val Date: TextView = itemView.findViewById(R.id.date)
        val Predmet: TextView = itemView.findViewById(R.id.predmet)
        val Estimat: TextView = itemView.findViewById(R.id.estimation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter_Student.MyViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.student_spisok, parent, false)
        return MyViewHolder(listItemView)
    }

    override fun onBindViewHolder(holder: Adapter_Student.MyViewHolder, position: Int) {
        val apis = api.get(position)
        holder.Date.text = apis.Дата
        holder.Predmet.text = apis.Название
        holder.Estimat.text = apis.Оценка_НБ
    }

    override fun getItemCount(): Int {
        return api.size
    }
}