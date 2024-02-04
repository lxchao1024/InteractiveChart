package com.wk.demo.model

import android.content.Context
import android.content.res.AssetManager
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import kotlin.collections.ArrayList


object JsonUtils {

    private val gson = Gson()

    fun <T> jsonToList(data: String, tClass: Class<T>): ArrayList<T> {
        val mList = ArrayList<T>()
        if (TextUtils.isEmpty(data)) return mList
        try {
            val mArray = JSONArray(data)
            try {
                (0 until mArray.length()).mapTo(mList) {
                    jsonToListItem(
                        mArray.get(it).toString(),
                        tClass
                    )
                }
            } catch (e: Exception) {
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return mList
    }

    fun <T> jsonToBean(data: String, tClass: Class<T>): T? {
        return try {
            gson.fromJson(data, tClass)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun <T> jsonToListItem(data: String, tClass: Class<T>): T = gson.fromJson(data, tClass)

    @Throws(IOException::class)
    fun AssetJSONFile(
        filename: String?,
        context: Context
    ): String? {
        filename?.let {
            val manager = context.assets
            val file = manager.open(filename)
            val formArray = ByteArray(file.available())
            file.read(formArray)
            file.close()
            return String(formArray)
        }
        return ""
    }


    /**
     * 得到json文件中的内容
     * @param context
     * @param fileName
     * @return
     */
    fun getJson(context: Context, fileName: String?): String? {
        val stringBuilder = StringBuilder()
        //获得assets资源管理器
        val assetManager: AssetManager = context.getAssets()
        //使用IO流读取json文件内容
        try {
            val bufferedReader = BufferedReader(
                InputStreamReader(
                    fileName?.let { assetManager.open(it) }, "utf-8"
                )
            )
            var line: String? = ""
            while (bufferedReader.readLine().also({ line = it }) != null) {
                val append = stringBuilder.append(line)
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

}
