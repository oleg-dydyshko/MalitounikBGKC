package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity.Companion.toastView
import by.carkva_gazeta.malitounik.databinding.ContentPsalterBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemArtykulyBinding
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File


class BibliatekaArtykulyList : BaseActivity(), AdapterView.OnItemClickListener {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<LinkedTreeMap<String, String>>()
    private var position = 1
    private lateinit var binding: ContentPsalterBinding
    private var resetTollbarJob: Job? = null
    private lateinit var listAdapter: ArrayAdapter<LinkedTreeMap<String, String>>

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentPsalterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        position = intent.extras?.getInt("position") ?: 1
        val path = when (position) {
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
        if (MainActivity.isNetworkAvailable(true)) {
            if (!localFile.exists()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                        toastView(this@BibliatekaArtykulyList, getString(R.string.error))
                    }.await()
                    load(localFile)
                }
            } else {
                load(localFile)
            }
        } else if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                    toastView(this@BibliatekaArtykulyList, getString(R.string.error))
                }.await()
                load(localFile)
            }
        } else {
            if (localFile.exists()) {
                load(localFile)
            }
        }
        binding.listView.onItemClickListener = this
        binding.listView.isVerticalScrollBarEnabled = false
        val dzenNoch = (this as BaseActivity).getBaseDzenNoch()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.listView.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.listView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getStringArray(R.array.artykuly)[position]
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                }
            }
        }
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

    private fun load(localFile: File) {
        val gson = Gson()
        val text = localFile.readText()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
        data.addAll(gson.fromJson(text, type))
        listAdapter = MenuListAdaprer(this)
        binding.listView.adapter = listAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val intent = Intent(this, BibliatekaArtykuly::class.java)
        intent.putExtra("rubrika", this.position)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    private inner class MenuListAdaprer(private val context: Activity) : ArrayAdapter<LinkedTreeMap<String, String>>(context, R.layout.simple_list_item_2, R.id.label, data) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItemArtykulyBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label, binding.image)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val ssb = SpannableStringBuilder()
            val dataArt = SpannableString(data[position]["data"] ?: "")
            dataArt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), 0, dataArt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataArt.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt() - 2, true), 0, dataArt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.append(dataArt)
            val link = MainActivity.fromHtml(data[position]["link"] ?: "")
            ssb.append("\n")
            ssb.append(link)
            viewHolder.text.text = ssb
            if (data[position]["img_cache"]?.isEmpty() == true) {
                viewHolder.image.visibility = View.GONE
            } else {
                Picasso.get().load(data[position]["img_cache"]).into(viewHolder.image)
            }
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var image: ImageView)
}