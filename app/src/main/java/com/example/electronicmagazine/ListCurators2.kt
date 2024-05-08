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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Adapter.Adapter_FIO_Curator
import com.example.electronicmagazine.Class.User
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class ListCurators2 : AppCompatActivity() {
    val viewItems = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_curators2)

        val supabase = createSupabaseClient(
            supabaseUrl = "https://eefpcpbldmzljygkugxt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlZnBjcGJsZG16bGp5Z2t1Z3h0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDg5NTgxMTksImV4cCI6MjAyNDUzNDExOX0.P0eB4dN0mC-nvLokB-5ZVqw15vG5LiqlwnXXvJzbUbw"
        ) {
            install(GoTrue)
            install(Postgrest)
            //install other modules
        }

        val Delbut: Button = findViewById(R.id.delete_curator)
        val Savbut: Button = findViewById(R.id.save_curator)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_curator)

        val edit_FIO: EditText = findViewById(R.id.FIO_curator)
        //val edit_Log: EditText = findViewById(R.id.login_curator)
        //val edit_Pas: EditText =findViewById(R.id.password_curator)

        //Корутина
        lifecycleScope.launch {
            val users = supabase.postgrest["Пользователь"].select()
            {
                intent.getStringExtra("itemText")?.let { eq("ФИО", intent.getStringExtra("itemText")!!) }
            }.decodeSingle<User>()

            edit_FIO.setText(users.ФИО)
            //edit_FIO.text.clear()
            //edit_Log.setText(session_user.email)
            //edit_Pas.setText(session_user.pass)
        }

        //удаление
        Delbut.setOnClickListener {
            val fioR = edit_FIO.text.toString()
            val intent = Intent(this, ListCurators::class.java)

            lifecycleScope.launch {
                try {
                    supabase.postgrest["Пользователь"].delete {
                        eq("ФИО", fioR)
                    }

                    edit_FIO.text.clear()
                    Toast.makeText(applicationContext, "Куратор удалён!", Toast.LENGTH_SHORT).show()

                    startActivity(intent)
                }catch (ex: JSONException){
                    Log.e("!!!", ex.message.toString())
                }
            }
        }

        //сохранить
        Savbut.setOnClickListener {
            val fioR = edit_FIO.text.toString()
            val intent = Intent(this, ListCurators::class.java)

            lifecycleScope.launch {
                try {
                    val userId = supabase.gotrue.retrieveUserForCurrentSession(updateSession = true).id
                    supabase.postgrest["Пользователь"].update(
                        {
                            set("ФИО", fioR)
                        }
                    ) {
                            eq("ID_пользователя", userId)
                    }

                    edit_FIO.text.clear()
                    Toast.makeText(applicationContext, "Изменения сохранены!", Toast.LENGTH_SHORT).show()

                    startActivity(intent)
                }catch (ex: JSONException){
                    Log.e("!!!", ex.message.toString())
                }
            }
        }

        //Получаем кураторов
        try {
            lifecycleScope.launch {
                val users = supabase.postgrest["Пользователь"].select(){
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