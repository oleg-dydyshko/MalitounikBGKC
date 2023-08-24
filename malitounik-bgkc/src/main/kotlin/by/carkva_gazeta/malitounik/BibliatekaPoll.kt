package by.carkva_gazeta.malitounik

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import by.carkva_gazeta.malitounik.databinding.BibliatekaPollBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


class BibliatekaPoll : BaseActivity() {
    private lateinit var binding: BibliatekaPollBinding

    private suspend fun load() {
        binding.progressBar.visibility = View.VISIBLE
        try {
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(Any::class.java).type).type
            for (rubrika in 0..5) {
                val path = when (rubrika) {
                    0 -> "carkvapoll6.json"
                    1 -> "carkvapoll5.json"
                    2 -> "carkvapoll4.json"
                    3 -> "carkvapoll3.json"
                    4 -> "carkvapoll2.json"
                    5 -> "carkvapoll1.json"
                    else -> "carkvapoll1.json"
                }
                val localFile = File("$filesDir/Artykuly/$path")
                if (!localFile.exists()) {
                    if (MainActivity.isNetworkAvailable() && !MainActivity.isNetworkAvailable(true)) {
                        BaseActivity.referens.child("/$path").getFile(localFile).addOnFailureListener {
                            MainActivity.toastView(this@BibliatekaPoll, getString(R.string.error))
                        }.await()
                    }
                }
                if (localFile.exists()) {
                    val text = localFile.readText()
                    if (text == "") localFile.delete()
                    val arrayList = gson.fromJson<ArrayList<Any>>(text, type)
                    var count = 0
                    for (i in 1 until arrayList.size) {
                        val arrayList2 = arrayList[i] as ArrayList<*>
                        count += arrayList2[1].toString().toInt()
                    }
                    for (i in 0 until arrayList.size) {
                        if (i == 0) {
                            val textView = TextView(this)
                            textView.gravity = Gravity.CENTER
                            textView.textSize = SettingsActivity.GET_FONT_SIZE_MIN
                            textView.setTypeface(null, Typeface.BOLD)
                            textView.text = arrayList[0].toString()
                            textView.setPadding(0, 0, 0, (10 * resources.displayMetrics.density).toInt())
                            binding.linearLayout.addView(textView)
                        } else {
                            val textView2 = TextView(this)
                            textView2.gravity = Gravity.START
                            textView2.textSize = SettingsActivity.GET_FONT_SIZE_MIN
                            textView2.setTypeface(null, Typeface.NORMAL)
                            val arrayList2 = arrayList[i] as ArrayList<*>
                            val spannableStrind = SpannableString(arrayList2[0].toString()+ ": " + arrayList2[1].toString() + " (" + (arrayList2[1].toString().toInt() * 100 / count) + "%)")
                            val t1 = spannableStrind.lastIndexOf(":")
                            spannableStrind.setSpan(StyleSpan(Typeface.BOLD), t1, spannableStrind.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            textView2.text = spannableStrind
                            binding.linearLayout.addView(textView2)
                            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
                            progressBar.max = count
                            progressBar.progress = arrayList2[1].toString().toInt()
                            binding.linearLayout.addView(progressBar)
                        }
                    }
                    val textView = TextView(this)
                    textView.gravity = Gravity.CENTER
                    textView.textSize = SettingsActivity.GET_FONT_SIZE_MIN
                    textView.setTypeface(null, Typeface.BOLD)
                    textView.text = getString(R.string.artykuly_apytanki_count, count)
                    textView.setPadding(0, 0, 0, (20 * resources.displayMetrics.density).toInt())
                    binding.linearLayout.addView(textView)
                }
            }
        } catch (e: Throwable) {
            MainActivity.toastView(this, getString(R.string.error_ch2))
        }
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BibliatekaPollBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dir = File("$filesDir/Artykuly")
        if (!dir.exists()) dir.mkdir()
        CoroutineScope(Dispatchers.Main).launch {
            load()
        }
        val dzenNoch = (this as BaseActivity).getBaseDzenNoch()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getStringArray(R.array.artykuly)[21]
    }
}