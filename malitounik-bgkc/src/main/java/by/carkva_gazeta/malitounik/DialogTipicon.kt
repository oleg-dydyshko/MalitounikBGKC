package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.tipicon.view.*

class DialogTipicon : DialogFragment() {
    private lateinit var alert: AlertDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val tipicon = arguments?.getInt("tipicon") ?: 0
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val builder = AlertDialog.Builder(it)
            val dialogView = View.inflate(it, R.layout.tipicon, null)
            builder.setView(dialogView)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            dialogView.t1.visibility = View.GONE
            dialogView.t2.visibility = View.GONE
            dialogView.t3.visibility = View.GONE
            dialogView.t10.visibility = View.GONE
            dialogView.t11.visibility = View.GONE
            dialogView.polosa.visibility = View.GONE
            if (tipicon == 1) dialogView.t7.visibility = View.VISIBLE else dialogView.t7.visibility = View.GONE
            if (tipicon == 2) dialogView.t5.visibility = View.VISIBLE else dialogView.t5.visibility = View.GONE
            if (tipicon == 3) dialogView.t6.visibility = View.VISIBLE else dialogView.t6.visibility = View.GONE
            if (tipicon == 4) dialogView.t8.visibility = View.VISIBLE else dialogView.t8.visibility = View.GONE
            if (tipicon == 5) dialogView.t9.visibility = View.VISIBLE else dialogView.t9.visibility = View.GONE
            dialogView.textView1.visibility = View.GONE
            if (tipicon == 0) {
                dialogView.t1.visibility = View.VISIBLE
                dialogView.t2.visibility = View.VISIBLE
                dialogView.t3.visibility = View.VISIBLE
                dialogView.t10.visibility = View.VISIBLE
                dialogView.t11.visibility = View.VISIBLE
                dialogView.polosa.visibility = View.VISIBLE
                dialogView.t5.visibility = View.VISIBLE
                dialogView.t6.visibility = View.VISIBLE
                dialogView.t7.visibility = View.VISIBLE
                dialogView.t8.visibility = View.VISIBLE
                dialogView.t9.visibility = View.VISIBLE
                dialogView.textView1.visibility = View.VISIBLE
            }
            dialogView.textView7.setPadding(0, 0, 0, 0)
            dialogView.textView8.text = MainActivity.fromHtml("<strong>Двунадзясятыя</strong><br> і вялікія сьвяты")
            dialogView.textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView6.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView7.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView8.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView9.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView10.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView11.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView12.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            dialogView.textView13.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                dialogView.imageView14.setImageResource(R.drawable.znaki_ttk_whate)
                dialogView.textView1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView2.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView3.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView4.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView5.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView6.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView7.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView8.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView9.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView10.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView11.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView12.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                dialogView.textView13.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                dialogView.line2.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                dialogView.image1.setImageResource(R.drawable.znaki_krest_v_kruge_black)
                dialogView.image2.setImageResource(R.drawable.znaki_krest_v_polukruge_black)
                dialogView.image3.setImageResource(R.drawable.znaki_krest_black)
                dialogView.image4.setImageResource(R.drawable.znaki_ttk_black_black)
                dialogView.image5.setImageResource(R.drawable.znaki_red_kub_black)
            }
            builder.setPositiveButton(it.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2)
            }
        }
        return alert
    }

    companion object {
        fun getInstance(tipicon: Int): DialogTipicon {
            val dialogTipicon = DialogTipicon()
            val bundle = Bundle()
            bundle.putInt("tipicon", tipicon)
            dialogTipicon.arguments = bundle
            return dialogTipicon
        }
    }
}