package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Adapter.Adapter_FIO_Curator
import com.example.electronicmagazine.Adapter.Adapter_Student
import com.example.electronicmagazine.Class.Estimation
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.util.Identity.decode
import io.ktor.util.Identity.encode
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class ListCurators : AppCompatActivity() {
    val viewItems = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_curators)

        val Delbut: Button = findViewById(R.id.delete_curator)
        val Savbut: Button = findViewById(R.id.save_curator)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_curator)

        val edit_FIO: EditText = findViewById(R.id.FIO_curator)
        val edit_Log: EditText = findViewById(R.id.login_curator)
        val edit_Pas: EditText =findViewById(R.id.password_curator)

        //удаление
        Delbut.setOnClickListener {
                Toast.makeText(applicationContext, "Выберите куратора!", Toast.LENGTH_SHORT).show()
        }
        //сохранить
        Savbut.setOnClickListener {
            val fioR = edit_FIO.text.toString()
            val logR = edit_Log.text.toString()
            val pasR = edit_Pas.text.toString()

            val rol: Int = 2
            try {
                if(fioR == "" || logR == "" || pasR == ""){
                    Toast.makeText(applicationContext, "Поля не все заполнены!", Toast.LENGTH_SHORT).show()
                } else if(pasR <= pasR.length.toString(6)){
                    Toast.makeText(applicationContext, "Пароль не меньше шести символов!", Toast.LENGTH_SHORT).show()
                } else if(logR.toBoolean() == logR.isEmailValid()){
                    Toast.makeText(applicationContext, "Почта некорректна!", Toast.LENGTH_SHORT).show()
                } else{
                    lifecycleScope.launch {
                        val user = SB.getClient().gotrue.signUpWith(Email) {
                            email = logR
                            password = pasR
                        }

                        val userId = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true).id
                        val city = User(ID_пользователя = userId, ФИО = fioR, id_роли = rol)
                        SB.getClient().postgrest["Пользователь"].insert(city)

                        Toast.makeText(applicationContext, "Куратор зарегистрирован!", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                }
            }catch (ex: JSONException) {
                Log.e("!!!", ex.message.toString())
            }
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
                recyclerView.adapter = Adapter_FIO_Curator(viewItems, this@ListCurators)
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