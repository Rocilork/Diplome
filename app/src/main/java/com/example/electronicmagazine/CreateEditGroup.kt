package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import com.example.electronicmagazine.Class.GroupClass
import com.example.electronicmagazine.Class.Items
import com.example.electronicmagazine.Class.Speciality
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Class.UserCurators
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class CreateEditGroup : AppCompatActivity() {
    val viewItems = ArrayList<UserCurators>()
    val viewItems2 = ArrayList<Speciality>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_edit_group)

        val supabase = createSupabaseClient(
            supabaseUrl = "https://eefpcpbldmzljygkugxt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlZnBjcGJsZG16bGp5Z2t1Z3h0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDg5NTgxMTksImV4cCI6MjAyNDUzNDExOX0.P0eB4dN0mC-nvLokB-5ZVqw15vG5LiqlwnXXvJzbUbw"
        ) {
            install(GoTrue)
            install(Postgrest)
            //install other modules
        }

        val spinCurator: Spinner = findViewById(R.id.curator)
        val spinSpeciality: Spinner = findViewById(R.id.speciality)

        //Получаем кураторов
        try {
            lifecycleScope.launch {
                val city = supabase.postgrest["Пользователь"].select() {
                    eq("id_роли", 2)
                }
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())

                try {
                    for (i in 0 until array.length()) {
                        val itemObj = array.getJSONObject(i)
                        val ФИО: String = itemObj.getString("ФИО")
                        val api = UserCurators(ФИО)
                        viewItems.add(api)
                    }
                } catch (e: JSONException) {
                    Log.e("!!!", e.message.toString())
                }
            }
        } catch (ex: Exception) {
            Log.e("!!!", ex.toString())
        }
        //Выпадающий список кураторов
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewItems)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinCurator.adapter = arrayAdapter

        spinCurator.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //Получаем специальности
        try {
            lifecycleScope.launch {
                val city = supabase.postgrest["Специальность"].select()
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())

                try {
                    for (i in 0 until array.length()) {
                        val itemObj = array.getJSONObject(i)
                        val Название: String = itemObj.getString("Название")
                        val api = Speciality(Название)
                        viewItems2.add(api)
                    }
                } catch (e: JSONException) {
                    Log.e("!!!", e.message.toString())
                }
            }
        } catch (ex: Exception) {
            Log.e("!!!", ex.toString())
        }
        //Выпадающий список специальностей
        val arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewItems2)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinSpeciality.adapter = arrayAdapter2

        spinSpeciality.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun onBack (view: View){
        val intent = Intent(this, Administrator::class.java)
        startActivity(intent)
    }
}