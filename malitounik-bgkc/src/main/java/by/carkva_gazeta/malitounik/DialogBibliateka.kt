package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class DialogBibliateka : DialogFragment() {
    private lateinit var list: ArrayList<String>
    private var fileExists = false
    private var mListener: DialogBibliatekaListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogBibliatekaListener {
        fun onDialogbibliatekaPositiveClick(listPosition: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = arguments?.getStringArrayList("list") ?: ArrayList()
        fileExists = arguments?.getBoolean("fileExists", false) ?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogBibliatekaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibliatekaListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            if (fileExists) {
                binding.title.text = getString(R.string.opisanie).uppercase()
            } else {
                binding.title.text = getString(R.string.download_file, "")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CoroutineScope(Dispatchers.Main).launch {
                        runCatching {
                            val format = withContext(Dispatchers.IO) {
                                val storageManager = it.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                                val bates = storageManager.getAllocatableBytes(storageManager.getUuidForPath(it.filesDir))
                                val bat = (bates.toFloat() / 1024).toDouble()
                                return@withContext when {
                                    bat < 10000f -> getString(R.string.dastupna_bat, formatFigureTwoPlaces(BigDecimal(bat).setScale(2, RoundingMode.HALF_EVEN).toFloat()))
                                    bates < 1000L -> getString(R.string.dastupna_bates, bates)
                                    else -> ""
                                }
                            }
                            _binding?.title?.text = getString(R.string.download_file, format)
                        }
                    }
                }
            }
            binding.content.text = MainActivity.fromHtml(list[1])
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            val dirCount = list[3].toInt()
            val izm = if (dirCount / 1024 > 1000) {
                formatFigureTwoPlaces(BigDecimal.valueOf(dirCount.toFloat() / 1024 / 1024.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()) + " Мб"
            } else {
                formatFigureTwoPlaces(BigDecimal.valueOf(dirCount.toFloat() / 1024.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()) + " Кб"
            }
            if (fileExists) {
                builder.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            } else {
                if (MainActivity.isNetworkAvailable()) {
                    builder.setPositiveButton(getString(R.string.download_bibliateka_file, izm)) { dialog: DialogInterface, _: Int ->
                        mListener?.onDialogbibliatekaPositiveClick(list[2])
                        dialog.cancel()
                    }
                    builder.setNegativeButton(R.string.cansel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                } else {
                    builder.setPositiveButton(getString(R.string.no_internet)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                }
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun formatFigureTwoPlaces(value: Float): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value.toDouble())
    }

    companion object {
        fun getInstance(list: ArrayList<String>, fileExists: Boolean): DialogBibliateka {
            val instance = DialogBibliateka()
            val args = Bundle()
            args.putStringArrayList("list", list)
            args.putBoolean("fileExists", fileExists)
            instance.arguments = args
            return instance
        }
    }
}