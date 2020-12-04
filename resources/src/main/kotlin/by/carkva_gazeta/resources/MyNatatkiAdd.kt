package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuNatatki
import by.carkva_gazeta.malitounik.MyNatatkiFiles
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.my_malitva_add.*
import kotlinx.android.synthetic.main.my_malitva_add.title_toolbar
import kotlinx.android.synthetic.main.my_malitva_add.toolbar
import kotlinx.android.synthetic.main.my_malitva_view.*
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class MyNatatkiAdd : AppCompatActivity() {
    private var filename = ""
    private var redak = false
    private var dzenNoch = false
    private var md5sum = ""

    override fun onPause() {
        super.onPause()
        write()
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        md5sum = md5Sum("<MEMA></MEMA>")
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.my_malitva_add)
        var title = resources.getString(by.carkva_gazeta.malitounik.R.string.natatka_add)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (savedInstanceState != null) {
            filename = savedInstanceState.getString("filename") ?: ""
            redak = savedInstanceState.getBoolean("redak", false)
        } else {
            filename = intent.getStringExtra("filename") ?: ""
            redak = intent.getBooleanExtra("redak", false)
        }
        file.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (redak) {
            title = resources.getString(by.carkva_gazeta.malitounik.R.string.natatka_edit)
            val res = File("$filesDir/Malitva/$filename").readText().split("<MEMA></MEMA>").toTypedArray()
            if (res[1].contains("<RTE></RTE>")) {
                val start = res[1].indexOf("<RTE></RTE>")
                res[1] = res[1].substring(0, start)
                md5sum = md5Sum(res[0] + "<MEMA></MEMA>" + res[1].substring(0, start))
            } else {
                md5sum = md5Sum(res[0] + "<MEMA></MEMA>" + res[1])
            }
            EditText.setText(res[1])
            file.setText(res[0])
        }
        file.setSelection(file.text.toString().length)
        setTollbarTheme(title)
    }

    private fun setTollbarTheme(title: String) {
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
        title_toolbar.text = title
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun write() {
        var nazva = file.text.toString()
        var imiafile = "Mae_malitvy"
        val natatka = EditText.text.toString()
        val gc = Calendar.getInstance() as GregorianCalendar
        val editMd5 = md5Sum("$nazva<MEMA></MEMA>$natatka")
        var i: Long = 1
        if (md5sum != editMd5) {
            if (!redak) {
                while (true) {
                    imiafile = "Mae_malitvy_$i"
                    val fileN = File("$filesDir/Malitva/$imiafile")
                    if (fileN.exists()) {
                        i++
                    } else {
                        break
                    }
                }
            }
            if (nazva == "") {
                val mun = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
                nazva = gc[Calendar.DATE].toString() + " " + mun[gc[Calendar.MONTH]] + " " + gc[Calendar.YEAR] + " " + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
            }
            val file = if (redak) {
                MenuNatatki.myNatatkiFiles.forEach {
                    val t1 = filename.lastIndexOf("_")
                    val id = filename.substring(t1 + 1).toLong()
                    if (it.id == id) {
                        it.title = nazva
                        return@forEach
                    }
                }
                File("$filesDir/Malitva/$filename")
            } else {
                MenuNatatki.myNatatkiFiles.add(0, MyNatatkiFiles(i, gc.timeInMillis, nazva))
                File("$filesDir/Malitva/$imiafile")
            }
            val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
            MenuNatatki.myNatatkiFilesSort = k.getInt("natatki_sort", 0)
            MenuNatatki.myNatatkiFiles.sort()
            val fileName = File("$filesDir/Natatki.json")
            fileName.writer().use {
                val gson = Gson()
                it.write(gson.toJson(MenuNatatki.myNatatkiFiles))
            }
            file.writer().use {
                it.write(nazva + "<MEMA></MEMA>" + natatka + "<RTE></RTE>" + gc.timeInMillis)
            }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(EditText.windowToken, 0)
            redak = true
            filename = file.name
        }
    }

    private fun md5Sum(st: String): String {
        val digest: ByteArray
        val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(st.toByteArray())
        digest = messageDigest.digest()
        val bigInt = BigInteger(1, digest)
        val md5Hex = StringBuilder(bigInt.toString(16))
        while (md5Hex.length < 32) {
            md5Hex.insert(0, "0")
        }
        return md5Hex.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("filename", filename)
        outState.putBoolean("redak", redak)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.opisanie, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, EditText.text.toString())
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, title_toolbar.text.toString())
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, title_toolbar.text.toString()))
        }
        return super.onOptionsItemSelected(item)
    }
}