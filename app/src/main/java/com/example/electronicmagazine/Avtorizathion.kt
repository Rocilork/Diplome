package com.example.electronicmagazine

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.Exception
class Avtorizathion : AppCompatActivity() {
    @Suppress("DEPRECATION")
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
                    val yourCoroutineScope = lifecycleScope
                    yourCoroutineScope.launch {
                        //Обработка на ошибку
                        try {
                            //Авторизация пользователя по электронной почте и паролю
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
                                else -> { // Если ошибка
                                    print("Ошибка")
                                }
                            }


                            val channel = SB.getClient().realtime.createChannel("channelId2") {
                                //optional config
                            }
                            val changes = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                                table = "Пользователь"
                                // filter = "name=in.(red, blue, yellow)"
                            }
                            changes.onEach {
                                println(it.record)
                                Log.e("UPD:"," ${it.record}")

                                val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationCompat.Builder(this@Avtorizathion, " ${it.record}")
                                        .setSmallIcon(R.drawable.iconka)
                                        .setContentTitle("Уведомление")
                                        .setContentText("Изменения")
                                        .setAutoCancel(true)
                                        .setPriority(Notification.PRIORITY_DEFAULT)
                                } else {
                                    TODO("VERSION.SDK_INT < O")
                                    return@onEach
                                }

                                with(NotificationManagerCompat.from(this@Avtorizathion)){
                                    if (ActivityCompat.checkSelfPermission(this@Avtorizathion, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        notify(NOTIFICATION_SERVICE.length, builder.build())
                                        return@with
                                    //notify(changes.toString().length, builder.build())
                                    }
                                }


                            //Toast.makeText(applicationContext, "${it.record}", Toast.LENGTH_LONG).show()
                            }.launchIn(yourCoroutineScope)
                            SB.getClient().realtime.connect()
                            channel.join()
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