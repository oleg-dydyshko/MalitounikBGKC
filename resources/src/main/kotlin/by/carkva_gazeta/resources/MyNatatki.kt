package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.KeyListener
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.databinding.MyNatatkiBinding
import com.google.gson.Gson
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class MyNatatki : AppCompatActivity(), DialogFontSize.DialogFontSizeListener {
    private var filename = ""
    private var redak = 3
    private var edit = true
    private var dzenNoch = false
    private var md5sum = ""
    private lateinit var binding: MyNatatkiBinding
    private var editDrawer: Drawable? = null
    private lateinit var k: SharedPreferences

    override fun onPause() {
        super.onPause()
        if (redak != 3) write()
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
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = MyNatatkiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        binding.EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (savedInstanceState != null) {
            filename = savedInstanceState.getString("filename") ?: ""
            redak = savedInstanceState.getInt("redak", 2)
            edit = savedInstanceState.getBoolean("edit", true)
        } else {
            filename = intent.getStringExtra("filename") ?: ""
            redak = intent.getIntExtra("redak", 2)
        }
        binding.file.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        binding.EditText.tag = binding.EditText.keyListener
        binding.file.tag = binding.file.keyListener
        binding.file.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.file.setTextCursorDrawable(by.carkva_gazeta.malitounik.R.color.colorWhite)
        } else {
            val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            f.isAccessible = true
            f.set(binding.file, 0)
        }
        when (redak) {
            1 -> {
                edit = false
                binding.file.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            2 -> {
                edit = false
                val res = File("$filesDir/Malitva/$filename").readText().split("<MEMA></MEMA>").toTypedArray()
                if (res[1].contains("<RTE></RTE>")) {
                    val start = res[1].indexOf("<RTE></RTE>")
                    res[1] = res[1].substring(0, start)
                    md5sum = md5Sum(res[0] + "<MEMA></MEMA>" + res[1].substring(0, start))
                } else {
                    md5sum = md5Sum(res[0] + "<MEMA></MEMA>" + res[1])
                }
                binding.EditText.setText(res[1])
                binding.file.setText(res[0])
                binding.file.setSelection(binding.file.text.toString().length)
                binding.EditText.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            3 -> {
                val res = File("$filesDir/Malitva/$filename").readText().split("<MEMA></MEMA>").toTypedArray()
                if (res[1].contains("<RTE></RTE>")) {
                    val start = res[1].indexOf("<RTE></RTE>")
                    res[1] = res[1].substring(0, start)
                    md5sum = md5Sum(res[0] + "<MEMA></MEMA>" + res[1].substring(0, start))
                } else {
                    md5sum = md5Sum(res[0] + "<MEMA></MEMA>" + res[1])
                }
                binding.file.setText(res[0])
                binding.EditText.setText(res[1])
                prepareSave()
            }
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    override fun onDialogFontSize(fontSize: Float) {
        binding.EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    }

    private fun prepareSave() {
        binding.EditText.keyListener = null
        binding.file.keyListener = null
        editDrawer = binding.EditText.background
        binding.EditText.isCursorVisible = false
        binding.file.isCursorVisible = false
        binding.EditText.setBackgroundResource(android.R.color.transparent)
        binding.file.setBackgroundResource(android.R.color.transparent)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.EditText.windowToken, 0)
    }

    private fun write() {
        var nazva = binding.file.text.toString()
        var imiafile = "Mae_malitvy"
        val natatka = binding.EditText.text.toString()
        val gc = Calendar.getInstance() as GregorianCalendar
        val editMd5 = md5Sum("$nazva<MEMA></MEMA>$natatka")
        var i: Long = 1
        if (md5sum != editMd5) {
            if (redak == 1) {
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
            val file = if (redak == 2) {
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
            imm.hideSoftInputFromWindow(binding.EditText.windowToken, 0)
            filename = file.name
            redak = 2
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
        outState.putInt("redak", redak)
        outState.putBoolean("edit", edit)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val editItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_edit)
        if (edit) {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
        } else {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.opisanie, menu)
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            val prefEditor = k.edit()
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_edit) {
            if (edit) {
                binding.EditText.keyListener = binding.EditText.tag as KeyListener
                binding.file.keyListener = binding.file.tag as KeyListener
                binding.EditText.background = editDrawer
                binding.file.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
                binding.EditText.requestFocus()
                binding.EditText.setSelection(binding.EditText.text.toString().length)
                binding.EditText.isCursorVisible = true
                binding.file.isCursorVisible = true
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                edit = false
                if (redak == 3)
                    redak = 2
            } else {
                write()
                prepareSave()
                edit = true
            }
            invalidateOptionsMenu()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            write()
            prepareSave()
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, binding.EditText.text.toString())
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, binding.file.text.toString())
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, binding.file.text.toString()))
        }
        return super.onOptionsItemSelected(item)
    }
}