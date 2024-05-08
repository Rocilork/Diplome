package com.example.electronicmagazine.Class
import kotlinx.serialization.Serializable
@Serializable
data class User (val ID_пользователя: String = "", val ФИО: String = "", val id_роли: Int)