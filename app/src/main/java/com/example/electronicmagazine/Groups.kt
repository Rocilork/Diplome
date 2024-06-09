package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class Groups : AppCompatActivity() {
    val viewItems = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        val spinGroup: Spinner = findViewById(R.id.group)
        val butDel: Button = findViewById(R.id.buttonDelete)
        val butEd: Button = findViewById(R.id.buttonEdite)

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
                        viewItems.add(Название)
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

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        butDel.setOnClickListener {
            val sG = spinGroup.textAlignment.toString()
            lifecycleScope.launch {
                try {
                    //Удаляем пользователя из таблицы
                    SB.getClient().postgrest["Группы"].delete {
                        eq("Название", sG)
                    }
                    //Получаем уведомление
                    Toast.makeText(applicationContext, "Группа удалена!", Toast.LENGTH_SHORT).show()

                    startActivity(intent)
                }catch (ex: JSONException){
                    Log.e("!!!", ex.message.toString())
                }
            }
        }

        butEd.setOnClickListener {
            val sG = spinGroup.textAlignment.toString()
            val intent = Intent(this, CreateEditGroup::class.java)
            startActivity(intent)
        }
    }

    fun onBack (view: View){
        val intent = Intent(this, Administrator::class.java)
        startActivity(intent)
    }
}