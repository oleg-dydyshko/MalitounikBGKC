package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.DialogContextMenuSabytie.DialogContextMenuSabytieListener
import by.carkva_gazeta.malitounik.DialogDelite.DialogDeliteListener
import by.carkva_gazeta.malitounik.DialogSabytieSave.DialogSabytieSaveListener
import by.carkva_gazeta.malitounik.databinding.ListItemSabytieBinding
import by.carkva_gazeta.malitounik.databinding.SabytieBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemColorBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.util.Calendar
import java.util.GregorianCalendar

class Sabytie : BaseActivity(), DialogSabytieSaveListener, DialogContextMenuSabytieListener, DialogDeliteListener, DialogSabytieDelite.DialogSabytieDeliteListener, DialogSabytieTime.DialogSabytieTimeListener, DialogSabytieDeliteAll.DialogSabytieDeliteAllListener, DialogHelpAlarm.DialogHelpAlarmListener, DialogHelpNotificationApi33.DialogHelpNotificationApi33Listener {
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var konec = false
    private var back = false
    private var home = false
    private var redak = false
    private var save = false
    private lateinit var adapter: SabytieAdapter
    private lateinit var c: Calendar
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
    private var mLastClickTime: Long = 0
    private lateinit var colorAdapter: ColorAdapter
    private var nazvaPadzei = "Назва падзеі"
    private lateinit var binding: SabytieBinding
    private var resetTollbarJob: Job? = null
    private var searchView: SearchView? = null
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var searchViewQwery = ""
    private var actionExpandOn = false
    private val labelbutton12Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                val setCal = GregorianCalendar(arrayList[3].toInt(), arrayList[2].toInt(), arrayList[1].toInt(), 0, 0, 0)
                setCal[Calendar.MILLISECOND] = 0
                this.result = setCal.timeInMillis
                var nol1 = ""
                var nol2 = ""
                if (setCal[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                if (setCal[Calendar.MONTH] < 9) nol2 = "0"
                da = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                yearG = setCal[Calendar.YEAR]
                munG = setCal[Calendar.MONTH]
                binding.labelbutton12.text = da
                val days = binding.label1.text.toString().split(".")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
                gc.add(Calendar.DATE, 1)
                val days2 = binding.labelbutton12.text.toString().split(".")
                val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
                val kon = gc2.timeInMillis
                val resul = gc.timeInMillis
                if (kon - resul < 0) {
                    MainActivity.toastView(this, getString(R.string.data_sabytie_error2))
                    nol1 = ""
                    nol2 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    binding.labelbutton12.text = resources.getString(R.string.Sabytie, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR])
                }
            }
        }
    }
    private val labelbutton1Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                val setCal = GregorianCalendar(arrayList[3].toInt(), arrayList[2].toInt(), arrayList[1].toInt(), 0, 0, 0)
                setCal[Calendar.MILLISECOND] = 0
                this.result = setCal.timeInMillis
                var nol1 = ""
                var nol2 = ""
                if (setCal[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                if (setCal[Calendar.MONTH] < 9) nol2 = "0"
                da = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                yearG = setCal[Calendar.YEAR]
                munG = setCal[Calendar.MONTH]
                val days = binding.label1.text.toString().split(".")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
                val days2 = binding.label12.text.toString().split(".")
                val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
                val kon = gc2[Calendar.DAY_OF_YEAR]
                val res = gc[Calendar.DAY_OF_YEAR]
                if (kon - res >= 0) {
                    var da1: String
                    setCal.add(Calendar.DATE, kon - res)
                    nol1 = if (setCal[Calendar.DAY_OF_MONTH] < 10) "0" else ""
                    nol2 = if (setCal[Calendar.MONTH] < 9) "0" else ""
                    da1 = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                    binding.label12.text = da1
                    if (gc2[Calendar.YEAR] > gc[Calendar.YEAR]) {
                        var leapYear = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 366
                        setCal.add(Calendar.DATE, -(kon - res))
                        setCal.add(Calendar.DATE, leapYear - res + kon)
                        nol1 = if (setCal[Calendar.DAY_OF_MONTH] < 10) "0" else ""
                        nol2 = if (setCal[Calendar.MONTH] < 9) "0" else ""
                        da1 = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                        binding.label12.text = da1
                    }
                }
                binding.label1.text = da
                nol1 = ""
                nol2 = ""
                setCal.add(Calendar.DATE, 1)
                if (setCal[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                if (setCal[Calendar.MONTH] < 9) nol2 = "0"
                val days3 = binding.labelbutton12.text.toString().split(".")
                val gc3 = GregorianCalendar(days3[2].toInt(), days3[1].toInt() - 1, days3[0].toInt(), 0, 0, 0)
                val days4 = binding.label1.text.toString().split(".")
                val gc4 = GregorianCalendar(days4[2].toInt(), days4[1].toInt() - 1, days4[0].toInt(), 0, 0, 0)
                val kon2 = gc3.timeInMillis
                val resul = gc4.timeInMillis
                if (kon2 - resul < 0) binding.labelbutton12.text = resources.getString(R.string.Sabytie, nol1, setCal[Calendar.DAY_OF_MONTH], nol2, setCal[Calendar.MONTH] + 1, setCal[Calendar.YEAR])
                val temp = binding.editText2.text
                binding.editText2.text?.clear()
                binding.editText2.text = temp
            }
        }
    }
    private val labelbutton12bLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                val setCal = GregorianCalendar(arrayList[3].toInt(), arrayList[2].toInt(), arrayList[1].toInt(), 0, 0, 0)
                setCal[Calendar.MILLISECOND] = 0
                this.result = setCal.timeInMillis
                var nol1 = ""
                var nol2 = ""
                if (setCal[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                if (setCal[Calendar.MONTH] < 9) nol2 = "0"
                da = nol1 + setCal[Calendar.DAY_OF_MONTH] + "." + nol2 + (setCal[Calendar.MONTH] + 1) + "." + setCal[Calendar.YEAR]
                yearG = setCal[Calendar.YEAR]
                munG = setCal[Calendar.MONTH]
                binding.label12.text = da
                val days = binding.label1.text.toString().split(".")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
                val days2 = binding.label12.text.toString().split(".")
                val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
                val kon = gc2.timeInMillis
                this.result = gc.timeInMillis
                if (kon - this.result < 0) {
                    MainActivity.toastView(this, getString(R.string.data_sabytie_error))
                    da = binding.label1.text.toString()
                    binding.label12.text = da
                }
            }
        }
    }
    private val settingsAlarmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.editText2.setText(edit2Save)
        } else {
            edit2Save = ""
            binding.editText2.setText(edit2Save)
        }
    }
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            binding.pavedamic3.visibility = View.GONE
        } else {
            val prefEditor = k.edit()
            prefEditor.putBoolean("permissionNotificationApi33", false)
            prefEditor.apply()
        }
    }

    override fun onDialogHelpNotificationApi33(notification: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            edit2Save = binding.editText2.text.toString()
            mPermissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onSettingsAlarm(notification: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            edit2Save = notification.toString()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            val pkg = "package:$packageName"
            val pkgUri = Uri.parse(pkg)
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, pkgUri)
            settingsAlarmLauncher.launch(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun sabytieTimePositive(nomerDialoga: Int, hour: Int, minute: Int) {
        if (nomerDialoga == 1) {
            da = binding.label1.text.toString()
            daK = binding.label12.text.toString()
            taK = binding.label22.text.toString()
            c = Calendar.getInstance()
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
                    binding.label22.text = ta
                }
            }
            binding.label2.text = ta
            val temp = binding.editText2.text
            binding.editText2.text?.clear()
            binding.editText2.text = temp
        }
        if (nomerDialoga == 2) {
            var tr = ""
            if (minute < 10) tr = "0"
            taK = "$hour:$tr$minute"
            binding.label22.text = taK
            val days = binding.label1.text.toString().split(".")
            val gc1 = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), 0, 0, 0)
            val days2 = binding.label12.text.toString().split(".")
            val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), 0, 0, 0)
            val kon = gc2[Calendar.DAY_OF_YEAR]
            val res = gc1[Calendar.DAY_OF_YEAR]
            if (kon - res == 0) {
                val times = binding.label2.text.toString().split(":")
                val times2 = binding.label22.text.toString().split(":")
                val gc3 = GregorianCalendar(gc2[Calendar.YEAR], gc2[Calendar.MONTH], gc2[Calendar.DAY_OF_MONTH], times[0].toInt(), times[1].toInt(), 0)
                val gc4 = GregorianCalendar(gc2[Calendar.YEAR], gc2[Calendar.MONTH], gc2[Calendar.DAY_OF_MONTH], times2[0].toInt(), times2[1].toInt(), 0)
                if (gc4.timeInMillis - gc3.timeInMillis < 1000) {
                    gc2.add(Calendar.DATE, 1)
                    var nol112 = ""
                    var nol212 = ""
                    if (gc2[Calendar.DAY_OF_MONTH] < 10) nol112 = "0"
                    if (gc2[Calendar.MONTH] < 9) nol212 = "0"
                    val da1 = nol112 + gc2[Calendar.DAY_OF_MONTH] + "." + nol212 + (gc2[Calendar.MONTH] + 1) + "." + gc2[Calendar.YEAR]
                    binding.label12.text = da1
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = SabytieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.labelbutton12.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            val w = binding.labelbutton12.text.toString().split(".")
            val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
            yearG = gc[Calendar.YEAR]
            munG = gc[Calendar.MONTH]
            val i = Intent(this@Sabytie, CaliandarMun::class.java)
            i.putExtra("day", gc[Calendar.DATE])
            i.putExtra("year", yearG)
            i.putExtra("mun", munG)
            i.putExtra("getData", true)
            labelbutton12Launcher.launch(i)
        })
        binding.radioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            when (checkedId) {
                R.id.radioButton1 -> {
                    radio = 1
                    binding.radioButton2a.visibility = View.GONE
                    binding.labelbutton12.visibility = View.GONE
                }

                R.id.radioButton2 -> {
                    radio = 2
                    binding.radioButton2a.visibility = View.VISIBLE
                    binding.labelbutton12.visibility = View.GONE
                }

                R.id.radioButton3 -> {
                    if (idMenu != 3) {
                        val w = binding.labelbutton12.text.toString().split(".")
                        val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
                        yearG = gc[Calendar.YEAR]
                        munG = gc[Calendar.MONTH]
                        val i = Intent(this@Sabytie, CaliandarMun::class.java)
                        i.putExtra("day", gc[Calendar.DATE])
                        i.putExtra("year", yearG)
                        i.putExtra("mun", munG)
                        i.putExtra("getData", true)
                        labelbutton12Launcher.launch(i)
                    }
                    radio = 3
                    binding.radioButton2a.visibility = View.GONE
                    binding.labelbutton12.visibility = View.VISIBLE
                }
            }
        }
        am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        c = Calendar.getInstance()
        c.add(Calendar.DATE, 1)
        var nol1 = ""
        var nol2 = ""
        if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c[Calendar.MONTH] < 9) nol2 = "0"
        binding.labelbutton12.text = resources.getString(R.string.Sabytie, nol1, c[Calendar.DAY_OF_MONTH], nol2, c[Calendar.MONTH] + 1, c[Calendar.YEAR])
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
        binding.label1.text = da
        val notifi = resources.getStringArray(R.array.sabytie_izmerenie)
        val adapter2 = SpinnerAdapter(this, notifi)
        binding.spinner3.adapter = adapter2
        binding.spinner3.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                posit = position
                val temp = binding.editText2.text
                binding.editText2.text?.clear()
                binding.editText2.text = temp
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val repit = resources.getStringArray(R.array.sabytie_repit)
        val adapter3 = SpinnerAdapter(this, repit)
        binding.spinner4.adapter = adapter3
        binding.spinner4.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                repitL = position
                if (repitL == 7) {
                    binding.radioButton3.isClickable = false
                    binding.radioButton3.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorSecondary_text))
                } else {
                    binding.radioButton3.isClickable = true
                    if (dzenNoch) binding.radioButton3.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorWhite))
                    else binding.radioButton3.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_text))
                }
                if (repitL == 0) binding.radioButton1.isChecked = true
                if (repitL > 0) {
                    binding.radioGroup.visibility = View.VISIBLE
                } else {
                    binding.radioGroup.visibility = View.GONE
                    binding.radioButton2a.visibility = View.GONE
                    binding.labelbutton12.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        colorAdapter = ColorAdapter(this)
        binding.spinner5.adapter = colorAdapter
        binding.spinner5.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                color = position
                binding.spinner5.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spinner5.setSelection(0)
        binding.label1.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            val w = binding.label1.text.toString().split(".")
            val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
            yearG = gc[Calendar.YEAR]
            munG = gc[Calendar.MONTH]
            val i = Intent(this@Sabytie, CaliandarMun::class.java)
            i.putExtra("day", gc[Calendar.DATE])
            i.putExtra("year", yearG)
            i.putExtra("mun", munG)
            i.putExtra("getData", true)
            labelbutton1Launcher.launch(i)
        }
        binding.label2.text = ta
        binding.label2.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            val dialogSabytieTime = DialogSabytieTime.getInstance(1, binding.label2.text.toString())
            dialogSabytieTime.show(supportFragmentManager, "dialogSabytieTime")
        }
        binding.label12.text = da
        binding.label12.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            val w = binding.label12.text.toString().split(".")
            val gc = GregorianCalendar(w[2].toInt(), w[1].toInt() - 1, w[0].toInt())
            yearG = gc[Calendar.YEAR]
            munG = gc[Calendar.MONTH]
            val i = Intent(this@Sabytie, CaliandarMun::class.java)
            i.putExtra("day", gc[Calendar.DATE])
            i.putExtra("year", yearG)
            i.putExtra("mun", munG)
            i.putExtra("getData", true)
            labelbutton12bLauncher.launch(i)
        }
        binding.label22.text = ta
        binding.label22.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            val dialogSabytieTime = DialogSabytieTime.getInstance(2, binding.label22.text.toString())
            dialogSabytieTime.show(supportFragmentManager, "dialogSabytieTime")
        }
        binding.checkBox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.linearKonec.visibility = View.GONE
                konec = false
            } else {
                binding.linearKonec.visibility = View.VISIBLE
                konec = true
            }
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        binding.pavedamic3.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                MainActivity.toastView(this, getString(R.string.error_ch2))
            }
        }
        binding.titleToolbar.text = resources.getString(R.string.sabytie)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.pacatak.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.kanec.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.pavedamic.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.pavedamic3.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.pavtor.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.cvet.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.pazov.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.label1.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.label2.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.label12.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.label22.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.checkBox2.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.label1.setBackgroundResource(R.drawable.selector_dark)
            binding.label2.setBackgroundResource(R.drawable.selector_dark)
            binding.label12.setBackgroundResource(R.drawable.selector_dark)
            binding.label22.setBackgroundResource(R.drawable.selector_dark)
            binding.checkBox2.setBackgroundResource(R.drawable.selector_dark)
            binding.labelbutton12.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.labelbutton12.setBackgroundResource(R.drawable.selector_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        val c2 = Calendar.getInstance()
        nol1 = ""
        nol2 = ""
        if (c2[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c2[Calendar.MONTH] < 9) nol2 = "0"
        val daInit = nol1 + c2[Calendar.DAY_OF_MONTH] + "." + nol2 + (c2[Calendar.MONTH] + 1) + "." + c2[Calendar.YEAR]
        var initPosition = -1
        for (i in 0 until MainActivity.padzeia.size) {
            if (daInit == MainActivity.padzeia[i].dat) {
                initPosition = i
                break
            }
        }
        if (initPosition == -1) initPosition = 0
        adapter = SabytieAdapter(MainActivity.padzeia, R.id.image, false)
        binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
        binding.dragListView.setLayoutManager(LinearLayoutManager(this))
        binding.dragListView.setAdapter(adapter, false)
        binding.dragListView.setCanDragHorizontally(false)
        binding.dragListView.setCanDragVertically(true)
        binding.dragListView.setCanDragVertically(false)
        binding.dragListView.recyclerView.scrollToPosition(initPosition)
        binding.dragListView.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                val adapterItem = item.tag as Padzeia
                val pos: Int = binding.dragListView.adapter.getPositionForItem(adapterItem)
                if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    onDialogDeliteClick(pos)
                }
                if (swipedDirection == ListSwipeItem.SwipeDirection.RIGHT) {
                    onDialogEditClick(pos)
                }
            }
        })
        if (savedInstanceState != null) {
            searchViewQwery = savedInstanceState.getString("SearchViewQwery", "")
            actionExpandOn = savedInstanceState.getBoolean("actionExpandOn")
            redak = savedInstanceState.getBoolean("redak")
            back = savedInstanceState.getBoolean("back")
            save = savedInstanceState.getBoolean("save")
            idMenu = savedInstanceState.getInt("idMenu")
            if (savedInstanceState.getBoolean("titleLayout")) {
                binding.titleLayout.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
                invalidateOptionsMenu()
            }
            ta = savedInstanceState.getString("ta") ?: "0:0"
            da = savedInstanceState.getString("da") ?: "0.0.0"
            taK = savedInstanceState.getString("taK") ?: "0:0"
            daK = savedInstanceState.getString("daK") ?: "0.0.0"
            binding.labelbutton12.text = savedInstanceState.getString("labelbutton12")
            binding.label1.text = da
            binding.label2.text = ta
            binding.label12.text = daK
            binding.label22.text = taK
        }
        binding.editText.addTextChangedListener(MyTextWatcher(binding.editText))
        binding.editText2.addTextChangedListener(MyTextWatcher(binding.editText2))
        editSave = binding.editText.text.toString().trim()
        edit2Save = binding.editText2.text.toString()
        daSave = binding.label1.text.toString()
        taSave = binding.label2.text.toString()
        daKSave = binding.label12.text.toString()
        taKSave = binding.label22.text.toString()
        labelbutton12Save = binding.labelbutton12.text.toString()
        editText4Save = binding.editText4.text.toString()
        colorSave = binding.spinner5.selectedItemPosition
        radioSave = radio
        if (intent.extras?.getBoolean("shortcuts", false) == true) {
            addSabytie()
        }
        if (intent.extras?.getBoolean("edit", false) == true) {
            val position = intent.extras?.getInt("position") ?: 0
            onDialogEditClick(position)
            editCaliandar = true
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

    override fun onDialogEditClick(position: Int) {
        binding.dragListView.resetSwipedViews(null)
        save = true
        back = true
        val p = MainActivity.padzeia[position]
        binding.editText.setText(p.padz)
        binding.label1.text = p.dat
        binding.label2.text = p.tim
        binding.label12.text = p.datK
        binding.label22.text = p.timK
        if (p.sec == "-1") binding.editText2.text?.clear()
        else binding.editText2.setText(p.sec)
        binding.spinner3.setSelection(p.vybtime)
        binding.spinner4.setSelection(p.repit)
        binding.spinner5.setSelection(p.color)
        labelbutton12Save = binding.labelbutton12.text.toString()
        editText4Save = binding.editText4.text.toString()
        radioSave = radio
        vybtimeSave = p.vybtime
        repitSave = p.repit
        colorSave = p.color
        color = p.color
        konec = p.konecSabytie
        binding.checkBox2.isChecked = !konec
        if (konec) binding.linearKonec.visibility = View.VISIBLE
        if (p.repit > 0) binding.radioGroup.visibility = View.VISIBLE else binding.radioGroup.visibility = View.GONE
        nomer = getPadzeaiPosition(position, p.padz, p.dat)
        binding.titleLayout.visibility = View.VISIBLE
        binding.dragListView.visibility = View.GONE
        idMenu = 3
        time = p.count
        val count = time.split(".")
        when {
            time == "0" -> binding.radioButton1.isChecked = true
            count.size == 1 -> {
                binding.radioButton2.isChecked = true
                binding.editText4.setText(time)
            }

            else -> {
                binding.radioButton3.isChecked = true
                binding.labelbutton12.text = time
            }
        }
        repitL = p.repit
        editSave = binding.editText.text.toString().trim()
        edit2Save = binding.editText2.text.toString()
        daSave = binding.label1.text.toString()
        taSave = binding.label2.text.toString()
        daKSave = binding.label12.text.toString()
        taKSave = binding.label22.text.toString()
        invalidateOptionsMenu()
        binding.editText.requestFocus()
        binding.toolbar.collapseActionView()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun fileDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun fileDelite(position: Int, file: String) {
        if (SettingsActivity.isPadzeiaSetAlarm) return
        redak = true
        val sab = MainActivity.padzeia[position]
        val filen = sab.padz
        val del = ArrayList<Padzeia>()
        for (p in MainActivity.padzeia) {
            if (p.padz == filen) {
                del.add(p)
            }
        }

        MainActivity.padzeia.removeAll(del.toSet())
        val outputStream = FileWriter("$filesDir/Sabytie.json")
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
        outputStream.write(gson.toJson(MainActivity.padzeia, type))
        outputStream.close()
        adapter.updateList(MainActivity.padzeia)
        CoroutineScope(Dispatchers.IO).launch {
            if (sab.count == "0") {
                if (sab.repit == 1 || sab.repit == 4 || sab.repit == 5 || sab.repit == 6) {
                    if (sab.sec != "-1") {
                        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PendingIntent.FLAG_IMMUTABLE or 0
                        } else {
                            0
                        }
                        val intent = SettingsActivity.createIntentSabytie(sab.padz, sab.dat, sab.tim)
                        val londs3 = sab.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                } else {
                    for (p in del) {
                        if (p.padz.contains(filen)) {
                            if (p.sec != "-1") {
                                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    PendingIntent.FLAG_IMMUTABLE or 0
                                } else {
                                    0
                                }
                                val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                                val londs3 = p.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                am.cancel(pIntent)
                                pIntent.cancel()
                            }
                        }
                    }
                }
            } else {
                for (p in del) {
                    if (p.sec != "-1") {
                        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PendingIntent.FLAG_IMMUTABLE or 0
                        } else {
                            0
                        }
                        val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                        val londs3 = p.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                }
            }
        }
        MainActivity.toastView(this, getString(R.string.remove_padzea))
    }

    override fun onDialogDeliteClick(position: Int) {
        val padzeia = MainActivity.padzeia[position]
        val pos = getPadzeaiPosition(position, padzeia.padz, padzeia.dat)
        binding.toolbar.collapseActionView()
        val dd = DialogDelite.getInstance(pos, "", "з падзей", getString(R.string.sabytie_data_name, padzeia.dat, padzeia.padz))
        dd.show(supportFragmentManager, "dialig_delite")
    }

    override fun onBack() {
        val editSaveN = binding.editText.text.toString().trim()
        val edit2SaveN = binding.editText2.text.toString()
        val edit4SaveN = binding.editText4.text.toString()
        val daSaveN = binding.label1.text.toString()
        val taSaveN = binding.label2.text.toString()
        val daKSaveN = binding.label12.text.toString()
        val taKSaveN = binding.label22.text.toString()
        if (editCaliandar) {
            onSupportNavigateUp()
        } else if (!(edit2SaveN == edit2Save && editSaveN == editSave && daSaveN == daSave && daKSaveN == daKSave && taSaveN == taSave && taKSaveN == taKSave && binding.spinner3.selectedItemPosition == vybtimeSave && binding.spinner4.selectedItemPosition == repitSave && binding.spinner5.selectedItemPosition == colorSave && labelbutton12Save == binding.labelbutton12.text.toString() && editText4Save == edit4SaveN && radioSave == radio) && binding.dragListView.visibility == View.GONE) {
            val dialogSabytieSave = DialogSabytieSave()
            dialogSabytieSave.show(supportFragmentManager, "sabytie_save")
        } else if (back) {
            home = true
            actionCansel()
        } else {
            if (redak) {
                MainActivity.setListPadzeia()
                onSupportNavigateUp()
            } else {
                super.onBack()
            }
        }
    }

    override fun onDialogPositiveClick() {
        actionSaveRedak()
    }

    override fun onDialogNegativeClick() {
        actionCansel()
    }

    private fun changeSearchViewElements(view: View?) {
        if (view == null) return
        if (view.id == androidx.appcompat.R.id.search_edit_frame || view.id == androidx.appcompat.R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == androidx.appcompat.R.id.search_src_text) {
            autoCompleteTextView = view as AutoCompleteTextView
            val p = view.layoutParams as LinearLayout.LayoutParams
            val density = resources.displayMetrics.density
            val margin = (10 * density).toInt()
            p.rightMargin = margin
            autoCompleteTextView?.layoutParams = p
            autoCompleteTextView?.setBackgroundResource(R.drawable.underline_white)
            autoCompleteTextView?.addTextChangedListener(SearchViewTextWatcher())
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.sabytie, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(R.id.action_seashe_text)
        searchView = searchViewItem.actionView as SearchView
        if (actionExpandOn) {
            searchViewItem.expandActionView()
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                actionExpandOn = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                actionExpandOn = false
                return true
            }
        })
        searchView?.queryHint = getString(R.string.search_malitv)
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            searchViewItem.expandActionView()
            autoCompleteTextView?.setText(searchViewQwery)
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_add).isVisible = false
        menu.findItem(R.id.action_delite).isVisible = false
        menu.findItem(R.id.action_save).isVisible = false
        menu.findItem(R.id.action_save_redak).isVisible = false
        menu.findItem(R.id.action_cansel).isVisible = false
        menu.findItem(R.id.action_seashe_text).isVisible = false
        when (idMenu) {
            1 -> {
                menu.findItem(R.id.action_add).isVisible = true
                menu.findItem(R.id.action_delite).isVisible = true
                menu.findItem(R.id.action_seashe_text).isVisible = true
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
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SettingsActivity.isPadzeiaSetAlarm) return false
        if (!home) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false
            }
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val id = item.itemId
        c = Calendar.getInstance()
        val shakeanimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_settings) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SettingsActivity.notificationChannel(this, SettingsActivity.NOTIFICATION_CHANNEL_ID_SABYTIE)
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
                        MainActivity.toastView(this, getString(R.string.error_ch2))
                    }
                }
            } else {
                val settings = DialogSabytieSettings()
                settings.show(supportFragmentManager, "settings")
            }
            return true
        }
        if (id == R.id.action_save) {
            redak = true
            back = false
            val edit = binding.editText.text.toString().trim()
            var edit2 = binding.editText2.text.toString()
            da = binding.label1.text.toString()
            ta = binding.label2.text.toString()
            daK = binding.label12.text.toString()
            taK = binding.label22.text.toString()
            if (edit != "") {
                var londs: Long = 0
                var londs2: Long = 0
                val days = binding.label1.text.toString().split(".")
                val times = binding.label2.text.toString().split(":")
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
                                val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
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
                            time = binding.editText4.text.toString()
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
                                    val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
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
                            time = binding.editText4.text.toString()
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
                                        val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        } else {
                                            am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
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
                            time = binding.editText4.text.toString()
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
                                        val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                        } else {
                                            am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
                            val tim = time.split(".")
                            val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                            var resd = gc3[Calendar.WEEK_OF_YEAR] - dayof
                            if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                                var yeav = 365
                                if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                                resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                            }
                            leapYear = resd + 1
                        }
                        if (radio == 2) {
                            time = binding.editText4.text.toString()
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
                                    val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
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
                            time = binding.editText4.text.toString()
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
                                    val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
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
                            time = binding.editText4.text.toString()
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
                                    val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.labelbutton12.text.toString()
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
                            time = binding.editText4.text.toString()
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
                                    val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                            time = binding.editText4.text.toString()
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
                                    val intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
                outputStream.write(gson.toJson(MainActivity.padzeia, type))
                outputStream.close()
                MainActivity.padzeia.sort()
                if (binding.editText2.text.toString() != "") {
                    if (k.getBoolean("help_check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                }
                adapter.updateList(MainActivity.padzeia)
                binding.editText.text?.clear()
                binding.editText2.text?.clear()
                MainActivity.toastView(this, getString(R.string.save))
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
                binding.titleLayout.visibility = View.GONE
                binding.dragListView.visibility = View.VISIBLE
                idMenu = 1
                invalidateOptionsMenu()
            } else {
                binding.editText.startAnimation(shakeanimation)
            }
            return true
        }
        if (id == R.id.action_save_redak) {
            actionSaveRedak()
            return true
        }
        if (id == R.id.action_cansel) {
            actionCansel()
            return true
        }
        if (id == R.id.action_delite) {
            if (actionExpandOn) binding.toolbar.collapseActionView()
            val delite = DialogSabytieDelite()
            delite.show(supportFragmentManager, "delite")
            return true
        }
        if (id == R.id.action_add) {
            addSabytie()
            return true
        }
        return false
    }

    private fun actionSaveRedak() {
        redak = true
        back = false
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val p = MainActivity.padzeia[nomer]
        val edit = binding.editText.text.toString().trim()
        var edit2 = binding.editText2.text.toString()
        da = binding.label1.text.toString()
        ta = binding.label2.text.toString()
        daK = binding.label12.text.toString()
        taK = binding.label22.text.toString()
        if (edit != "") {
            var intent: Intent
            var pIntent: PendingIntent
            var londs: Long = 0
            var londs2: Long = 0
            val days = binding.label1.text.toString().split(".")
            val times = binding.label2.text.toString().split(":")
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
                if (p.padz == it.padz) {
                    del.add(it)
                    if (it.sec != "-1") {
                        intent = SettingsActivity.createIntentSabytie(it.padz, it.dat, it.tim)
                        val londs3 = it.paznic / 100000L
                        pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                }
            }
            MainActivity.padzeia.removeAll(del.toSet())
            when (repitL) {
                0 -> {
                    time = "0"
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                            pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                            } else {
                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
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
                        time = binding.editText4.text.toString()
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
                                intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
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
                        time = binding.editText4.text.toString()
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
                                    intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
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
                        time = binding.editText4.text.toString()
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
                                    intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                    pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
                        val tim = time.split(".")
                        val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                        var resd = gc3[Calendar.WEEK_OF_YEAR] - dayof
                        if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                            var yeav = 365
                            if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                            resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                        }
                        leapYear = resd + 1
                    }
                    if (radio == 2) {
                        time = binding.editText4.text.toString()
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
                                intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
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
                        time = binding.editText4.text.toString()
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
                                intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
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
                        time = binding.editText4.text.toString()
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
                                intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.labelbutton12.text.toString()
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
                        time = binding.editText4.text.toString()
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
                                intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
                        time = binding.editText4.text.toString()
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
                                intent = SettingsActivity.createIntentSabytie(edit, da, ta)
                                pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
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
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
            outputStream.write(gson.toJson(MainActivity.padzeia, type))
            outputStream.close()
            MainActivity.padzeia.sort()
            if (binding.editText2.text.toString() != "") {
                if (k.getBoolean("help_check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
                    val notifi = DialogHelpNotification()
                    notifi.show(supportFragmentManager, "help_notification")
                }
            }
            adapter.updateList(MainActivity.padzeia)
            binding.editText.text?.clear()
            binding.editText2.text?.clear()
            binding.spinner3.setSelection(0)
            binding.spinner4.setSelection(0)
            binding.spinner5.setSelection(0)
            binding.radioGroup.visibility = View.GONE
            var nol1 = ""
            var nol2 = ""
            c.add(Calendar.HOUR_OF_DAY, 1)
            if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
            if (c[Calendar.MONTH] < 9) nol2 = "0"
            da = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
            ta = "$timeH:00"
            binding.label1.text = da
            binding.label2.text = ta
            binding.label12.text = da
            binding.label22.text = ta
            MainActivity.toastView(this, getString(R.string.save))
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            binding.titleLayout.visibility = View.GONE
            binding.dragListView.visibility = View.VISIBLE
            idMenu = 1
            invalidateOptionsMenu()
        } else {
            val shakeanimation = AnimationUtils.loadAnimation(this, R.anim.shake)
            binding.editText.startAnimation(shakeanimation)
        }
        if (editCaliandar) {
            CaliandarFull.editCaliandarTitle = edit
            onSupportNavigateUp()
        }
    }

    private fun actionCansel() {
        back = false
        home = false
        c.add(Calendar.HOUR_OF_DAY, 1)
        var nol1 = ""
        var nol2 = ""
        if (c[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c[Calendar.MONTH] < 9) nol2 = "0"
        binding.editText.text?.clear()
        binding.editText2.text?.clear()
        binding.spinner3.setSelection(0)
        binding.spinner4.setSelection(0)
        binding.spinner5.setSelection(0)
        binding.radioGroup.visibility = View.GONE
        da = nol1 + c[Calendar.DAY_OF_MONTH] + "." + nol2 + (c[Calendar.MONTH] + 1) + "." + c[Calendar.YEAR]
        ta = "$timeH:00"
        binding.label1.text = da
        binding.label2.text = ta
        binding.label12.text = da
        binding.label22.text = ta
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        binding.titleLayout.visibility = View.GONE
        binding.dragListView.visibility = View.VISIBLE
        idMenu = 1
        invalidateOptionsMenu()
        if (editCaliandar) {
            onSupportNavigateUp()
        }
    }

    private fun getPadzeaiPosition(position: Int, padz: String, dat: String): Int {
        var pos = position
        if (actionExpandOn) {
            adapter.getOpigData().forEachIndexed { index, padzeia ->
                if (padzeia.padz == padz && padzeia.dat == dat) {
                    pos = index
                    return@forEachIndexed
                }
            }
        }
        return pos
    }

    private fun addSabytie() {
        if (actionExpandOn) binding.toolbar.collapseActionView()
        c = Calendar.getInstance()
        save = false
        back = true
        konec = false
        binding.checkBox2.isChecked = !konec
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
        binding.titleLayout.visibility = View.VISIBLE
        binding.dragListView.visibility = View.GONE
        binding.label1.text = da
        binding.label2.text = ta
        binding.label12.text = daK
        binding.label22.text = taK
        idMenu = 2
        binding.spinner4.setSelection(0)
        binding.spinner5.setSelection(0)
        color = 0
        editSave = binding.editText.text.toString().trim()
        edit2Save = binding.editText2.text.toString()
        daSave = binding.label1.text.toString()
        taSave = binding.label2.text.toString()
        daKSave = binding.label12.text.toString()
        taKSave = binding.label22.text.toString()
        invalidateOptionsMenu()
        binding.editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun sabytieDelAll() {
        if (SettingsActivity.isPadzeiaSetAlarm) return
        redak = true
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or 0
                } else {
                    0
                }
                for (p in MainActivity.padzeia) {
                    if (p.sec != "-1") {
                        val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                        val londs3 = p.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this@Sabytie, londs3.toInt(), intent, flags)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                }
                MainActivity.padzeia.clear()
                File("$filesDir/Sabytie.json").delete()
            }
            adapter.updateList(MainActivity.padzeia)
            MainActivity.toastView(this@Sabytie, getString(R.string.remove_padzea))
        }
    }

    override fun sabytieDelOld() {
        if (SettingsActivity.isPadzeiaSetAlarm) return
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val c2 = Calendar.getInstance()
        c2.set(Calendar.SECOND, 0)
        val del = ArrayList<Padzeia>()
        for (p in MainActivity.padzeia) {
            if (p.repit == 0) {
                val days = p.datK.split(".")
                val time = p.timK.split(":")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), time[0].toInt(), time[1].toInt(), 0)
                if (c2.timeInMillis >= gc.timeInMillis) {
                    if (p.sec != "-1") {
                        val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                        val londs3 = p.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this, londs3.toInt(), intent, flags)
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
                        val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                        val londs3 = p.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(this, londs3.toInt(), intent, flags)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                    del.add(p)
                }
            }
        }
        if (del.size != 0) {
            redak = true
            MainActivity.padzeia.removeAll(del.toSet())
            val outputStream = FileWriter("$filesDir/Sabytie.json")
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
            outputStream.write(gson.toJson(MainActivity.padzeia, type))
            outputStream.close()
            adapter.updateList(MainActivity.padzeia)
            binding.dragListView.recyclerView.scrollToPosition(0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("redak", redak)
        outState.putBoolean("save", save)
        outState.putBoolean("titleLayout", binding.titleLayout.visibility == View.VISIBLE)
        outState.putInt("idMenu", idMenu)
        outState.putString("ta", binding.label2.text.toString())
        outState.putString("da", binding.label1.text.toString())
        outState.putString("taK", binding.label22.text.toString())
        outState.putString("daK", binding.label12.text.toString())
        outState.putString("SearchViewQwery", autoCompleteTextView?.text.toString())
        outState.putBoolean("actionExpandOn", actionExpandOn)
        outState.putString("labelbutton12", binding.labelbutton12.text.toString())
    }

    private inner class SabytieAdapter(list: ArrayList<Padzeia>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<Padzeia, SabytieAdapter.ViewHolder>(), Filterable {
        private val day = Calendar.getInstance()
        private val origData = ArrayList(list)

        fun getOpigData() = origData

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemSabytieBinding.inflate(layoutInflater, parent, false)
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark2)
            } else {
                view.itemLayout.setBackgroundResource(R.drawable.selector_default_list)
                view.root.setBackgroundResource(R.color.colorDivider)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val padzeia = mItemList[position]
            val data = padzeia.dat.split(".")
            val gc = GregorianCalendar(data[2].toInt(), data[1].toInt() - 1, data[0].toInt())
            if (gc[Calendar.DAY_OF_YEAR] == day[Calendar.DAY_OF_YEAR] && gc[Calendar.YEAR] == day[Calendar.YEAR]) {
                holder.mText.typeface = MainActivity.createFont(Typeface.BOLD)
            } else {
                holder.mText.typeface = MainActivity.createFont(Typeface.NORMAL)
            }
            holder.mText.text = getString(R.string.sabytie_data_name, padzeia.dat, padzeia.padz)
            holder.color.setBackgroundColor(Color.parseColor(getColors(padzeia.color)))
            holder.buttonPopup.setOnClickListener {
                showPopupMenu(it, position)
            }
            holder.itemView.tag = padzeia
        }

        override fun getUniqueItemId(position: Int) = position.toLong()

        private fun showPopupMenu(view: View, position: Int) {
            val popup = PopupMenu(this@Sabytie, view)
            val infl = popup.menuInflater
            infl.inflate(R.menu.popup, popup.menu)
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
                if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
                spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                when (menuItem.itemId) {
                    R.id.menu_redoktor -> onDialogEditClick(position)
                    R.id.menu_remove -> onDialogDeliteClick(position)
                }
                true
            }
            popup.show()
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    var constraint1 = constraint
                    constraint1 = constraint1.toString()
                    val result = FilterResults()
                    if (constraint1.isNotEmpty()) {
                        val founded = ArrayList<Padzeia>()
                        for (item in origData) {
                            if (getString(R.string.sabytie_data_name, item.dat, item.padz).contains(constraint1, true)) {
                                founded.add(item)
                            }
                        }
                        result.values = founded
                        result.count = founded.size
                    } else {
                        result.values = origData
                        result.count = origData.size
                    }
                    return result
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    MainActivity.padzeia.clear()
                    for (item in results.values as ArrayList<*>) {
                        MainActivity.padzeia.add(item as Padzeia)
                    }
                    updateList(MainActivity.padzeia, false)
                }
            }
        }

        private inner class ViewHolder(itemView: ListItemSabytieBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            val color = itemView.color
            val buttonPopup = itemView.buttonPopup

            override fun onItemLongClicked(view: View): Boolean {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return true
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val contextMenuSabytie = DialogContextMenuSabytie.getInstance(bindingAdapterPosition, MainActivity.padzeia[bindingAdapterPosition].padz)
                contextMenuSabytie.show(supportFragmentManager, "context_menu_sabytie")
                return true
            }

            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val p = MainActivity.padzeia[bindingAdapterPosition]
                val title = p.padz
                val data = p.dat
                val time = p.tim
                val dataK = p.datK
                val timeK = p.timK
                val paz = p.paznic
                val konecSabytie = p.konecSabytie
                val color = p.color
                var res = getString(R.string.sabytie_no_pavedam)
                val gc = Calendar.getInstance()
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
                val dialogShowSabytie = DialogSabytieShow.getInstance(title, data, time, dataK, timeK, res, paznicia, !konecSabytie, color)
                dialogShowSabytie.show(supportFragmentManager, "sabytie")
            }
        }

        fun updateList(newSabytieDataAdapter: ArrayList<Padzeia>, updateList: Boolean = true) {
            if (updateList) {
                origData.clear()
                origData.addAll(newSabytieDataAdapter)
                if (k.getBoolean("help_sabytie_list_view", true) && origData.size > 1) {
                    val dialogHelpListView = DialogHelpListView()
                    dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
                    val prefEditor = k.edit()
                    prefEditor.putBoolean("help_sabytie_list_view", false)
                    prefEditor.apply()
                }
            }
            itemList = newSabytieDataAdapter
        }

        init {
            itemList = list
        }
    }

    private inner class ColorAdapter(context: Context) : ArrayAdapter<String>(context, R.layout.simple_list_item_color, R.id.label, colors) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderColor
            if (convertView == null) {
                val binding = SimpleListItemColorBinding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolderColor(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderColor
            }
            viewHolder.text.setBackgroundColor(Color.parseColor(getColors(position)))
            viewHolder.text.text = nazvaPadzei
            viewHolder.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            viewHolder.text.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorWhite))
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val text = view.findViewById<TextView>(R.id.label)
            text.setBackgroundColor(Color.parseColor(getColors(position)))
            text.text = nazvaPadzei
            text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            text.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorWhite))
            return view
        }
    }

    private inner class SpinnerAdapter(context: Context, list: Array<String>) : ArrayAdapter<String>(context, R.layout.simple_list_item_1, list) {
        private val spinnerList = list
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderColor
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolderColor(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderColor
            }
            viewHolder.text.text = spinnerList[position]
            viewHolder.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) viewHolder.text.setBackgroundResource(R.drawable.selector_dark)
            else viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val text = view as TextView
            text.text = spinnerList[position]
            text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) text.setBackgroundResource(R.drawable.selector_dark)
            else text.setBackgroundResource(R.drawable.selector_default)
            return view
        }
    }

    private class ViewHolderColor(var text: TextView)

    private inner class SearchViewTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            adapter.filter.filter(s)
        }
    }

    private inner class MyTextWatcher(private val editTextWatcher: EditText) : TextWatcher {
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (!am.canScheduleExactAlarms()) {
                                edit2Save = ""
                                binding.editText2.setText(edit2Save)
                                val dialogHelpAlarm = DialogHelpAlarm.getInstance(edit.toInt())
                                dialogHelpAlarm.show(supportFragmentManager, "dialogHelpAlarm")
                                return
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val permissionCheck = ContextCompat.checkSelfPermission(this@Sabytie, Manifest.permission.POST_NOTIFICATIONS)
                            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                                if (k.getBoolean("permissionNotificationApi33", true) && supportFragmentManager.findFragmentByTag("dialogHelpNotificationApi33") == null) {
                                    val dialogHelpNotificationApi33 = DialogHelpNotificationApi33()
                                    dialogHelpNotificationApi33.show(supportFragmentManager, "dialogHelpNotificationApi33")
                                }
                                binding.pavedamic3.visibility = View.VISIBLE
                            } else {
                                binding.pavedamic3.visibility = View.GONE
                            }
                        }
                        val days = binding.label1.text.toString().split(".")
                        val times = binding.label2.text.toString().split(":")
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
                        binding.pavedamic2.text = getString(R.string.sabytie_pavedam, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR], gc[Calendar.HOUR_OF_DAY], nol3, gc[Calendar.MINUTE])
                        val gcReal = Calendar.getInstance()
                        if (gcReal.timeInMillis > londs2) {
                            if (dzenNoch) binding.pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_black))
                            else binding.pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary))
                        } else {
                            if (dzenNoch) binding.pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorWhite))
                            else binding.pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_text))
                        }
                    } else {
                        binding.pavedamic2.text = getString(R.string.sabytie_no_pavedam)
                        if (dzenNoch) binding.pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorWhite))
                        else binding.pavedamic2.setTextColor(ContextCompat.getColor(this@Sabytie, R.color.colorPrimary_text))
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

    companion object {
        private val colors = Malitounik.applicationContext().resources.getStringArray(R.array.colors)
        var editCaliandar = false

        fun getColors(color: Int): String = colors[color]
    }
}