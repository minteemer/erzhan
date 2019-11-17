package iu.quaraseequi.erzhan.data

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import iu.quaraseequi.erzhan.domain.entities.logger.log
import java.lang.reflect.Type

val defaultGson: Gson by lazy { Gson() }

/**
 * Tries to parse object of class [classOfT] from given [json]
 * @return parsed object or null in case of parsing error
 */
private fun <T> Gson.fromJsonOrNull(json: String, classOfT: Class<T>): T? = try {
    fromJson(json, classOfT)
} catch (e: JsonSyntaxException) {
    e.log("GsonTools", "Json parsing error ${classOfT.canonicalName}")
    null
}

/**
 * Tries to parse object of type [typeOfT] from given [json]
 * @return parsed object or null in case of parsing error
 */
private fun <T> Gson.fromJsonOrNull(json: String, typeOfT: Type): T? = try {
    fromJson(json, typeOfT)
} catch (e: JsonSyntaxException) {
    e.log("GsonTools", "Json parsing error $typeOfT")
    null
}

fun <T> String.fromJson(typeOfT: Type): T = defaultGson.fromJson(this, typeOfT)

fun <T> String.fromJson(classOfT: Class<T>): T = defaultGson.fromJson(this, classOfT)

fun <T> String.fromJsonOrNull(typeOfT: Type): T? = defaultGson.fromJsonOrNull(this, typeOfT)

fun <T> String.fromJsonOrNull(classOfT: Class<T>): T? = defaultGson.fromJsonOrNull(this, classOfT)

fun Any.toJson() = defaultGson.toJson(this)