package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.MyNatatkiBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Calendar

class MyNatatki : DialogFragment() {
    private var filename = ""
    private var redak = 3
    private var edit = true
    private var position = 0
    private var md5sum = ""
    private var _binding: MyNatatkiBinding? = null
    private val binding get() = _binding!!
    private var editDrawer: Drawable? = null
    private lateinit var alert: AlertDialog
    private var editSettings = false
    private var mListener: MyNatatkiListener? = null
    private var resetTollbarJob: Job? = null

    interface MyNatatkiListener {
        fun myNatatkiAdd()
        fun myNatatkiEdit(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as MyNatatkiListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement MyNatatkiListener")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        val oldredak = redak
        if (redak != 3) write()
        if (oldredak == 1 || oldredak == 2) mListener?.myNatatkiAdd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            md5sum = md5Sum("<MEMA></MEMA>")
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            _binding = MyNatatkiBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, style)
            builder.setView(binding.root)
            if (savedInstanceState != null) {
                filename = savedInstanceState.getString("filename") ?: ""
                redak = savedInstanceState.getInt("redak", 2)
                edit = savedInstanceState.getBoolean("edit", true)
                editSettings = savedInstanceState.getBoolean("editSettings", false)
                position = savedInstanceState.getInt("position", 0)
            } else {
                filename = arguments?.getString("filename") ?: ""
                redak = arguments?.getInt("redak", 3) ?: 3
                position = arguments?.getInt("position", 0) ?: 0
            }
            binding.EditText.tag = binding.EditText.keyListener
            binding.file.tag = binding.file.keyListener
            when (redak) {
                1 -> {
                    edit = false
                    binding.file.requestFocus()
                }
                2 -> {
                    edit = false
                    val res = File("${it.filesDir}/Malitva/$filename").readText().split("<MEMA></MEMA>").toTypedArray()
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
                }
                3 -> {
                    val res = File("${it.filesDir}/Malitva/$filename").readText().split("<MEMA></MEMA>").toTypedArray()
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
            builder.setPositiveButton(it.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setNeutralButton(getString(R.string.share)) { _: DialogInterface, _: Int ->
                write()
                prepareSave()
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, binding.EditText.text.toString())
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, binding.file.text.toString())
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, binding.file.text.toString()))
            }
            if (redak == 3) {
                builder.setNegativeButton(getString(R.string.redagaktirovat)) { _: DialogInterface, _: Int ->
                    mListener?.myNatatkiEdit(position)
                }
            }
            alert = builder.create()
        }
        binding.file.setOnClickListener {
            fullTextTollbar()
        }
        return alert
    }

    private fun fullTextTollbar() {
        resetTollbarJob?.cancel()
        if (binding.file.isSelected) {
            resetTollbar()
        } else {
            binding.file.isSingleLine = false
            binding.file.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar()
            }
        }
    }

    private fun resetTollbar() {
        binding.file.isSelected = false
        binding.file.isSingleLine = true
    }

    private fun prepareSave() {
        binding.EditText.keyListener = null
        binding.file.keyListener = null
        editDrawer = binding.EditText.background
        binding.EditText.isCursorVisible = false
        binding.EditText.setBackgroundResource(android.R.color.transparent)
        binding.file.isCursorVisible = false
        val imm = Malitounik.applicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.EditText.windowToken, 0)
    }

    private fun write() {
        val context = Malitounik.applicationContext()
        var nazva = binding.file.text.toString()
        var imiafile = "Mae_malitvy"
        val natatka = binding.EditText.text.toString()
        val gc = Calendar.getInstance()
        val editMd5 = md5Sum("$nazva<MEMA></MEMA>$natatka")
        var i: Long = 1
        if (md5sum != editMd5) {
            if (redak == 1) {
                while (true) {
                    imiafile = "Mae_malitvy_$i"
                    val fileN = File("${context.filesDir}/Malitva/$imiafile")
                    if (fileN.exists()) {
                        i++
                    } else {
                        break
                    }
                }
            }
            if (nazva == "") {
                val mun = resources.getStringArray(R.array.meciac_smoll)
                nazva = gc[Calendar.DATE].toString() + " " + mun[gc[Calendar.MONTH]] + " " + gc[Calendar.YEAR] + " " + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
            }
            val fileName = File("${context.filesDir}/Natatki.json")
            val file = if (redak == 2) {
                MenuNatatki.myNatatkiFiles.forEach {
                    val t1 = filename.lastIndexOf("_")
                    val id = filename.substring(t1 + 1).toLong()
                    if (it.id == id) {
                        it.title = nazva
                        return@forEach
                    }
                }
                File("${context.filesDir}/Malitva/$filename")
            } else {
                MenuNatatki.myNatatkiFiles.clear()
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
                MenuNatatki.myNatatkiFiles.addAll(gson.fromJson(fileName.readText(), type))
                MenuNatatki.myNatatkiFiles.add(0, MyNatatkiFiles(i, gc.timeInMillis, nazva))
                File("${context.filesDir}/Malitva/$imiafile")
            }
            fileName.writer().use {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
                it.write(gson.toJson(MenuNatatki.myNatatkiFiles, type))
            }
            file.writer().use {
                it.write(nazva + "<MEMA></MEMA>" + natatka + "<RTE></RTE>" + gc.timeInMillis)
            }
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.EditText.windowToken, 0)
            filename = file.name
            redak = 2
        }
    }

    private fun md5Sum(st: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(st.toByteArray())
        val digest = messageDigest.digest()
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
        outState.putBoolean("editSettings", editSettings)
        outState.putInt("position", position)
    }

    companion object {
        fun getInstance(filename: String, redak: Int, position: Int): MyNatatki {
            val myNatatki = MyNatatki()
            val bundle = Bundle()
            bundle.putString("filename", filename)
            bundle.putInt("redak", redak)
            bundle.putInt("position", position)
            myNatatki.arguments = bundle
            return myNatatki
        }
    }
}