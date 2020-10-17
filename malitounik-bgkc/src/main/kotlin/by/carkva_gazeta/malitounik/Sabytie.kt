package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.carkva_gazeta.malitounik.DialogContextMenuSabytie.DialogContextMenuSabytieListener
import by.carkva_gazeta.malitounik.DialogDelite.DialogDeliteListener
import by.carkva_gazeta.malitounik.DialogSabytieSave.DialogSabytieSaveListener
import com.google.gson.Gson
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.android.synthetic.main.sabytie.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 8.5.17
 */
class Sabytie : AppCompatActivity(), DialogSabytieSaveListener, DialogContextMenuSabytieListener, DialogDeliteListener, DialogSabytieDelite.DialogSabytieDeliteListener, DialogSabytieTime.DialogSabytieTimeListener {
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var konec = false
    private var back = false
    private var home = false
    private var redak = false
    private var save = false
    private lateinit var adapter: SabytieAdapter
    private val sabytie2 = ArrayList<SabytieDataAdapter>()
    private lateinit var c: GregorianCalendar
    private var timeH = 0
    private var timeM = 0
    private var posit = 0
    private var nomer = 0
    private var yearG = 0
    private var munG = 0
    private var idMenu = 1
    private var repitL = 0
    private var radio = 1
    private var color = 0
    private var vybtimeSave = 0
    private var repitSave = 0
    private var colorSave = 0
    private var ta = ""
    private var da = ""
    private var taK = ""
    private var daK = ""
    private var time = "0"
    private var editSave = ""
    private var edit2Save = ""
    private var daSave = ""
    private var taSave = ""
    private var daKSave = ""
    private var taKSave = ""
    private var editText4Save = ""
    private var labelbutton12Save = ""
    private var radioSave = 0
    private var result: Long = 0
    private lateinit var am: AlarmManager
    private var menu: Menu? = null
    private var mLastClickTime: Long = 0
    private lateinit var colorAdapter: ColorAdapter
    private var nazvaPadzei = "Назва падзеі"

