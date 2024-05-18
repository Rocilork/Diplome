package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Adapter.Adapter_Student
import com.example.electronicmagazine.Class.Estimation
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Student : AppCompatActivity() {
    //Используем класс, в котором хранятся данные
    val viewItems = ArrayList<Estimation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val textView: TextView = findViewById(R.id.textFIO)

        //Корутина
        lifecycleScope.launch {
            //По id после авторизации используя объект получаем данные авторизованного пользователя
            val session_user = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true)
            val users = SB.getClient().postgrest["Пользователь"].select()
            {
                eq("ID_пользователя", session_user.id)
            }.decodeSingle<User>()
            //Получаем ФИО авторизованного пользователя
            textView.setText(users.ФИО)
        }

        //Получаем успеваемоть студента
        try {
            lifecycleScope.launch {
                val session_user = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true)
                val columns = Columns.raw("""Дата, Оценка_НБ, id_предметаСпециальностей (id_предмета (Название))""".trimIndent())
                val city = SB.getClient().postgrest["Оценки"].select(columns = columns){
                    eq("id_студента", session_user.id)
                }

                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                val array = JSONArray(buf.toString())

                Log.e("!!!", city.body.toString())
                try {
                    for(i in 0 until   array.length()){
                        val item = array.getJSONObject(i)
                        val Дата = item.getString("Дата")
                        val Оценка_НБ = item.getString("Оценка_НБ")
                        val id_предметаСпециальностей = item.getJSONObject("id_предметаСпециальностей")
                        val id_предмета = id_предметаСпециальностей.getJSONObject("id_предмета")
                        val Название = id_предмета.getString("Название")
                        val api = Estimation(Дата, Оценка_НБ, Название)
                        viewItems.add(api)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }
        //Заполняем список
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val timer = object: CountDownTimer(3000, 1000){
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish(){
                recyclerView.adapter = Adapter_Student(viewItems)
            }
        }
        timer.start()
    }
    fun onBack (view: View){
        val intent = Intent(this, Avtorizathion::class.java)
        startActivity(intent)
    }
}