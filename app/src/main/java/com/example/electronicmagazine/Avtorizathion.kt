package com.example.electronicmagazine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.Exception
class Avtorizathion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avtorizathion)
        val button: Button = findViewById(R.id.buttonEnter)
        val log: EditText = findViewById(R.id.login)
        val pass: EditText = findViewById(R.id.password)

        //Обработчик авторизации
        button.setOnClickListener {
            val logA = log.text.toString()
            val passA = pass.text.toString()

            //Переход на другое окно
            val intentS = Intent(this, Student::class.java)
            val intentC = Intent(this, Curator::class.java)
            val intentA = Intent(this, Administrator::class.java)
            //Обработка на ошибку
            try{
                //Проверка на наличие симвоволов
                if(logA == "" || passA == ""){
                    Toast.makeText(applicationContext, "Поля не все заполнены!", Toast.LENGTH_SHORT).show()
                } else if(logA.toBoolean() == logA.isEmailValid()){
                    Toast.makeText(applicationContext, "Почта некорректна!", Toast.LENGTH_SHORT).show()
                }
                else{
                    //Корутина
                    lifecycleScope.launch {
                        //Обработка на ошибку
                        try {
                            //Подключаем объект для запоминания сеанса пользователя после авторизации
                            SB.getClient().gotrue.loginWith(Email) {
                                email = logA
                                password = passA
                            }
                            Toast.makeText(applicationContext, "Вы авторизовались!", Toast.LENGTH_SHORT).show()
                            //Получаем пользователя по id
                            val session_user = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true)
                            val city = SB.getClient().postgrest["Пользователь"].select(){
                                eq("ID_пользователя", session_user.id)
                            }.decodeSingle<User>()
                            //Выбор на какое окно перейти в зависимости от роли пользователя
                            when (city.id_роли) {
                                1 -> print(startActivity(intentS))
                                2 -> print(startActivity(intentC))
                                3 -> print(startActivity(intentA))
                                else -> { // Внимание на блок
                                    print("Ошибка")
                                }
                            }
                            //Если произошла ошибка
                        }catch (ex: Exception){
                            Toast.makeText(applicationContext, "Такого пользователя нет!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch (ex: JSONException) {
                Log.e("!!!", ex.message.toString())
            }
        }
    }

    //Функция для проверки на корректность введённой электронной почты
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}