    override fun sabytieTimePositive(nomerDialoga: Int, hour: Int, minute: Int) {
        if (nomerDialoga == 1) {
            da = label1.text.toString()
            daK = label12.text.toString()
            taK = label22.text.toString()
            c = Calendar.getInstance() as GregorianCalendar
            c.timeInMillis = result
            timeH = hour
            timeM = minute
            c[c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH], timeH, timeM] = 0
            result = c.timeInMillis
            var tr = ""
            if (timeM < 10) tr = "0"
            ta = "$timeH:$tr$timeM"
            val date = da.split(".")
            var gc12 = GregorianCalendar(date[2].toInt(), date[1].toInt() - 1, date[0].toInt(), 0, 0, 0)
            val dateK = daK.split(".")
            val timeK = taK.split(":")
            var gcK = GregorianCalendar(dateK[2].toInt(), dateK[1].toInt() - 1, dateK[0].toInt(), 0, 0, 0)
            if (gc12.timeInMillis == gcK.timeInMillis) {
                gc12 = GregorianCalendar(date[2].toInt(), date[1].toInt() - 1, date[0].toInt(), timeH, timeM, 0)
                gcK = GregorianCalendar(dateK[2].toInt(), dateK[1].toInt() - 1, dateK[0].toInt(), timeK[0].toInt(), timeK[1].toInt(), 0)
                if (gc12.timeInMillis > gcK.timeInMillis) {
                    label22.text = ta
                }
            }
            label2.text = ta
            val temp = editText2.text
            editText2.setText("")
            editText2.text = temp
        }
        if (nomerDialoga == 2) {
            var tr = ""
            if (minute < 10) tr = "0"
            taK = "$hour:$tr$minute"
            label22.text = taK
            val days = label1.text.toString().split(".")
            val gc1 = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
            val days2 = label12.text.toString().split(".")
            val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
            val kon = gc2[Calendar.DAY_OF_YEAR]
            val res = gc1[Calendar.DAY_OF_YEAR]
            if (kon - res == 0) {
                val times = label2.text.toString().split(":")
                val times2 = label22.text.toString().split(":")
                val gc3 = GregorianCalendar(gc2[Calendar.YEAR], gc2[Calendar.MONTH], gc2[Calendar.DAY_OF_MONTH], times[0].toInt(), times[1].toInt(), 0)
                val gc4 = GregorianCalendar(gc2[Calendar.YEAR], gc2[Calendar.MONTH], gc2[Calendar.DAY_OF_MONTH], times2[0].toInt(), times2[1].toInt(), 0)
                if (gc4.timeInMillis - gc3.timeInMillis < 1000) {
                    gc2.add(Calendar.DATE, 1)
                    var nol112 = ""
                    var nol212 = ""
                    if (gc2[Calendar.DAY_OF_MONTH] < 10) nol112 = "0"
                    if (gc2[Calendar.MONTH] < 9) nol212 = "0"
                    val da1 = nol112 + gc2[Calendar.DAY_OF_MONTH] + "." + nol212 + (gc2[Calendar.MONTH] + 1) + "." + gc2[Calendar.YEAR]
                    label12.text = da1
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) {
            setTheme(R.style.AppCompatDark)
            colors[0] = "#f44336"
        }
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sabytie)
        labelbutton12.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val w = labelbutton12.text.toString().split(".")
            val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
            yearG = gc[Calendar.YEAR]
            munG = gc[Calendar.MONTH]
            val i = Intent(this@Sabytie, CaliandarMun::class.java)
            i.putExtra("day", gc[Calendar.DATE])
            i.putExtra("year", yearG)
            i.putExtra("mun", munG)
            i.putExtra("sabytie", true)
            startActivityForResult(i, 1093)
        })
        radioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButton1 -> {
                    radio = 1
                    radioButton2a.visibility = View.GONE
                    labelbutton12.visibility = View.GONE
                }
                R.id.radioButton2 -> {
                    radio = 2
                    radioButton2a.visibility = View.VISIBLE
                    labelbutton12.visibility = View.GONE
                }
                R.id.radioButton3 -> {
                    if (idMenu != 3) {
                        val w = labelbutton12.text.toString().split(".")
                        val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
                        yearG = gc[Calendar.YEAR]
                        munG = gc[Calendar.MONTH]
                        val i = Intent(this@Sabytie, CaliandarMun::class.java)
                        i.putExtra("day", gc[Calendar.DATE])
                        i.putExtra("year", yearG)
                        i.putExtra("mun", munG)
                        i.putExtra("sabytie", true)
                        startActivityForResult(i, 1093)
                    }
                    radio = 3
                    radioButton2a.visibility = View.GONE
                    labelbutton12.visibility = View.VISIBLE
                }
            }
        }
        am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        c = Calendar.getInstance() as GregorianCalendar
        c.add(Calendar.DATE, 1)
        var nol1 = ""
        var nol2 = ""
        if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c[Calendar.MONTH] < 9) nol2 = "0"
        labelbutton12.text = resources.getString(R.string.Sabytie, nol1, c[Calendar.DAY_OF_MONTH], nol2, c[Calendar.MONTH] + 1, c[Calendar.YEAR])
        c.add(Calendar.DATE, -1)
        result = c.timeInMillis
        c.add(Calendar.HOUR_OF_DAY, 1)
        timeH = c[Calendar.HOUR_OF_DAY]
        timeM = 0
        c[c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH], timeH, timeM] = 0
        nol1 = ""
        nol2 = ""
        if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c[Calendar.MONTH] < 9) nol2 = "0"
        yearG = c[Calendar.YEAR]
        munG = c[Calendar.MONTH]
        da = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
        ta = "$timeH:00"
        color = 0
        label1.text = da
        val notifi = arrayOf("хвілінаў", "часоў", "дзён", "тыдняў")
        val adapter2 = SpinnerAdapter(this, notifi)
        spinner3.adapter = adapter2
        spinner3.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                posit = position
                val temp = editText2.text
                editText2.setText("")
                editText2.text = temp
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val repit = arrayOf("Няма", "Кожны дзень", "Па будных днях", "Два дні праз два", "Кожны тыдзень", "Кожныя два тыдні", "Кожныя чатыры тыдні", "Кожны месяц", "Раз на год")
        val adapter3 = SpinnerAdapter(this, repit)
        spinner4.adapter = adapter3
        spinner4.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(spinner4.windowToken, 0)
                repitL = position
                if (repitL == 7) {
                    radioButton3.isClickable = false
                    radioButton3.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorSecondary_text))
                } else {
                    radioButton3.isClickable = true
                    if (dzenNoch) radioButton3.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons)) else radioButton3.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_text))
                }
                if (repitL == 0) radioButton1.isChecked = true
                if (repitL > 0) {
                    radioGroup.visibility = View.VISIBLE
                } else {
                    radioGroup.visibility = View.GONE
                    radioButton2a.visibility = View.GONE
                    labelbutton12.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        colorAdapter = ColorAdapter(this)
        spinner5.adapter = colorAdapter
        spinner5.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                color = position
                spinner5.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner5.setSelection(0)
        label1.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val w = label1.text.toString().split(".")
            val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
            yearG = gc[Calendar.YEAR]
            munG = gc[Calendar.MONTH]
            val i = Intent(this@Sabytie, CaliandarMun::class.java)
            i.putExtra("day", gc[Calendar.DATE])
            i.putExtra("year", yearG)
            i.putExtra("mun", munG)
            i.putExtra("sabytie", true)
            startActivityForResult(i, 109)
        }
        label2.text = ta
        label2.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val dialogSabytieTime = DialogSabytieTime.getInstance(1, label2.text.toString())
            dialogSabytieTime.show(supportFragmentManager, "dialogSabytieTime")
        }
        label12.text = da
        label12.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val w = label12.text.toString().split(".")
            val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
            yearG = gc[Calendar.YEAR]
            munG = gc[Calendar.MONTH]
            val i = Intent(this@Sabytie, CaliandarMun::class.java)
            i.putExtra("day", gc[Calendar.DATE])
            i.putExtra("year", yearG)
            i.putExtra("mun", munG)
            i.putExtra("sabytie", true)
            startActivityForResult(i, 1092)
        }
        label22.text = ta
        label22.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val dialogSabytieTime = DialogSabytieTime.getInstance(2, label22.text.toString())
            dialogSabytieTime.show(supportFragmentManager, "dialogSabytieTime")
        }
        checkBox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                linearKonec.visibility = View.GONE
                konec = false
            } else {
                linearKonec.visibility = View.VISIBLE
                konec = true
            }
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
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
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        title_toolbar.text = resources.getString(R.string.sabytie)
        if (dzenNoch) {
            pacatak.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            kanec.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            pavedamic.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            pavtor.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            cvet.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            pazov.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label1.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label2.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label12.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label22.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            checkBox2.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label1.setBackgroundResource(R.drawable.selector_dark)
            label2.setBackgroundResource(R.drawable.selector_dark)
            label12.setBackgroundResource(R.drawable.selector_dark)
            label22.setBackgroundResource(R.drawable.selector_dark)
            checkBox2.setBackgroundResource(R.drawable.selector_dark)
            labelbutton12.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            labelbutton12.setBackgroundResource(R.drawable.selector_dark)
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
        MainActivity.padzeia.sort()
        for (i in 0 until MainActivity.padzeia.size) {
            sabytie2.add(SabytieDataAdapter(i.toLong(), MainActivity.padzeia[i].dat + " " + MainActivity.padzeia[i].padz, MainActivity.padzeia[i].color))
        }
        adapter = SabytieAdapter(sabytie2, R.layout.list_item_sabytie, R.id.image, false)
        drag_list_view.recyclerView.isVerticalScrollBarEnabled = false
        drag_list_view.setLayoutManager(LinearLayoutManager(this))
        drag_list_view.setAdapter(adapter, false)
        drag_list_view.setCanDragHorizontally(false)
        drag_list_view.setCanDragVertically(true)
        drag_list_view.setCustomDragItem(MyDragItem(this, R.layout.list_item_sabytie))
        drag_list_view.setCanDragVertically(false)
        drag_list_view.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                val adapterItem = item.tag as SabytieDataAdapter
                val pos: Int = drag_list_view.adapter.getPositionForItem(adapterItem)
                if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    onDialogDeliteClick(pos)
                }
                if (swipedDirection == ListSwipeItem.SwipeDirection.RIGHT) {
                    onSabytieRedaktor(pos)
                }
            }
        })
        if (savedInstanceState != null) {
            redak = savedInstanceState.getBoolean("redak")
            back = savedInstanceState.getBoolean("back")
            save = savedInstanceState.getBoolean("save")
            idMenu = savedInstanceState.getInt("idMenu")
            if (savedInstanceState.getBoolean("titleLayout")) {
                titleLayout.visibility = View.VISIBLE
                drag_list_view.visibility = View.GONE
                invalidateOptionsMenu()
            }
            ta = savedInstanceState.getString("ta") ?: "0:0"
            da = savedInstanceState.getString("da") ?: "0.0.0"
            taK = savedInstanceState.getString("taK") ?: "0:0"
            daK = savedInstanceState.getString("daK") ?: "0.0.0"
            label1.text = da
            label2.text = ta
            label12.text = daK
            label22.text = taK
        }
        editText.addTextChangedListener(MyTextWatcher(editText))
        editText2.addTextChangedListener(MyTextWatcher(editText2))
        editSave = editText.text.toString().trim()
        edit2Save = editText2.text.toString()
        daSave = label1.text.toString()
        taSave = label2.text.toString()
        daKSave = label12.text.toString()
        taKSave = label22.text.toString()
        labelbutton12Save = labelbutton12.text.toString()
        editText4Save = editText4.text.toString()
        colorSave = spinner5.selectedItemPosition
        radioSave = radio
        if (intent.extras?.getBoolean("shortcuts", false) == true) {
            addSabytie()
        }
        if (intent.extras?.getBoolean("edit", false) == true) {
            val position = intent.extras?.getInt("position")?: 0
            onSabytieRedaktor(position)
            editCaliandar = true
        }
    }

    override fun onDialogEditClick(position: Int) {
        save = true
        back = true
        val p = MainActivity.padzeia[position]
        editText.setText(p.padz)
        label1.text = p.dat
        label2.text = p.tim
        label12.text = p.datK
        label22.text = p.timK
        if (p.sec == "-1") editText2.setText("") else editText2.setText(p.sec)
        spinner3.setSelection(p.vybtime)
        spinner4.setSelection(p.repit)
        spinner5.setSelection(p.color)
        labelbutton12Save = labelbutton12.text.toString()
        editText4Save = editText4.text.toString()
        radioSave = radio
        vybtimeSave = p.vybtime
        repitSave = p.repit
        colorSave = p.color
        color = p.color
        if (p.repit > 0) radioGroup.visibility = View.VISIBLE else radioGroup.visibility = View.GONE
        nomer = position
        titleLayout.visibility = View.VISIBLE
        drag_list_view.visibility = View.GONE
        idMenu = 3
        time = p.count
        val count = time.split(".")
        when {
            time == "0" -> radioButton1.isChecked = true
            count.size == 1 -> {
                radioButton2.isChecked = true
                editText4.setText(time)
            }
            else -> {
                radioButton3.isChecked = true
                labelbutton12.text = time
            }
        }
        repitL = p.repit
        editSave = editText.text.toString().trim()
        edit2Save = editText2.text.toString()
        daSave = label1.text.toString()
        taSave = label2.text.toString()
        daKSave = label12.text.toString()
        taKSave = label22.text.toString()
        invalidateOptionsMenu()
    }

    override fun fileDeliteCancel() {
        drag_list_view.resetSwipedViews(null)
    }

    override fun fileDelite(position: Int, file: String) {
        redak = true
        val sab = MainActivity.padzeia[position]
        val filen = sab.padz
        val del = ArrayList<Padzeia>()
        for (p in MainActivity.padzeia) {
            if (p.padz == filen) {
                del.add(p)
            }
        }
        MainActivity.padzeia.removeAll(del)
        val outputStream = FileWriter("$filesDir/Sabytie.json")
        val gson = Gson()
        outputStream.write(gson.toJson(MainActivity.padzeia))
        outputStream.close()
        MainActivity.padzeia.sort()
        sabytie2.clear()
        for (i in 0 until MainActivity.padzeia.size) {
            sabytie2.add(SabytieDataAdapter(i.toLong(), MainActivity.padzeia[i].dat + " " + MainActivity.padzeia[i].padz, MainActivity.padzeia[i].color))
        }
        adapter.notifyDataSetChanged()
        CoroutineScope(Dispatchers.IO).launch {
            if (sab.count == "0") {
                if (sab.repit == 1 || sab.repit == 4 || sab.repit == 5 || sab.repit == 6) {
                    if (sab.sec != "-1") {
                        val intent = createIntent(sab.padz, "Падзея" + " " + sab.dat + " у " + sab.tim, sab.dat, sab.tim)
                        val londs3 = sab.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                } else {
                        for (p in del) {
                            if (p.padz.contains(filen)) {
                                if (p.sec != "-1") {
                                    val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                                    val londs3 = p.paznic / 100000L
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                    am.cancel(pIntent)
                                    pIntent.cancel()
                                }
                            }
                        }
                    }
                } else {
                    for (p in del) {
                        if (p.sec != "-1") {
                            val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                            val londs3 = p.paznic / 100000L
                            val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                            am.cancel(pIntent)
                            pIntent.cancel()
                        }
                    }
                }
        }
        MainActivity.toastView(this@Sabytie, getString(R.string.remove_padzea))
    }

    override fun onDialogDeliteClick(position: Int) {
        val dd = DialogDelite.getInstance(position, "", "з падзей", sabytie2[position].title)
        dd.show(supportFragmentManager, "dialig_delite")
    }

    override fun onBackPressed() {
        val editSaveN = editText.text.toString().trim()
        val edit2SaveN = editText2.text.toString()
        val edit4SaveN = editText4.text.toString()
        val daSaveN = label1.text.toString()
        val taSaveN = label2.text.toString()
        val daKSaveN = label12.text.toString()
        val taKSaveN = label22.text.toString()
        if (editCaliandar) {
            onSupportNavigateUp()
        } else if (!(edit2SaveN == edit2Save && editSaveN == editSave && daSaveN == daSave && daKSaveN == daKSave && taSaveN == taSave && taKSaveN == taKSave && spinner3.selectedItemPosition == vybtimeSave && spinner4.selectedItemPosition == repitSave && spinner5.selectedItemPosition == colorSave && labelbutton12Save == labelbutton12.text.toString() && editText4Save == edit4SaveN && radioSave == radio) && drag_list_view.visibility == View.GONE) {
            val dialogSabytieSave = DialogSabytieSave()
            dialogSabytieSave.show(supportFragmentManager, "sabytie_save")
        } else if (back) {
            home = true
            menu?.let {
                val item = it.findItem(R.id.action_cansel)
                onOptionsItemSelected(item)
            }
        } else {
            if (redak) {
                MainActivity.setListPadzeia(this)
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDialogPositiveClick() {
        menu?.let {
            val item: MenuItem = if (save) it.findItem(R.id.action_save_redak) else it.findItem(R.id.action_save)
            onOptionsItemSelected(item)
        }
    }

    override fun onDialogNegativeClick() {
        menu?.let {
            val item = it.findItem(R.id.action_cansel)
            onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 109 || requestCode == 1092 || requestCode == 1093) {
            if (data != null) {
                var dayyear: Long = 0
                val day = data.getIntExtra("data", 0).toLong()
                val year = data.getIntExtra("year", c[Calendar.YEAR])
                for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until year) {
                    dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
                }
                val mills = (dayyear + day) * 86400000L
                val setCal = Calendar.getInstance() as GregorianCalendar
                setCal[SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1, 0, 0] = 0
                val timeold = setCal.timeInMillis
                result = mills + timeold
                setCal.timeInMillis = result
                var nol1 = ""
                var nol2 = ""
                if (setCal[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                if (setCal[Calendar.MONTH] < 9) nol2 = "0"
                da = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                yearG = setCal[Calendar.YEAR]
                munG = setCal[Calendar.MONTH]
                if (requestCode == 109) {
                    val days = label1.text.toString().split(".")
                    val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
                    val days2 = label12.text.toString().split(".")
                    val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
                    val kon = gc2[Calendar.DAY_OF_YEAR]
                    val res = gc[Calendar.DAY_OF_YEAR]
                    if (kon - res >= 0) {
                        var da1: String
                        setCal.add(Calendar.DATE, kon - res)
                        nol1 = if (setCal[Calendar.DAY_OF_MONTH] < 10) "0" else ""
                        nol2 = if (setCal[Calendar.MONTH] < 9) "0" else ""
                        da1 = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                        label12.text = da1
                        if (gc2[Calendar.YEAR] > gc[Calendar.YEAR]) {
                            var leapYear = 365
                            if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 366
                            setCal.add(Calendar.DATE, -(kon - res))
                            setCal.add(Calendar.DATE, leapYear - res + kon)
                            nol1 = if (setCal[Calendar.DAY_OF_MONTH] < 10) "0" else ""
                            nol2 = if (setCal[Calendar.MONTH] < 9) "0" else ""
                            da1 = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                            label12.text = da1
                        }
                    }
                    label1.text = da
                    nol1 = ""
                    nol2 = ""
                    setCal.add(Calendar.DATE, 1)
                    if (setCal[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (setCal[Calendar.MONTH] < 9) nol2 = "0"
                    val days3 = labelbutton12.text.toString().split(".")
                    val gc3 = GregorianCalendar(days3[2].toInt(), days3[1].toInt() - 1, days3[0].toInt(), 0, 0, 0)
                    val days4 = label1.text.toString().split(".")
                    val gc4 = GregorianCalendar(days4[2].toInt(), days4[1].toInt() - 1, days4[0].toInt(), 0, 0, 0)
                    val kon2 = gc3.timeInMillis
                    val resul = gc4.timeInMillis
                    if (kon2 - resul < 0) labelbutton12.text = resources.getString(R.string.Sabytie, nol1, setCal[Calendar.DAY_OF_MONTH], nol2, setCal[Calendar.MONTH] + 1, setCal[Calendar.YEAR])
                    val temp = editText2.text
                    editText2.setText("")
                    editText2.text = temp
                }
                if (requestCode == 1092) {
                    label12.text = da
                    val days = label1.text.toString().split(".")
                    val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
                    val days2 = label12.text.toString().split(".")
                    val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
                    val kon = gc2.timeInMillis
                    result = gc.timeInMillis
                    if (kon - result < 0) {
                        MainActivity.toastView(this@Sabytie, getString(R.string.data_sabytie_error))
                        da = label1.text.toString()
                        label12.text = da
                    }
                }
                if (requestCode == 1093) {
                    labelbutton12.text = da
                    val days = label1.text.toString().split(".")
                    val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
                    gc.add(Calendar.DATE, 1)
                    val days2 = labelbutton12.text.toString().split(".")
                    val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
                    val kon = gc2.timeInMillis
                    val resul = gc.timeInMillis
                    if (kon - resul < 0) {
                        MainActivity.toastView(this@Sabytie, getString(R.string.data_sabytie_error2))
                        nol1 = ""
                        nol2 = ""
                        if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                        if (gc[Calendar.MONTH] < 9) nol2 = "0"
                        labelbutton12.text = resources.getString(R.string.Sabytie, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR])
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.sabytie, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_add).isVisible = false
        menu.findItem(R.id.action_delite).isVisible = false
        menu.findItem(R.id.action_save).isVisible = false
        menu.findItem(R.id.action_save_redak).isVisible = false
        menu.findItem(R.id.action_cansel).isVisible = false
        when (idMenu) {
            1 -> {
                menu.findItem(R.id.action_add).isVisible = true
                menu.findItem(R.id.action_delite).isVisible = true
            }
            2 -> {
                menu.findItem(R.id.action_save).isVisible = true
                menu.findItem(R.id.action_cansel).isVisible = true
            }
            3 -> {
                menu.findItem(R.id.action_save_redak).isVisible = true
                menu.findItem(R.id.action_cansel).isVisible = true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!home) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return true
            }
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        c = Calendar.getInstance() as GregorianCalendar
        val shakeanimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_settings) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SettingsActivity.notificationChannel(this, channelID = SettingsActivity.NOTIFICATION_CHANNEL_ID_SABYTIE)
                try {
                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, SettingsActivity.NOTIFICATION_CHANNEL_ID_SABYTIE)
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    try {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        MainActivity.toastView(this, getString(R.string.error_ch))
                    }
                }
            } else {
                val settings = DialogSabytieSettings()
                settings.show(supportFragmentManager, "settings")
            }
        }
        if (id == R.id.action_save) {
            redak = true
            back = false
            val edit = editText.text.toString().trim()
            var edit2 = editText2.text.toString()
            da = label1.text.toString()
            ta = label2.text.toString()
            daK = label12.text.toString()
            taK = label22.text.toString()
            if (edit != "") {
                var londs: Long = 0
                var londs2: Long = 0
                val days = label1.text.toString().split(".")
                val times = label2.text.toString().split(":")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                result = gc.timeInMillis
                if (!konec) {
                    daK = da
                    taK = ta
                }
                if (edit2 != "") {
                    londs = edit2.toLong()
                    when (posit) {
                        0 -> londs *= 60000L
                        1 -> londs *= 3600000L
                        2 -> londs *= 86400000L
                        3 -> londs *= 604800000L
                    }
                } else {
                    edit2 = "-1"
                }
                when (repitL) {
                    0 -> {
                        time = "0"
                        if (edit2 != "-1") {
                            londs2 = result - londs
                            val londs3 = londs2 / 100000L
                            if (londs2 > c.timeInMillis) {
                                val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                when {
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    }
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    }
                                    else -> {
                                        am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                    }
                                }
                            }
                        }
                        MainActivity.padzeia.add(Padzeia(edit, da, ta, londs2, posit, edit2, daK, taK, repitL, time, color, konec))
                    }
                    1 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.DAY_OF_YEAR]
                        var leapYear = 365 - dayof + 365 + 1
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 86400000L, pIntent)
                                        break
                                    }
                                    londs2 += 86400000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2[Calendar.DAY_OF_MONTH] < 10) nol3 = "0"
                            if (gc2[Calendar.MONTH] < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2[Calendar.DAY_OF_MONTH] + "." + nol4 + (gc2[Calendar.MONTH] + 1) + "." + gc2[Calendar.YEAR], taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 1)
                            gc2.add(Calendar.DATE, 1)
                            i++
                        }
                    }
                    2 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.DAY_OF_YEAR]
                        var leapYear = 365 - dayof + 365 + 1
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (gc[Calendar.DAY_OF_WEEK] in 2..6) {
                                if (edit2 != "-1") {
                                    londs2 = result - londs
                                    val londs3 = londs2 / 100000L
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                                var nol1 = ""
                                var nol2 = ""
                                var nol3 = ""
                                var nol4 = ""
                                if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                                if (gc[Calendar.MONTH] < 9) nol2 = "0"
                                if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                                if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                                MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            }
                            gc.add(Calendar.DATE, 1)
                            gc2.add(Calendar.DATE, 1)
                            i++
                        }
                    }
                    3 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.DAY_OF_YEAR]
                        var leapYear = 365 - dayof + 365 + 1
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var schet = 0
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (schet < 2) {
                                if (edit2 != "-1") {
                                    londs2 = result - londs
                                    val londs3 = londs2 / 100000L
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                                var nol1 = ""
                                var nol2 = ""
                                var nol3 = ""
                                var nol4 = ""
                                if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                                if (gc[Calendar.MONTH] < 9) nol2 = "0"
                                if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                                if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                                MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            }
                            schet++
                            gc.add(Calendar.DATE, 1)
                            gc2.add(Calendar.DATE, 1)
                            if (schet == 4) schet = 0
                            i++
                        }
                    }
                    4 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.WEEK_OF_YEAR]
                        var leapYear = 52 - dayof + 52 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.WEEK_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 604800000L, pIntent)
                                        break
                                    }
                                    londs2 += 604800000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 7)
                            gc2.add(Calendar.DATE, 7)
                            i++
                        }
                    }
                    5 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        
                        val dayof = gc[Calendar.WEEK_OF_YEAR]
                        var leapYear = 26 - dayof / 2 + 26 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 14 * 86400000L, pIntent)
                                        break
                                    }
                                    londs2 += 14 * 86400000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 14)
                            gc2.add(Calendar.DATE, 14)
                            i++
                        }
                    }
                    6 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        
                        val dayof = gc[Calendar.WEEK_OF_YEAR]
                        var leapYear = 13 - dayof / 4 + 13
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 2419200000L, pIntent)
                                        break
                                    }
                                    londs2 += 2419200000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 28)
                            gc2.add(Calendar.DATE, 28)
                            i++
                        }
                    }
                    7 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.MONTH] + 1
                        var leapYear = 12 - dayof + 12 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (londs2 > c.timeInMillis) {
                                    val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                    when {
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                            am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        else -> {
                                            am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.MONTH, 1)
                            gc2.add(Calendar.MONTH, 1)
                            i++
                        }
                    }
                    8 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        var leapYear = 10
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (londs2 > c.timeInMillis) {
                                    val intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                    when {
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                            am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        else -> {
                                            am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.YEAR, 1)
                            gc2.add(Calendar.YEAR, 1)
                            i++
                        }
                    }
                }
                val gson = Gson()
                val outputStream = FileWriter("$filesDir/Sabytie.json")
                outputStream.write(gson.toJson(MainActivity.padzeia))
                outputStream.close()
                MainActivity.padzeia.sort()
                sabytie2.clear()
                for (i in 0 until MainActivity.padzeia.size) {
                    sabytie2.add(SabytieDataAdapter(i.toLong(), MainActivity.padzeia[i].dat + " " + MainActivity.padzeia[i].padz, MainActivity.padzeia[i].color))
                }
                if (editText2.text.toString() != "") {
                    if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                }
                adapter.notifyDataSetChanged()
                editText.setText("")
                editText2.setText("")
                MainActivity.toastView(this@Sabytie, getString(R.string.save))
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
                titleLayout.visibility = View.GONE
                drag_list_view.visibility = View.VISIBLE
                idMenu = 1
                invalidateOptionsMenu()
            } else {
                editText.startAnimation(shakeanimation)
            }
        }
        if (id == R.id.action_save_redak) {
            redak = true
            back = false
            val p = MainActivity.padzeia[nomer]
            val edit = editText.text.toString().trim()
            var edit2 = editText2.text.toString()
            da = label1.text.toString()
            ta = label2.text.toString()
            daK = label12.text.toString()
            taK = label22.text.toString()
            if (edit != "") {
                var intent: Intent
                var pIntent: PendingIntent
                var londs: Long = 0
                var londs2: Long = 0
                val days = label1.text.toString().split(".")
                val times = label2.text.toString().split(":")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                result = gc.timeInMillis
                if (!konec) {
                    daK = da
                    taK = ta
                }
                if (edit2 != "") {
                    londs = edit2.toLong()
                    when (posit) {
                        0 -> londs *= 60000L
                        1 -> londs *= 3600000L
                        2 -> londs *= 86400000L
                        3 -> londs *= 604800000L
                    }
                } else {
                    edit2 = "-1"
                }
                val del = ArrayList<Padzeia>()
                MainActivity.padzeia.forEach {
                    if (p.padz.contains(it.padz)) {
                        del.add(it)
                        if (it.sec != "-1") {
                            intent = createIntent(it.padz, "Падзея" + " " + it.dat + " у " + it.tim, it.dat, it.tim)
                            val londs3 = it.paznic / 100000L
                            pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                            am.cancel(pIntent)
                            pIntent.cancel()
                        }
                    }
                }
                MainActivity.padzeia.removeAll(del)
                when (repitL) {
                    0 -> {
                        time = "0"
                        if (edit2 != "-1") {
                            londs2 = result - londs
                            val londs3 = londs2 / 100000L
                            if (londs2 > c.timeInMillis) {
                                intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                when {
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    }
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    }
                                    else -> {
                                        am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                    }
                                }
                            }
                        }
                        MainActivity.padzeia.add(Padzeia(edit, da, ta, londs2, posit, edit2, daK, taK, repitL, time, color, konec))
                    }
                    1 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.DAY_OF_YEAR]
                        var leapYear = 365 - dayof + 365 + 1
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 86400000L, pIntent)
                                        break
                                    }
                                    londs2 += 86400000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2[Calendar.DAY_OF_MONTH] < 10) nol3 = "0"
                            if (gc2[Calendar.MONTH] < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2[Calendar.DAY_OF_MONTH] + "." + nol4 + (gc2[Calendar.MONTH] + 1) + "." + gc2[Calendar.YEAR], taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 1)
                            gc2.add(Calendar.DATE, 1)
                            i++
                        }
                    }
                    2 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.DAY_OF_YEAR]
                        var leapYear = 365 - dayof + 365 + 1
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (gc[Calendar.DAY_OF_WEEK] in 2..6) {
                                if (edit2 != "-1") {
                                    londs2 = result - londs
                                    val londs3 = londs2 / 100000L
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                                var nol1 = ""
                                var nol2 = ""
                                var nol3 = ""
                                var nol4 = ""
                                if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                                if (gc[Calendar.MONTH] < 9) nol2 = "0"
                                if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                                if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                                MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            }
                            gc.add(Calendar.DATE, 1)
                            gc2.add(Calendar.DATE, 1)
                            i++
                        }
                    }
                    3 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.DAY_OF_YEAR]
                        var leapYear = 365 - dayof + 365 + 1
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var schet = 0
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (schet < 2) {
                                if (edit2 != "-1") {
                                    londs2 = result - londs
                                    val londs3 = londs2 / 100000L
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                                var nol1 = ""
                                var nol2 = ""
                                var nol3 = ""
                                var nol4 = ""
                                if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                                if (gc[Calendar.MONTH] < 9) nol2 = "0"
                                if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                                if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                                MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            }
                            schet++
                            gc.add(Calendar.DATE, 1)
                            gc2.add(Calendar.DATE, 1)
                            if (schet == 4) schet = 0
                            i++
                        }
                    }
                    4 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        
                        val dayof = gc[Calendar.WEEK_OF_YEAR]
                        var leapYear = 52 - dayof + 52 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.WEEK_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 604800000L, pIntent)
                                        break
                                    }
                                    londs2 += 604800000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 7)
                            gc2.add(Calendar.DATE, 7)
                            i++
                        }
                    }
                    5 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.WEEK_OF_YEAR]
                        var leapYear = 26 - dayof / 2 + 26 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 1209600000L, pIntent)
                                        break
                                    }
                                    londs2 += 1209600000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 14)
                            gc2.add(Calendar.DATE, 14)
                            i++
                        }
                    }
                    6 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.WEEK_OF_YEAR]
                        var leapYear = 13 - dayof / 4 + 13
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        if (radio == 1) {
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                var i = 0
                                while (i < 731) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, londs2, 2419200000L, pIntent)
                                        break
                                    }
                                    londs2 += 2419200000L
                                    i++
                                }
                            }
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (radio != 1) {
                                    if (londs2 > c.timeInMillis) {
                                        intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                        when {
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                            }
                                            else -> {
                                                am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                            }
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.DATE, 28)
                            gc2.add(Calendar.DATE, 28)
                            i++
                        }
                    }
                    7 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        val dayof = gc[Calendar.MONTH] + 1
                        var leapYear = 12 - dayof + 12 + 1
                        if (radio == 3) {
                            time = labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd: Int = gc3[Calendar.DAY_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (londs2 > c.timeInMillis) {
                                    intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                    pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                    when {
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                            am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        else -> {
                                            am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.MONTH, 1)
                            gc2.add(Calendar.MONTH, 1)
                            i++
                        }
                    }
                    8 -> {
                        time = "0"
                        val rdat = da.split(".")
                        gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                        val rdat2 = daK.split(".")
                        val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        
                        var leapYear = 10
                        if (radio == 2) {
                            time = editText4.text.toString()
                            if (time == "") time = "1"
                            leapYear = time.toInt()
                        }
                        var i = 0
                        while (i < leapYear) {
                            result = gc.timeInMillis
                            if (edit2 != "-1") {
                                londs2 = result - londs
                                val londs3 = londs2 / 100000L
                                if (londs2 > c.timeInMillis) {
                                    intent = createIntent(edit, "Падзея $da у $ta", da, ta)
                                    pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                                    when {
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                            am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        }
                                        else -> {
                                            am[AlarmManager.RTC_WAKEUP, londs2] = pIntent
                                        }
                                    }
                                }
                            }
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            var nol4 = ""
                            if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                            if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                            MainActivity.padzeia.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], ta, londs2, posit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), taK, repitL, time, color, konec))
                            gc.add(Calendar.YEAR, 1)
                            gc2.add(Calendar.YEAR, 1)
                            i++
                        }
                    }
                }
                val outputStream = FileWriter("$filesDir/Sabytie.json")
                val gson = Gson()
                outputStream.write(gson.toJson(MainActivity.padzeia))
                outputStream.close()
                MainActivity.padzeia.sort()
                sabytie2.clear()
                for (i in 0 until MainActivity.padzeia.size) {
                    sabytie2.add(SabytieDataAdapter(i.toLong(), MainActivity.padzeia[i].dat + " " + MainActivity.padzeia[i].padz, MainActivity.padzeia[i].color))
                }
                if (editText2.text.toString() != "") {
                    if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                }
                adapter.notifyDataSetChanged()
                editText.setText("")
                editText2.setText("")
                spinner3.setSelection(0)
                spinner4.setSelection(0)
                spinner5.setSelection(0)
                radioGroup.visibility = View.GONE
                var nol1 = ""
                var nol2 = ""
                c.add(Calendar.HOUR_OF_DAY, 1)
                if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                if (c[Calendar.MONTH] < 9) nol2 = "0"
                da = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
                ta = "$timeH:00"
                label1.text = da
                label2.text = ta
                label12.text = da
                label22.text = ta
                MainActivity.toastView(this@Sabytie, getString(R.string.save))
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
                titleLayout.visibility = View.GONE
                drag_list_view.visibility = View.VISIBLE
                idMenu = 1
                invalidateOptionsMenu()
            } else {
                editText.startAnimation(shakeanimation)
            }
            if (editCaliandar) {
                CaliandarFull.editCaliandarTitle = edit
                onSupportNavigateUp()
            }
        }
        if (id == R.id.action_cansel) {
            back = false
            home = false
            c.add(Calendar.HOUR_OF_DAY, 1)
            var nol1 = ""
            var nol2 = ""
            if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
            if (c[Calendar.MONTH] < 9) nol2 = "0"
            editText.setText("")
            editText2.setText("")
            spinner3.setSelection(0)
            spinner4.setSelection(0)
            spinner5.setSelection(0)
            radioGroup.visibility = View.GONE
            da = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
            ta = "$timeH:00"
            label1.text = da
            label2.text = ta
            label12.text = da
            label22.text = ta
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
            titleLayout.visibility = View.GONE
            drag_list_view.visibility = View.VISIBLE
            idMenu = 1
            invalidateOptionsMenu()
            if (editCaliandar) {
                onSupportNavigateUp()
            }
        }
        if (id == R.id.action_delite) {
            val delite = DialogSabytieDelite()
            delite.show(supportFragmentManager, "delite")
        }
        if (id == R.id.action_add) {
            addSabytie()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSabytieRedaktor(pos: Int) {
        drag_list_view.resetSwipedViews(null)
        val timeC: String
        save = true
        back = true
        val p = MainActivity.padzeia[pos]
        editText.setText(p.padz)
        label1.text = p.dat
        label2.text = p.tim
        label12.text = p.datK
        label22.text = p.timK
        if (p.sec == "-1") editText2.setText("") else editText2.setText(p.sec)
        spinner3.setSelection(p.vybtime)
        spinner4.setSelection(p.repit)
        spinner5.setSelection(p.color)
        labelbutton12Save = labelbutton12.text.toString()
        editText4Save = editText4.text.toString()
        radioSave = radio
        vybtimeSave = p.vybtime
        repitSave = p.repit
        colorSave = p.color
        color = p.color
        konec = p.konecSabytie
        checkBox2.isChecked = !konec
        if (konec)
            linearKonec.visibility = View.VISIBLE
        if (p.repit > 0) radioGroup.visibility = View.VISIBLE else radioGroup.visibility = View.GONE
        nomer = pos
        titleLayout.visibility = View.VISIBLE
        drag_list_view.visibility = View.GONE
        idMenu = 3
        timeC = p.count
        val count = timeC.split(".")
        when {
            timeC == "0" -> radioButton1.isChecked = true
            count.size == 1 -> {
                radioButton2.isChecked = true
                editText4.setText(timeC)
            }
            else -> {
                radioButton3.isChecked = true
                labelbutton12.text = timeC
            }
        }
        repitL = p.repit
        time = timeC
        editSave = editText.text.toString().trim()
        edit2Save = editText2.text.toString()
        daSave = label1.text.toString()
        taSave = label2.text.toString()
        daKSave = label12.text.toString()
        taKSave = label22.text.toString()
        invalidateOptionsMenu()
    }

    private fun addSabytie() {
        c = Calendar.getInstance() as GregorianCalendar
        save = false
        back = true
        konec = false
        checkBox2.isChecked = !konec
        var nol1 = ""
        var nol2 = ""
        if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c[Calendar.MONTH] < 9) nol2 = "0"
        da = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
        c.add(Calendar.HOUR_OF_DAY, 1)
        timeH = c[Calendar.HOUR_OF_DAY]
        ta = "$timeH:00"
        nol1 = ""
        nol2 = ""
        if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c[Calendar.MONTH] < 9) nol2 = "0"
        daK = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
        taK = ta
        titleLayout.visibility = View.VISIBLE
        drag_list_view.visibility = View.GONE
        label1.text = da
        label2.text = ta
        label12.text = daK
        label22.text = taK
        idMenu = 2
        spinner4.setSelection(0)
        spinner5.setSelection(0)
        color = 0
        editSave = editText.text.toString().trim()
        edit2Save = editText2.text.toString()
        daSave = label1.text.toString()
        taSave = label2.text.toString()
        daKSave = label12.text.toString()
        taKSave = label22.text.toString()
        invalidateOptionsMenu()
    }

    override fun sabytieDelAll() {
        redak = true
        CoroutineScope(Dispatchers.IO).launch {
            for (p in MainActivity.padzeia) {
                if (p.sec != "-1") {
                    val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                    val londs3 = p.paznic / 100000L
                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, 0)
                    am.cancel(pIntent)
                    pIntent.cancel()
                }
            }
            MainActivity.padzeia.clear()
            val file = File("$filesDir/Sabytie.json")
            val gson = Gson()
            file.writer().use {
                withContext(Dispatchers.IO) {
                    it.write(gson.toJson(MainActivity.padzeia))
                }
            }
        }
        adapter.notifyDataSetChanged()
        MainActivity.toastView(this, getString(R.string.remove_padzea))
    }

    override fun sabytieDelOld() {
        val c2 = Calendar.getInstance() as GregorianCalendar
        c2.set(Calendar.SECOND, 0)
        val del = ArrayList<Padzeia>()
        for (p in MainActivity.padzeia) {
            if (p.repit == 0) {
                val days = p.datK.split(".")
                val time = p.timK.split(":")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), time[0].toInt(), time[1].toInt(), 0)
                if (c2.timeInMillis >= gc.timeInMillis) {
                    if (p.sec != "-1") {
                        val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                        val londs3 = p.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this, londs3.toInt(), intent, 0)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                    del.add(p)
                }
            } else {
                val days = p.dat.split(".")
                val time = p.timK.split(":")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), time[0].toInt(), time[1].toInt(), 0)
                if (c2.timeInMillis >= gc.timeInMillis) {
                    if (p.sec != "-1") {
                        val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                        val londs3 = p.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this, londs3.toInt(), intent, 0)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                    del.add(p)
                }
            }
        }
        if (del.size != 0) {
            redak = true
            MainActivity.padzeia.removeAll(del)
            val outputStream = FileWriter("$filesDir/Sabytie.json")
            val gson = Gson()
            outputStream.write(gson.toJson(MainActivity.padzeia))
            outputStream.close()
            MainActivity.padzeia.sort()
            sabytie2.clear()
            for (i in 0 until MainActivity.padzeia.size) {
                sabytie2.add(SabytieDataAdapter(i.toLong(), MainActivity.padzeia[i].dat + " " + MainActivity.padzeia[i].padz, MainActivity.padzeia[i].color))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun createIntent(action: String, extra: String, data: String, time: String): Intent {
        val i = Intent(this, ReceiverBroad::class.java)
        i.action = action
        i.putExtra("sabytieSet", true)
        i.putExtra("extra", extra)
        val dateN = data.split(".")
        val timeN = time.split(":")
        val g = GregorianCalendar(dateN[2].toInt(), dateN[1].toInt() - 1, dateN[0].toInt(), 0, 0, 0)
        i.putExtra("dataString", dateN[0] + dateN[1] + timeN[0] + timeN[1])
        i.putExtra("year", g[Calendar.YEAR])
        return i
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("redak", redak)
        outState.putBoolean("back", back)
        outState.putBoolean("save", save)
        outState.putBoolean("titleLayout", titleLayout.visibility == View.VISIBLE)
        outState.putInt("idMenu", idMenu)
        outState.putString("ta", label2.text.toString())
        outState.putString("da", label1.text.toString())
        outState.putString("taK", label22.text.toString())
        outState.putString("daK", label12.text.toString())
    }

    private inner class SabytieAdapter(list: ArrayList<SabytieDataAdapter>, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<SabytieDataAdapter, SabytieAdapter.ViewHolder>() {
        private var dzenNoch = false
        private val day = Calendar.getInstance() as GregorianCalendar
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
            val textview = view.findViewById<TextViewRobotoCondensed>(R.id.text)
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            textview.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                val itemLeft = view.findViewById<TextViewRobotoCondensed>(R.id.item_left)
                itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                itemLeft.setBackgroundResource(R.color.colorprimary_material_dark)
                val itemRight = view.findViewById<TextViewRobotoCondensed>(R.id.item_right)
                itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                itemRight.setBackgroundResource(R.color.colorprimary_material_dark)
                view.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_dark)
                textview.setTextColor(ContextCompat.getColor(parent.context, R.color.colorIcons))
            } else {
                textview.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_text))
                view.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_default)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = mItemList[position].title
            val sab = text.split(" ")
            val data = sab[0].split(".")
            val gc = GregorianCalendar(data[2].toInt(), data[1].toInt() - 1, data[0].toInt())
            if (gc[Calendar.DAY_OF_YEAR] == day[Calendar.DAY_OF_YEAR] && gc[Calendar.YEAR] == day[Calendar.YEAR]) {
                holder.mText.setTypeface(null, Typeface.BOLD)
            } else {
                holder.mText.setTypeface(null, Typeface.NORMAL)
            }
            holder.mText.text = text
            holder.color.setBackgroundColor(Color.parseColor(colors[mItemList[position].color]))
            holder.buttonPopup.setOnClickListener {
                showPopupMenu(it, position)
            }
            holder.itemView.tag = mItemList[position]
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].id
        }

        private fun showPopupMenu(view: View, position: Int) {
            val popup = PopupMenu(this@Sabytie, view)
            val infl = popup.menuInflater
            infl.inflate(R.menu.popup, popup.menu)
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
                    R.id.menu_redoktor -> onSabytieRedaktor(position)
                    R.id.menu_remove -> onDialogDeliteClick(position)
                }
                true
            }
            popup.show()
        }

        private inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
            var mText: TextViewRobotoCondensed = itemView.findViewById(R.id.text)
            val color: TextViewRobotoCondensed = itemView.findViewById(R.id.color)
            val buttonPopup: ImageView = itemView.findViewById(R.id.button_popup)

            override fun onItemLongClicked(view: View): Boolean {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return true
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val contextMenuSabytie = DialogContextMenuSabytie.getInstance(adapterPosition, sabytie2[adapterPosition].title)
                contextMenuSabytie.show(supportFragmentManager, "context_menu_sabytie")
                return true
            }

            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                var title = ""
                var data = ""
                var time = ""
                var dataK = ""
                var timeK = ""
                var paz: Long = 0
                var konecSabytie = false
                for (i in MainActivity.padzeia.indices) {
                    if (i == adapterPosition) {
                        val p = MainActivity.padzeia[i]
                        title = p.padz
                        data = p.dat
                        time = p.tim
                        paz = p.paznic
                        dataK = p.datK
                        timeK = p.timK
                        konecSabytie = p.konecSabytie
                    }
                }
                var res = getString(R.string.sabytie_no_pavedam)
                val gc = Calendar.getInstance() as GregorianCalendar
                val realTime = gc.timeInMillis
                var paznicia = false
                if (paz != 0L) {
                    gc.timeInMillis = paz
                    var nol11 = ""
                    var nol21 = ""
                    var nol3 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol11 = "0"
                    if (gc[Calendar.MONTH] < 9) nol21 = "0"
                    if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                    res = "Паведаміць: " + nol11 + gc[Calendar.DAY_OF_MONTH] + "." + nol21 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR] + " у " + gc[Calendar.HOUR_OF_DAY] + ":" + nol3 + gc[Calendar.MINUTE]
                    if (realTime > paz) paznicia = true
                }
                val dialogShowSabytie = DialogSabytieShow.getInstance(title, data, time, dataK, timeK, res, paznicia, !konecSabytie)
                dialogShowSabytie.show(supportFragmentManager, "sabytie")
            }
        }

        init {
            itemList = list
        }
    }

    private class MyDragItem(context: Context, layoutId: Int) : DragItem(context, layoutId) {
        private val mycontext = context
        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            val dragTextView = dragView.findViewById<View>(R.id.text) as TextView
            dragTextView.text = text
            dragTextView.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            val k = mycontext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                clickedView.findViewById<TextViewRobotoCondensed>(R.id.text).setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
                clickedView.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_dark)
                val itemLeft = clickedView.findViewById<TextViewRobotoCondensed>(R.id.item_left)
                itemLeft.setTextColor(ContextCompat.getColor(mycontext, R.color.colorPrimary_black))
                itemLeft.setBackgroundResource(R.color.colorprimary_material_dark)
                val itemRight = clickedView.findViewById<TextViewRobotoCondensed>(R.id.item_right)
                itemRight.setTextColor(ContextCompat.getColor(mycontext, R.color.colorPrimary_black))
                itemRight.setBackgroundResource(R.color.colorprimary_material_dark)
                dragTextView.setTextColor(ContextCompat.getColor(mycontext, R.color.colorIcons))
                dragView.findViewById<View>(R.id.item_layout).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.colorprimary_material_dark))
            } else {
                dragTextView.setTextColor(ContextCompat.getColor(mycontext, R.color.colorPrimary_text))
                dragView.findViewById<View>(R.id.item_layout).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.colorDivider))
            }
        }
    }

    private inner class ColorAdapter(context: Context) : ArrayAdapter<String>(context, R.layout.simple_list_item_color, R.id.label, colors) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderColor
            if (convertView == null) {
                rootView = this@Sabytie.layoutInflater.inflate(R.layout.simple_list_item_color, parent, false)
                viewHolder = ViewHolderColor()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderColor
            }
            viewHolder.text?.setBackgroundColor(Color.parseColor(colors[position]))
            viewHolder.text?.text = nazvaPadzei
            viewHolder.text?.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            viewHolder.text?.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons))
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val text: TextView = view.findViewById(R.id.label)
            text.setBackgroundColor(Color.parseColor(colors[position]))
            text.text = nazvaPadzei
            text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            text.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons))
            return view
        }
    }

    private inner class SpinnerAdapter(context: Context, list: Array<String>) : ArrayAdapter<String>(context, R.layout.simple_list_item_1, list) {
        private val spinnerList = list
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderColor
            if (convertView == null) {
                rootView = this@Sabytie.layoutInflater.inflate(R.layout.simple_list_item_1, parent, false)
                viewHolder = ViewHolderColor()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.text1)
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderColor
            }
            viewHolder.text?.text = spinnerList[position]
            viewHolder.text?.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) viewHolder.text?.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons))
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val text: TextView = view.findViewById(R.id.text1)
            text.text = spinnerList[position]
            text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) text.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons))
            return view
        }
    }

    private class ViewHolderColor {
        var text: TextViewRobotoCondensed? = null
    }

    private inner class MyTextWatcher(private val editTextWatcher: EditTextRobotoCondensed) : TextWatcher {
        private var editPosition = 0
        private var check = 0
        private var editch = true
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            editch = count != after
            check = after
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable) {
            if (editch) {
                val edit = s.toString()
                val id = editTextWatcher.id
                if (id == R.id.editText) {
                    nazvaPadzei = if (edit != "") edit
                    else getString(R.string.sabytie_name)
                    colorAdapter.notifyDataSetChanged()
                }
                if (id == R.id.editText2) {
                    if (edit != "") {
                        val days = label1.text.toString().split(".")
                        val times = label2.text.toString().split(":")
                        val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        result = gc.timeInMillis
                        var londs = edit.toLong()
                        when (posit) {
                            0 -> londs *= 60000L
                            1 -> londs *= 3600000L
                            2 -> londs *= 86400000L
                            3 -> londs *= 604800000L
                        }
                        val londs2 = result - londs
                        gc.timeInMillis = londs2
                        var nol1 = ""
                        var nol2 = ""
                        var nol3 = ""
                        if (gc[Calendar.DATE] < 10) nol1 = "0"
                        if (gc[Calendar.MONTH] < 9) nol2 = "0"
                        if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                        pavedamic2.text = getString(R.string.sabytie_pavedam, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR], gc[Calendar.HOUR_OF_DAY], nol3, gc[Calendar.MINUTE])
                        val gcReal = Calendar.getInstance() as GregorianCalendar
                        if (gcReal.timeInMillis > londs2) {
                            if (dzenNoch) pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_black))
                            else pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary))
                        } else {
                            if (dzenNoch) pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons))
                            else pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_text))
                        }
                    } else {
                        pavedamic2.text = getString(R.string.sabytie_no_pavedam)
                        if (dzenNoch) pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorIcons))
                        else pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_text))
                    }
                }
                if (check != 0) {
                    editTextWatcher.removeTextChangedListener(this)
                    editTextWatcher.setText(edit)
                    editTextWatcher.setSelection(editPosition)
                    editTextWatcher.addTextChangedListener(this)
                }
            }
        }
    }

    private data class SabytieDataAdapter(val id: Long, val title: String, val color: Int)

    companion object {
        private val colors = arrayOf("#D00505", "#800080", "#C71585", "#FF00FF", "#F4A460", "#D2691E", "#A52A2A", "#1E90FF", "#6A5ACD", "#228B22", "#9ACD32", "#20B2AA")
        var editCaliandar = false

        fun getColors(context: Context): Array<String> {
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                colors[0] = "#f44336"
            } else {
                colors[0] = "#D00505"
            }
            return colors
        }
    }
}