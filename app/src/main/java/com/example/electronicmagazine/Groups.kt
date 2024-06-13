package com.example.electronicmagazine

import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.gotrue.gotrue
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
        //удалить
        butDel.setOnClickListener {
            val sG = spinGroup.textAlignment.toString()

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Вы уверены, что хотите удалить?")
            builder.setTitle(android.R.string.dialog_alert_title)
            builder.setIcon(R.drawable.iconka)
            //кнопка Да и обработчик событий
            builder.setPositiveButton("Да",
                DialogInterface.OnClickListener { dialog, id -> this.lifecycleScope.launch {
                    try {
                        //Удаляем группу из таблицы
                        SB.getClient().postgrest["Группы"].delete {
                            eq("Название", sG)
                        }
                        Toast.makeText(applicationContext, "Группа удалена!", Toast.LENGTH_SHORT).show()

                        startActivity(intent)
                    }catch (ex: JSONException){
                        Log.e("!!!", ex.message.toString())
                    }
                }
                })
            //кнопка Нет и обработчик событий
            builder.setNegativeButton("Нет",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            builder.setCancelable(false)
            builder.create()
            builder.show()
        }
        //изменить
        butEd.setOnClickListener {
            val sG = spinGroup.textAlignment.toString()
            val intent = Intent(this, CreateEditGroup::class.java)

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Вы уверены, что хотите изменить?")
            builder.setTitle(android.R.string.dialog_alert_title)
            builder.setIcon(R.drawable.iconka)
            //кнопка Да и обработчик событий
            builder.setPositiveButton("Да",
                DialogInterface.OnClickListener { dialog, id -> this.lifecycleScope.launch {
                    try {
                        startActivity(intent)
                    }catch (ex: JSONException){
                        Log.e("!!!", ex.message.toString())
                    }
                }
                })
            //кнопка Нет и обработчик событий
            builder.setNegativeButton("Нет",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            builder.setCancelable(false)
            builder.create()
            builder.show()
        }
    }
    fun onBack (view: View){
        val intent = Intent(this, Administrator::class.java)
        startActivity(intent)
    }
}