package com.example.electronicmagazine

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicmagazine.Adapter.Adapter_FIO_Student
import com.example.electronicmagazine.Class.ClassGroups
import com.example.electronicmagazine.Class.User
import com.example.electronicmagazine.Object.SB
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class CreateEditGroup : AppCompatActivity() {
    val viewItems = ArrayList<String>()
    val viewItems2 = ArrayList<String>()
    val viewItems3 = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_edit_group)

        val spinCurator: Spinner = findViewById(R.id.curator)
        val spinSpeciality: Spinner = findViewById(R.id.speciality)

        val groupName: EditText = findViewById(R.id.nameGroup)
        val edit_FIO: EditText = findViewById(R.id.FIO_student)
        val edit_Log: EditText = findViewById(R.id.login_student)
        val edit_Pas: EditText = findViewById(R.id.password_student)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewStudent)

        val butSaveGroup: Button = findViewById(R.id.saveG)
        val butSaveStudent: Button = findViewById(R.id.saveS)
        val butDeleteStudent: Button = findViewById(R.id.deleteS)

        //Получаем кураторов
        try {
            lifecycleScope.launch {
                val city = SB.getClient().postgrest["Пользователь"].select() {
                    eq("id_роли", 2)
                }
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())

                try {
                    for (i in 0 until array.length()) {
                        val itemObj = array.getJSONObject(i)
                        val ФИО: String = itemObj.getString("ФИО")
                        viewItems.add(ФИО)
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
                val city = SB.getClient().postgrest["Специальность"].select()
                val buf = StringBuilder()
                buf.append(city.body.toString()).append("\n")
                var array: JSONArray = JSONArray(buf.toString())

                try {
                    for (i in 0 until array.length()) {
                        val itemObj = array.getJSONObject(i)
                        val Название: String = itemObj.getString("Название")
                        viewItems2.add(Название)
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

        //Получаем студентов
        try {
            lifecycleScope.launch {
                val users = SB.getClient().postgrest["Пользователь"].select(){
                    eq("id_роли", 1)
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
                        viewItems3.add(api)
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
                recyclerView.adapter = Adapter_FIO_Student(viewItems3, this@CreateEditGroup)
            }
        }
        timer.start()

        //Удаление студента
        butDeleteStudent.setOnClickListener {
            Toast.makeText(applicationContext, "Выберите студента!", Toast.LENGTH_SHORT).show()
        }
        //Сохранение студента
        butSaveStudent.setOnClickListener {
            val fioR = edit_FIO.text.toString()
            val logR = edit_Log.text.toString()
            val pasR = edit_Pas.text.toString()

            val rol: Int = 1
            try {
                if(fioR == "" || logR == "" || pasR == ""){
                    Toast.makeText(applicationContext, "Поля не все заполнены!", Toast.LENGTH_SHORT).show()
                } else if(pasR <= pasR.length.toString(6)){
                    Toast.makeText(applicationContext, "Пароль не меньше шести символов!", Toast.LENGTH_SHORT).show()
                } else if(logR.toBoolean() == logR.isEmailValid()){
                    Toast.makeText(applicationContext, "Почта некорректна!", Toast.LENGTH_SHORT).show()
                } else{
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setMessage("Вы уверены, что хотите добавить?")
                    builder.setTitle(android.R.string.dialog_alert_title)
                    builder.setIcon(R.drawable.iconka)
                    builder.setPositiveButton("Да",
                        DialogInterface.OnClickListener { dialog, id -> this.lifecycleScope.launch {
                            try {
                                val user = SB.getClient().gotrue.signUpWith(Email) {
                                    email = logR
                                    password = pasR
                                }

                                val userId = SB.getClient().gotrue.retrieveUserForCurrentSession(updateSession = true).id
                                val city = User(ID_пользователя = userId, ФИО = fioR, id_роли = rol)
                                SB.getClient().postgrest["Пользователь"].insert(city)

                                Toast.makeText(applicationContext, "Студент добавлен!", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                            } catch (ex: JSONException) {
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
            }catch (ex: JSONException){
                Log.e("!!!", ex.message.toString())
            }
        }
        //Группа
        butSaveGroup.setOnClickListener {
            val groupR = groupName.text.toString()
            val spC = spinCurator.textAlignment.toString()
            val spS = spinSpeciality.textAlignment.toInt()

            val intent = Intent(this, Administrator::class.java)

            try {
                if(groupR == "" || spC == "" || spS == toString().length){
                    Toast.makeText(applicationContext, "Не всё заполнено!", Toast.LENGTH_SHORT).show()
                }else{
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setMessage("Вы уверены, что хотите сохранить?")
                    builder.setTitle(android.R.string.dialog_alert_title)
                    builder.setIcon(R.drawable.iconka)
                    builder.setPositiveButton("Да",
                        DialogInterface.OnClickListener { dialog, id -> this.lifecycleScope.launch {
                            try {
                                //val city = ClassGroups(ID_группы = 0, Название = groupR, id_специальности = spS, id_пользователя = spC)
                                val city = ClassGroups(ID_группы = 0, Название = groupR,)
                                SB.getClient().postgrest["Группы"].insert(city)
                                Toast.makeText(applicationContext, "Группа создана!", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                            } catch (ex: JSONException) {
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
            }catch (ex: JSONException){
                Log.e("!!!", ex.message.toString())
            }
        }
    }

    fun onBack (view: View){
        val intent = Intent(this, Administrator::class.java)
        startActivity(intent)
    }
    //Функция для проверки на корректность введённой электронной почты
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}