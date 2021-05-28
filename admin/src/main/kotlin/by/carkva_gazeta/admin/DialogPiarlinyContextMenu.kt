package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity

class DialogPiarlinyContextMenu : DialogFragment() {
    private var position = 0
    private var name: String = ""
    private lateinit var mListener: DialogPiarlinyContextMenuListener
    private lateinit var dialog: AlertDialog

    interface DialogPiarlinyContextMenuListener {
        fun onDialogEditClick(position: Int)
        fun onDialogDeliteClick(position: Int, name: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
        name = arguments?.getString("name") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPiarlinyContextMenuListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPiarlinyContextMenuListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = name
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextView(it)
            textView.typeface = MainActivity.createFont(Typeface.NORMAL)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = getString(by.carkva_gazeta.malitounik.R.string.redagaktirovat)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            linearLayout.addView(textView)
            val textView2 = TextView(it)
            textView2.typeface = MainActivity.createFont(Typeface.NORMAL)
            textView2.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView2.text = getString(by.carkva_gazeta.malitounik.R.string.delite)
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView2.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            textView2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            linearLayout.addView(textView2)
            builder.setView(linearLayout)
            dialog = builder.create()
            textView.setOnClickListener {
                dialog.cancel()
                mListener.onDialogEditClick(position)
            }
            textView2.setOnClickListener {
                dialog.cancel()
                mListener.onDialogDeliteClick(position, name)
            }
        }
        return dialog
    }

    companion object {
        fun getInstance(position: Int, name: String): DialogPiarlinyContextMenu {
            val dialogContextMenu = DialogPiarlinyContextMenu()
            val args = Bundle()
            args.putInt("position", position)
            args.putString("name", name)
            dialogContextMenu.arguments = args
            return dialogContextMenu
        }
    }
}