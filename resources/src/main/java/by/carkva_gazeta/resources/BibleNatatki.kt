package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.DialogBibleNatatkaEdit.BibleNatatkaEditlistiner
import by.carkva_gazeta.resources.DialogDeliteAllZakladkiINatatki.DialogDeliteAllZakladkiINatatkiListener
import by.carkva_gazeta.resources.DialogZakladkaDelite.ZakladkaDeliteListiner
import com.google.gson.Gson
import kotlinx.android.synthetic.main.akafist_list_bible.*
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class BibleNatatki : AppCompatActivity(), OnItemClickListener, OnItemLongClickListener, ZakladkaDeliteListiner, DialogDeliteAllZakladkiINatatkiListener, BibleNatatkaEditlistiner, DialogContextMenu.DialogContextMenuListener {
    private lateinit var data: ArrayList<ArrayList<String>>
    private lateinit var adapter: ListAdaprer
    private var semuxa = 1
    private var dzenNoch = false
    private var mLastClickTime: Long = 0

    override fun setEdit() {
        adapter.notifyDataSetChanged()
    }

    override fun onDialogEditClick(position: Int) {
        val natatka = DialogBibleNatatkaEdit.getInstance(semuxa, position)
        natatka.show(supportFragmentManager, "bible_natatka_edit")
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val delite = DialogZakladkaDelite.getInstance(position, name, semuxa, false)
        delite.show(supportFragmentManager, "zakladka_delite")
    }

    override fun fileAllNatatkiAlboZakladki(semuxa: Int) {
        if (semuxa == 1) {
            MaranAtaGlobalList.natatkiSemuxa?.removeAll(MaranAtaGlobalList.natatkiSemuxa
                    ?: ArrayList())
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        }
        if (semuxa == 2) {
            MaranAtaGlobalList.natatkiSinodal?.removeAll(MaranAtaGlobalList.natatkiSinodal
                    ?: ArrayList())
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        }
        /*if (semuxa == 3) {
            MaranAta_Global_List.getNatatkiPsalterNadsana().removeAll(MaranAta_Global_List.getNatatkiPsalterNadsana());
            adapter.notifyDataSetChanged();
            File fileNatatki = new File(getFilesDir() + "/PsalterNadsanNatatki.json");
            if (fileNatatki.exists()) {
                fileNatatki.delete();
            }
        }*/
        help.visibility = View.VISIBLE
        ListView.visibility = View.GONE
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        setContentView(R.layout.akafist_list_bible)
        if (intent != null) {
            semuxa = intent.getIntExtra("semuxa", 1)
        }
        if (semuxa == 1) data = MaranAtaGlobalList.natatkiSemuxa ?: ArrayList()
        if (semuxa == 2) data = MaranAtaGlobalList.natatkiSinodal ?: ArrayList()
        //if (semuxa == 3)
//    data = MaranAta_Global_List.getNatatkiPsalterNadsana();
        adapter = ListAdaprer(this, data)
        if (data.size == 0) {
            help.visibility = View.VISIBLE
            ListView.visibility = View.GONE
        }
        if (dzenNoch) help.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        ListView.adapter = adapter
        ListView.isVerticalScrollBarEnabled = false
        ListView.onItemClickListener = this
        ListView.onItemLongClickListener = this
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setText(by.carkva_gazeta.malitounik.R.string.natatki_biblii)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.trash).isVisible = data.size != 0
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.zakladki_i_natatki, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.trash) {
            val natatki = DialogDeliteAllZakladkiINatatki.getInstance(resources.getString(by.carkva_gazeta.malitounik.R.string.natatki_biblii).toLowerCase(Locale.getDefault()), semuxa)
            natatki.show(supportFragmentManager, "delite_all_zakladki_i_natatki")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun zakladkadiliteItem(position: Int, semuxa: Int) {}
    override fun natatkidiliteItem(position: Int, semuxa: Int) {
        if (semuxa == 1) {
            MaranAtaGlobalList.natatkiSemuxa?.removeAt(position)
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
            if (MaranAtaGlobalList.natatkiSemuxa?.size == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete()
                }
                help.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            } else {
                if (MaranAtaGlobalList.natatkiSemuxa != null) {
                    val gson = Gson()
                    val outputStream = FileWriter(fileNatatki)
                    outputStream.write(gson.toJson(MaranAtaGlobalList.natatkiSemuxa))
                    outputStream.close()
                }
            }
        }
        if (semuxa == 2) {
            MaranAtaGlobalList.natatkiSinodal?.removeAt(position)
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
            if (MaranAtaGlobalList.natatkiSinodal?.size == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete()
                }
                help.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            } else {
                if (MaranAtaGlobalList.natatkiSinodal != null) {
                    val gson = Gson()
                    val outputStream = FileWriter(fileNatatki)
                    outputStream.write(gson.toJson(MaranAtaGlobalList.natatkiSinodal))
                    outputStream.close()
                }
            }
        }
        /*if (semuxa == 3) {
            MaranAta_Global_List.getNatatkiPsalterNadsana().remove(position);
            adapter.notifyDataSetChanged();
            File fileNatatki = new File(getFilesDir() + "/PsalterNadsanNatatki.json");
            if (MaranAta_Global_List.getNatatkiPsalterNadsana().size() == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete();
                }
                help.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                Gson gson = new Gson();
                try {
                    FileWriter outputStream = new FileWriter(fileNatatki);
                    outputStream.write(gson.toJson(MaranAta_Global_List.getNatatkiPsalterNadsana()));
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }*/invalidateOptionsMenu()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        var kniga = -1
        var knigaS = -1
        if (data[position][0].contains("1")) kniga = data[position][1].toInt() else knigaS = data[position][1].toInt()
        var intent = Intent()
        /*if (semuxa == 3) {
            intent = new Intent(this, nadsanContentActivity.class);
        } else {*/if (kniga != -1) {
            if (semuxa == 1) {
                intent = Intent(this, NovyZapaviet3::class.java)
            }
            if (semuxa == 2) {
                intent = Intent(this, NovyZapavietSinaidal3::class.java)
            }
            intent.putExtra("kniga", kniga)
        }
        if (knigaS != -1) {
            if (semuxa == 1) {
                intent = Intent(this, StaryZapaviet3::class.java)
                when (knigaS) {
                    19 -> knigaS = 16
                    20 -> knigaS = 17
                    21 -> knigaS = 18
                    22 -> knigaS = 19
                    23 -> knigaS = 20
                    24 -> knigaS = 21
                    27 -> knigaS = 22
                    28 -> knigaS = 23
                    29 -> knigaS = 24
                    32 -> knigaS = 25
                    33 -> knigaS = 26
                    34 -> knigaS = 27
                    35 -> knigaS = 28
                    36 -> knigaS = 29
                    37 -> knigaS = 30
                    38 -> knigaS = 31
                    39 -> knigaS = 32
                    40 -> knigaS = 33
                    41 -> knigaS = 34
                    42 -> knigaS = 35
                    43 -> knigaS = 36
                    44 -> knigaS = 37
                    45 -> knigaS = 38
                }
            }
            if (semuxa == 2) {
                intent = Intent(this, StaryZapavietSinaidal3::class.java)
            }
            intent.putExtra("kniga", knigaS)
        }
        //}
        intent.putExtra("glava", Integer.valueOf(data[position][2]))
        intent.putExtra("stix", Integer.valueOf(data[position][3]))
        startActivityForResult(intent, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 500) {
            if (data.size == 0) {
                help.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        val contextMenu = DialogContextMenu.getInstance(position, data[position][5])
        contextMenu.show(supportFragmentManager, "context_menu")
        return true
    }

    private inner class ListAdaprer internal constructor(private val mContext: Activity, private val itemsL: ArrayList<ArrayList<String>>) : ArrayAdapter<ArrayList<String>?>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_3, by.carkva_gazeta.malitounik.R.id.label, itemsL as List<ArrayList<String>>) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        override fun add(string: ArrayList<String>?) {
            super.add(string)
            itemsL.add(string ?: ArrayList())
        }

        override fun remove(string: ArrayList<String>?) {
            super.remove(string)
            itemsL.remove(string)
        }

        override fun clear() {
            super.clear()
            itemsL.clear()
        }

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_3, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
                viewHolder.buttonPopup = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.button_popup)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.buttonPopup?.setOnClickListener { showPopupMenu(viewHolder.buttonPopup, position, itemsL[position][5]) }
            viewHolder.text?.text = itemsL[position][4] + "\n\n" + itemsL[position][5]
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, by.carkva_gazeta.malitounik.R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }

    }

    private fun showPopupMenu(view: View?, position: Int, name: String) {
        val popup = PopupMenu(this, view)
        val infl = popup.menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.popup, popup.menu)
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            val spanString = SpannableString(popup.menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            popup.dismiss()
            when (menuItem.itemId) {
                by.carkva_gazeta.malitounik.R.id.menu_redoktor -> {
                    val natatka = DialogBibleNatatkaEdit.getInstance(semuxa, position)
                    natatka.show(supportFragmentManager, "bible_natatka_edit")
                    return@setOnMenuItemClickListener true
                }
                by.carkva_gazeta.malitounik.R.id.menu_remove -> {
                    val delite = DialogZakladkaDelite.getInstance(position, name, semuxa, false)
                    delite.show(supportFragmentManager, "zakladka_delite")
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        popup.show()
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
        var buttonPopup: ImageView? = null
    }
}