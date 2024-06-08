package com.example.electronicmagazine.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Class.Estimation2
import com.example.electronicmagazine.Curator2
import com.example.electronicmagazine.R

class Adapter_Curator(val api: ArrayList<Estimation2>, val context: Context): RecyclerView.Adapter<Adapter_Curator.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fio: TextView = itemView.findViewById(R.id.FIO)
        val est: TextView = itemView.findViewById(R.id.estimation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter_Curator.MyViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.curator_spisok, parent, false)
        return MyViewHolder(listItemView)
    }

    override fun onBindViewHolder(holder: Adapter_Curator.MyViewHolder, position: Int) {
        val apis = api.get(position)
        holder.fio.text = apis.id_студента
        holder.est.text = apis.Оценка_НБ

        holder.itemView.setOnClickListener {
            // обработка нажатия
            val intent = Intent(context, Curator2::class.java)
            intent.putExtra("itemText", api[position].ID_оценки)
            intent.putExtra("itemTextID", api[position].id_студента)
            intent.putExtra("itemTextEst", api[position].Оценка_НБ)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return api.size
    }
}