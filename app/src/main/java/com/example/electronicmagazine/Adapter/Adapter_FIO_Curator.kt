package com.example.electronicmagazine.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.ListCurators
import com.example.electronicmagazine.ListCurators2
import com.example.electronicmagazine.R

class Adapter_FIO_Curator(val api: ArrayList<User>, val context: Context): RecyclerView.Adapter<Adapter_FIO_Curator.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val FIO: TextView = itemView.findViewById(R.id.fio_curator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.fio_curator, parent, false)
        return MyViewHolder(listItemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val apis = api.get(position)
        holder.FIO.text = apis.ФИО

        holder.FIO.setOnClickListener {
            // обработка нажатия
            val intent = Intent(context, ListCurators2::class.java)
            intent.putExtra("itemText", api[position].ФИО)
//            intent.putExtra("itemTextLog", api[position].Логин)
//            intent.putExtra("itemTextPas", api[position].Пароль)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return api.size
    }
}