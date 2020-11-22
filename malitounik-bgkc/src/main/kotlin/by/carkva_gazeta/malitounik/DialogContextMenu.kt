package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogContextMenu : DialogFragment() {
    private var position = 0
    private var name: String = ""
    private lateinit var mListener: DialogContextMenuListener
    private lateinit var dialog: AlertDialog

    interface DialogContextMenuListener {
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
                context as DialogContextMenuListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogContextMenuListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = name
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = getString(R.string.redagaktirovat)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                textView.setBackgroundResource(R.drawable.selector_dark)
            } else {
                textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView.setBackgroundResource(R.drawable.selector_default)
            }
            linearLayout.addView(textView)
            val textView2 = TextViewRobotoCondensed(it)
            textView2.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView2.text = getString(R.string.delite)
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                textView2.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                textView2.setBackgroundResource(R.drawable.selector_dark)
            } else {
                textView2.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView2.setBackgroundResource(R.drawable.selector_default)
            }
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
        fun getInstance(position: Int, name: String): DialogContextMenu {
            val dialogContextMenu = DialogContextMenu()
            val args = Bundle()
            args.putInt("position", position)
            args.putString("name", name)
            dialogContextMenu.arguments = args
            return dialogContextMenu
        }
    }
}