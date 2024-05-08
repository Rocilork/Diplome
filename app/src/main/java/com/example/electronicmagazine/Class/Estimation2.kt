package com.example.electronicmagazine.Class
import kotlinx.serialization.Serializable
@Serializable
data class Estimation2 (val ID_оценки: Int, val id_студента: String = "", val Оценка_НБ: String = "")