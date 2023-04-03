package by.carkva_gazeta.admin

import android.content.Context
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminArtykulyBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File

class Artykly : BaseActivity(), View.OnClickListener {
    private lateinit var binding: AdminArtykulyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val artykli = ArrayList<LinkedTreeMap<String, String>>()
    private var edittext: AppCompatEditText? = null
    private var path = "history.json"
    private var position = 0

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminArtykulyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        path = when (intent.extras?.getInt("rybrika") ?: 1) {
            0 -> "svietlo_uschodu.json"
            1 -> "history.json"
            2 -> "gramadstva.json"
            3 -> "videa.json"
            4 -> "adkaz.json"
            5 -> "naviny2022.json"
            6 -> "naviny2021.json"
            7 -> "naviny2020.json"
            8 -> "naviny2019.json"
            9 -> "naviny2018.json"
            10 -> "naviny2017.json"
            11 -> "naviny2016.json"
            12 -> "naviny2015.json"
            13 -> "naviny2014.json"
            14 -> "naviny2013.json"
            15 -> "naviny2012.json"
            16 -> "naviny2011.json"
            17 -> "naviny2010.json"
            18 -> "naviny2009.json"
            19 -> "naviny2008.json"
            20 -> "abvestki.json"
            else -> "history.json"
        }
        val localFile = File("$filesDir/$path")
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.sviaty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) edittext = v as? AppCompatEditText
        }
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            try {
                Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                    MainActivity.toastView(this@Artykly, getString(by.carkva_gazeta.malitounik.R.string.error))
                }.await()
                load(localFile)
            } catch (e: Throwable) {
                MainActivity.toastView(this@Artykly, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
            }
            binding.progressBar2.visibility = View.GONE
        }
        setTollbarTheme()
    }

    private fun load(localFile: File) {
        val gson = Gson()
        val text = localFile.readText()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
        artykli.addAll(gson.fromJson(text, type))
        position = intent.extras?.getInt("position") ?: 0
        binding.sviaty.setText(artykli[position]["str"] ?: "")
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.artykuly)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        edittext?.let {
            if (id == R.id.action_bold) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<strong>")
                    append(text.substring(startSelect, endSelect))
                    append("</strong>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 17)
            }
            if (id == R.id.action_em) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<em>")
                    append(text.substring(startSelect, endSelect))
                    append("</em>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 9)
            }
            if (id == R.id.action_red) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<font color=\"#d00505\">")
                    append(text.substring(startSelect, endSelect))
                    append("</font>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 29)
            }
            if (id == R.id.action_p) {
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, endSelect))
                    append("<p>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 3)
            }
        }
    }

    override fun onBack() {
        if (binding.preView.visibility == View.VISIBLE) {
            binding.preView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        } else {
            super.onBack()
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val editItem = menu.findItem(R.id.action_preview)
        if (binding.preView.visibility == View.GONE) {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
        } else {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka)
        }
        menu.findItem(R.id.action_upload_image).isVisible = false
    }

    private fun sendPostRequest() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            artykli[position]["str"] = binding.sviaty.text.toString()
            val gson = Gson()
            val localFile = File("$filesDir/$path")
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
            localFile.writer().use {
                it.write(gson.toJson(artykli, type))
            }
            Malitounik.referens.child("/$path").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    MainActivity.toastView(this@Artykly, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(this@Artykly, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
            }.await()
            binding.progressBar2.visibility = View.GONE
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            sendPostRequest()
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.preView.visibility == View.VISIBLE) {
                binding.preView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            } else {
                val webSettings = binding.preView.settings
                webSettings.standardFontFamily = "sans-serif-condensed"
                webSettings.defaultFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT.toInt()
                webSettings.domStorageEnabled = true
                binding.preView.loadDataWithBaseURL(null, binding.sviaty.text.toString(), "text/html", "utf-8", null)
                binding.preView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.sviaty.windowToken, 0)
                invalidateOptionsMenu()
            }
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviaty, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }
}