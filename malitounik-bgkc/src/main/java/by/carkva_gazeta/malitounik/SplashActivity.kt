package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getBoolean("auto_dzen_noch", false)) {
            setlightSensor()
        }
        val data = intent.data
        val intent1 = Intent(this, MainActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val extras = intent.extras
        if (extras != null) {
            intent1.putExtras(extras)
            val widgetMun = "widget_mun"
            if (extras.getBoolean(widgetMun, false)) {
                intent1.putExtra(widgetMun, true)
            }
            val widgetDay = "widget_day"
            if (extras.getBoolean(widgetDay, false)) {
                intent1.putExtra(widgetDay, true)
            }
        }
        if (data != null) {
            intent1.data = data
            var file = ""
            val cursor2 = contentResolver.query(data, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
            cursor2?.moveToFirst()
            val nameIndex = cursor2?.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) ?: -1
            if (nameIndex >= 0) {
                file = cursor2?.getString(nameIndex) ?: ""
            }
            cursor2?.close()

            if (file != "") {
                val dir = File("$filesDir/Book")
                if (!dir.exists()) dir.mkdir()
                try {
                    val filePath = "$filesDir/Book/$file"
                    val inputStream = contentResolver.openInputStream(data)
                    val buffer = ByteArray(8192)
                    var count: Int
                    if (inputStream != null) {
                        FileOutputStream(filePath).use { fout -> while (inputStream.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count) }
                    }
                    intent1.putExtra("filePath", filePath)
                    inputStream?.close()
                } catch (_: Throwable) {
                }
            }
        }
        val gson = Gson()
        val file = File("$filesDir/BibliaSemuxaNatatki.json")
        if (file.exists() && BibleGlobalList.natatkiSemuxa.size == 0) {
            try {
                val type = TypeToken.getParameterized(ArrayList::class.java, BibleNatatkiData::class.java).type
                BibleGlobalList.natatkiSemuxa.addAll(gson.fromJson(file.readText(), type))
            } catch (_: Throwable) {
            }
        }
        val file2 = File("$filesDir/BibliaSemuxaZakladki.json")
        if (file2.exists() && BibleGlobalList.zakladkiSemuxa.size == 0) {
            try {
                val type = TypeToken.getParameterized(ArrayList::class.java, BibleZakladkiData::class.java).type
                BibleGlobalList.zakladkiSemuxa.addAll(gson.fromJson(file2.readText(), type))
            } catch (_: Throwable) {
            }
        }
        val file3 = File("$filesDir/BibliaSinodalNatatki.json")
        if (file3.exists() && BibleGlobalList.natatkiSinodal.size == 0) {
            try {
                val type = TypeToken.getParameterized(ArrayList::class.java, BibleNatatkiData::class.java).type
                BibleGlobalList.natatkiSinodal.addAll(gson.fromJson(file3.readText(), type))
            } catch (_: Throwable) {
            }
        }
        val file4 = File("$filesDir/BibliaSinodalZakladki.json")
        if (file4.exists() && BibleGlobalList.zakladkiSinodal.size == 0) {
            try {
                val type = TypeToken.getParameterized(ArrayList::class.java, BibleZakladkiData::class.java).type
                BibleGlobalList.zakladkiSinodal.addAll(gson.fromJson(file4.readText(), type))
            } catch (_: Throwable) {
            }
        }
        startActivity(intent1)
        onBack()
    }
}