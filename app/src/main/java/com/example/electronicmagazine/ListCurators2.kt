package com.example.electronicmagazine

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Adapter.Adapter_FIO_Curator
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.Flow

class ListCurators2 : AppCompatActivity() {
    val viewItems = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_curators2)

        val Delbut: Button = findViewById(R.id.delete_curator)
        val Savbut: Button = findViewById(R.id.save_curator)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_curator)

        val edit_FIO: EditText = findViewById(R.id.FIO_curator)
        val edit_Log: EditText = findViewById(R.id.login_curator)
        val edit_Pas: EditText =findViewById(R.id.password_curator)

        //Корутина
        lifecycleScope.launch {
            //Подключаемся к таблице
            val users = SB.getClient().postgrest["Пользователь"].select()
            {
               //Передаём данные пользователя
                intent.getStringExtra("itemText")?.let { eq("ФИО", intent.getStringExtra("itemText")!!) }
            }.decodeSingle<User>()
            //Получаем данные пользователя в текстовом поле
            edit_FIO.setText(users.ФИО)
        }

        //удаление
        Delbut.setOnClickListener {
            val fioR = edit_FIO.text.toString()
            val intent = Intent(this, ListCurators::class.java)

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Вы уверены, что хотите удалить?")
            builder.setTitle(android.R.string.dialog_alert_title)
            builder.setIcon(R.drawable.iconka)
            //кнопка Да и обработчик событий
            builder.setPositiveButton("Да",
                DialogInterface.OnClickListener { dialog, id -> this.lifecycleScope.launch {
                        try {
                            //Удаляем пользователя из таблицы
                            SB.getClient().postgrest["Пользователь"].delete {
                                eq("ФИО", fioR)
                            }
                            //Очищаем текстовое поле
                            edit_FIO.text.clear()
                            //Получаем уведомление
                            Toast.makeText(applicationContext, "Куратор удалён!", Toast.LENGTH_SHORT).show()

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

        //сохранить
        Savbut.setOnClickListener {
            val fioR = edit_FIO.text.toString()
            val intent = Intent(this, ListCurators::class.java)

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Вы уверены, что хотите изменить?")
            builder.setTitle(android.R.string.dialog_alert_title)
            builder.setIcon(R.drawable.iconka)
            //кнопка Да и обработчик событий
            builder.setPositiveButton("Да",
                DialogInterface.OnClickListener { dialog, id -> this.lifecycleScope.launch {
                    try {
                        //Запоминаем сессию выбранного пользователя
                        val userId = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true).id
                        //Обновляем данные пользователя в таблице
                        SB.getClient().postgrest["Пользователь"].update(
                            {
                                set("ФИО", fioR)
                            }
                        ) {
                            eq("ID_пользователя", userId)
                        }
                        //Очищаем текстовое поле
                        edit_FIO.text.clear()
                        //Получаем уведомление
                        Toast.makeText(applicationContext, "Изменения сохранены!", Toast.LENGTH_SHORT).show()
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

        //Получаем кураторов
        try {
            lifecycleScope.launch {
                val users = SB.getClient().postgrest["Пользователь"].select(){
                    eq("id_роли", 2)
                }

                val buf = StringBuilder()
                buf.append(users.body.toString()).append("\n")
                val array = JSONArray(buf.toString())

                Log.e("!!!", users.body.toString())
                try {
                    for(i in 0 until   array.length()){
                        val item = array.getJSONObject(i)
                        val ID_пользователя = item.getString("ID_пользователя")
                        val ФИО = item.getString("ФИО")
                        val id_роли = item.getInt("id_роли")
                        val api = User(ID_пользователя, ФИО, id_роли)
                        viewItems.add(api)
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
                recyclerView.adapter = Adapter_FIO_Curator(viewItems, this@ListCurators2)
            }
        }
        timer.start()
    }

    //Функция для проверки на корректность введённой электронной почты
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    fun onBack (view: View){
        val intent = Intent(this, Administrator::class.java)
        startActivity(intent)
    }
}
