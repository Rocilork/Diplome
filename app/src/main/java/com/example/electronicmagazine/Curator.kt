package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Adapter.Adapter_Curator
import com.example.electronicmagazine.Adapter.Adapter_DateCurator
import com.example.electronicmagazine.Adapter.Adapter_Student
import com.example.electronicmagazine.Class.DateEstimation
import com.example.electronicmagazine.Class.Estimation
import com.example.electronicmagazine.Class.Estimation2
import com.example.electronicmagazine.Class.GroupClass
import com.example.electronicmagazine.Class.Items
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import kotlin.Exception

class Curator : AppCompatActivity() {
    val viewItems = ArrayList<Items>()
    val viewItems2 = ArrayList<GroupClass>()
    val viewItems3 = ArrayList<Estimation2>()
    val viewItems4 = ArrayList<DateEstimation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curator)

        val spinGroup: Spinner = findViewById(R.id.group)
        val spinItem: Spinner = findViewById(R.id.item)

        val buttonBack: Button = findViewById(R.id.ButtonBack)
        val buttonForward: Button = findViewById(R.id.ButtonForward)

        val butSave: Button = findViewById(R.id.save)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val recyclerViewDate: RecyclerView = findViewById(R.id.recyclerViewDate)

//        val items = arrayOf("Стандартизация", "Разработка баз данных")
//        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
//        spinItem.adapter = arrayAdapter

        //Получаем группы
        try {
            lifecycleScope.launch {
                val session_user = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true)
                val city = SB.getClient().postgrest["Группы"].select(){
                    eq("id_пользователя", session_user.id)
                }
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())
                try {
                    for(i in 0 until   array.length()){
                        val itemObj = array.getJSONObject(i)
                        val Название: String = itemObj.getString("Название")
                        val api = GroupClass(Название)
                        viewItems2.add(api)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }

        //Выпадающий список групп
        //        val items = arrayOf("Стандартизация", "Разработка баз данных")
        //val items = arrayListOf(viewItems2)
        //val items = viewItems2.toString()
        val arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewItems2)
        //arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinGroup.adapter = arrayAdapter2

        spinGroup.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //recyclerView.adapter = spinGroup[p2]
                //recyclerView.adapter = Adapter_Curator(viewItems2)
                //spinGroup.textAlignment = selectG.toString().length

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //Получаем предметы
        try {
            lifecycleScope.launch {
                val city = SB.getClient().postgrest["Предмет"].select()
                //Log.e("!!!", city.body.toString())
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())

                try {
                    for(i in 0 until   array.length()){
                        val itemObj = array.getJSONObject(i)
                        val Название: String = itemObj.getString("Название")
                        val api = Items(Название)
                        viewItems.add(api)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }
        //Выпадающий список предметов

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewItems)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinItem.adapter = arrayAdapter

        spinItem.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //recyclerView.adapter = spinItem[p2]
                //recyclerView.adapter = Adapter_Curator(viewItems)

                //spinItem.adapter = arrayAdapter

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //Получаем студентов
        try {
            lifecycleScope.launch {
//                val columns = Columns.raw("""ID_пользователя, ФИО""".trimIndent())
//                val city = supabase.postgrest["Пользователь"].select(columns = columns){
//                    eq("id_роли", 1)
//                }
//                //Получаем только студентов
//                Log.e("!!!", city.body.toString())

                val columns4 = Columns.raw("""ID_оценки, Оценка_НБ, id_студента""".trimIndent())
                val city4 = SB.getClient().postgrest["Оценки"].select(columns = columns4){
                    //eq("id_студента", city.body.toString())
                    //city.body.toString()
                }
                //Получаем ФИО студентов за место их id
                Log.e("!!!", city4.body.toString())

                val buf = StringBuilder()
                buf.append(city4.body.toString()).append("\n")
                val array = JSONArray(buf.toString())

                try {
                    for(i in 0 until   array.length()){
                        val item = array.getJSONObject(i)
                        val ID_оценки = item.getInt("ID_оценки")
                        val id_студента = item.getString("id_студента")
                        val Оценка_НБ = item.getString("Оценка_НБ")
                        //val ФИО = id_студента.getString("ID_студента")
                        val api = Estimation2(ID_оценки, id_студента, Оценка_НБ)
                        viewItems3.add(api)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val timer = object: CountDownTimer(3000, 1000){
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish(){
                recyclerView.adapter = Adapter_Curator(viewItems3, this@Curator)
            }
        }
        timer.start()

        //Получаем даты
        try {
            lifecycleScope.launch {
                val columns4 = Columns.raw("""Дата""".trimIndent())
                val city4 = SB.getClient().postgrest["Оценки"].select(columns = columns4)
                Log.e("!!!", city4.body.toString())

                val buf = StringBuilder()
                buf.append(city4.body.toString()).append("\n")
                val array = JSONArray(buf.toString())

                try {
                    for(i in 0 until   array.length()){
                        val item = array.getJSONObject(i)
                        val Дата = item.getString("Дата")
                        val api = DateEstimation(Дата)
                        viewItems4.add(api)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }
        recyclerViewDate.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val timer2 = object: CountDownTimer(3000, 1000){
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish(){
                recyclerViewDate.adapter = Adapter_DateCurator(viewItems4)
            }
        }
        timer2.start()

        //Отматать дату назад
        buttonBack.setOnClickListener {

        }
        //Отматать дату вперёд
        buttonForward.setOnClickListener {

        }

        butSave.setOnClickListener {
            Toast.makeText(applicationContext, "Выберите студента!", Toast.LENGTH_SHORT).show()
        }
    }
    fun onBack (view: View){
        val intent = Intent(this, Avtorizathion::class.java)
        startActivity(intent)
    }
}