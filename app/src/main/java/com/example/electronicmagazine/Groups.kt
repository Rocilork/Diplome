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
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class Groups : AppCompatActivity() {
    val viewItems = ArrayList<GroupClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        val spinGroup: Spinner = findViewById(R.id.group)

        //Получаем группы
        try {
            lifecycleScope.launch {
                //val city = SB.getClient().postgrest["Группы"].select(columns = Columns.list("Название"))
                val city = SB.getClient().postgrest["Группы"].select()
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())

                try {
                    for(i in 0 until   array.length()){
                        val itemObj = array.getJSONObject(i)
                        //val ID_предмета: Int = itemObj.getInt("ID_группы")
                        val Название: String = itemObj.getString("Название")
                        val api = GroupClass(Название)
                        viewItems.add(api)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }
        //Выпадающий список групп
        val arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewItems)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinGroup.adapter = arrayAdapter2

        spinGroup.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
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