package by.carkva_gazeta.resources

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class DialogAddNatatka : DialogFragment() {
    private lateinit var input: EditTextRobotoCondensed
    private var realpadding = 0
    private var dzenNoch = false
    private lateinit var alert: AlertDialog

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("value", input.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.add_maje_natatki_nazva)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linear.addView(textViewZaglavie)
            input = EditTextRobotoCondensed(it)
            if (dzenNoch) {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                input.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                input.setBackgroundResource(R.color.colorWhite)
            }
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            linear.addView(input)
            if (savedInstanceState != null) {
                val sValue = savedInstanceState.getString("value", "")
                input.setText(sValue)
            }
            input.requestFocus()
            input.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    write()
                    dialog?.cancel()
                }
                false
            }
            input.imeOptions = EditorInfo.IME_ACTION_GO
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            builder.setView(linear)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                write()
            }
            builder.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface?, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(input.windowToken, 0)
            }
            alert = builder.create()
        }
        return alert
    }

    private fun write() {
        activity?.let { activity ->
            var nazva = input.text.toString()
            var imiafile: String
            val natatka = arguments?.getString("copy") ?: ""
            val gc = Calendar.getInstance() as GregorianCalendar
            var i: Long = 1
            while (true) {
                imiafile = "Mae_malitvy_$i"
                val fileN = File(activity.filesDir.toString() + "/Malitva/$imiafile")
                if (fileN.exists()) {
                    i++
                } else {
                    break
                }
            }
            if (nazva == "") {
                val mun = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
                nazva = gc[Calendar.DATE].toString() + " " + mun[gc[Calendar.MONTH]] + " " + gc[Calendar.YEAR] + " " + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
            }
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val fileNatatka = File(activity.filesDir.toString() + "/Natatki.json")
            if (fileNatatka.exists()) {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<MyNatatkiFiles>>() {}.type
                    MenuNatatki.myNatatkiFiles = gson.fromJson(fileNatatka.readText(), type)
                } catch (t: Throwable) {
                    fileNatatka.delete()
                }
            }
            MenuNatatki.myNatatkiFiles.add(0, MyNatatkiFiles(i, gc.timeInMillis, nazva))
            val file = File(activity.filesDir.toString() + "/Malitva/$imiafile")
            MenuNatatki.myNatatkiFilesSort = k.getInt("natatki_sort", 0)
            MenuNatatki.myNatatkiFiles.sort()
            fileNatatka.writer().use {
                val gson = Gson()
                it.write(gson.toJson(MenuNatatki.myNatatkiFiles))
            }
            file.writer().use {
                it.write(nazva + "<MEMA></MEMA>" + MainActivity.fromHtml(natatka) + "<RTE></RTE>" + gc.timeInMillis)
            }
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
            MainActivity.toastView(activity, getString(R.string.copy_maje_natatki_nazva))
        }
    }

    companion object {
        fun getInstance(copy: String): DialogAddNatatka {
            val dialog = DialogAddNatatka()
            val bundle = Bundle()
            bundle.putString("copy", copy)
            dialog.arguments = bundle
            return dialog
        }
    }
}