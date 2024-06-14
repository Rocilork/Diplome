package com.example.electronicmagazine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.electronicmagazine.Class.Estimation
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class Profil : AppCompatActivity() {
    val viewItems = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val butSave: Button = findViewById(R.id.butSave)
        val textHwo: TextView = findViewById(R.id.textHwo)
        val editFio: EditText = findViewById(R.id.editFio)
        val editGmail: EditText = findViewById(R.id.editGmail)

        //Корутина
        lifecycleScope.launch {
            //По id после авторизации используя объект получаем данные авторизованного пользователя
            val session_user = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true)
            val users = SB.getClient().postgrest["Пользователь"].select()
            {
                eq("ID_пользователя", session_user.id)
            }.decodeSingle<User>()
            //Получаем ФИО авторизованного пользователя
            editFio.setText(users.ФИО)
            //Получаем email авторизованного пользователя
            editGmail.setText(session_user.email)
        }

        //Корутина
        try {
            lifecycleScope.launch {
                val session_user = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true)
                val columns = Columns.raw("""id_роли (Роль)""".trimIndent())
                val city = SB.getClient().postgrest["Пользователь"].select(columns = columns){
                    eq("ID_пользователя", session_user.id)
                }

                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                val array = JSONArray(buf.toString())

                Log.e("!!!", city.body.toString())
                try {
                    for(i in 0 until   array.length()){
                        val item = array.getJSONObject(i)
                        val id_роли = item.getJSONObject("id_роли")
                        val Роль = id_роли.getString("Роль")
                        viewItems.add(Роль)
                    }
                }catch (e: JSONException){
                    Log.e("!!!", e.message.toString())
                }

                textHwo.text = viewItems.toString()
            }
        }catch (ex: Exception){
            Log.e("!!!", ex.toString())
        }

        butSave.setOnClickListener {

        }
    }
    fun onBack (view: View){
        val intent = Intent(this, Student::class.java)
        startActivity(intent)
    }
}