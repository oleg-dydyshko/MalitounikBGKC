package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.tipicon.*

class DialogTipicon : DialogFragment() {
    private lateinit var alert: AlertDialog
    private lateinit var rootView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val tipicon = arguments?.getInt("tipicon") ?: 0
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            t1.visibility = View.GONE
            t2.visibility = View.GONE
            t3.visibility = View.GONE
            t10.visibility = View.GONE
            t11.visibility = View.GONE
            polosa.visibility = View.GONE
            if (tipicon == 1) t7.visibility = View.VISIBLE else t7.visibility = View.GONE
            if (tipicon == 2) t5.visibility = View.VISIBLE else t5.visibility = View.GONE
            if (tipicon == 3) t6.visibility = View.VISIBLE else t6.visibility = View.GONE
            if (tipicon == 4) t8.visibility = View.VISIBLE else t8.visibility = View.GONE
            if (tipicon == 5) t9.visibility = View.VISIBLE else t9.visibility = View.GONE
            textView1.visibility = View.GONE
            if (tipicon == 0) {
                t1.visibility = View.VISIBLE
                t2.visibility = View.VISIBLE
                t3.visibility = View.VISIBLE
                t10.visibility = View.VISIBLE
                t11.visibility = View.VISIBLE
                polosa.visibility = View.VISIBLE
                t5.visibility = View.VISIBLE
                t6.visibility = View.VISIBLE
                t7.visibility = View.VISIBLE
                t8.visibility = View.VISIBLE
                t9.visibility = View.VISIBLE
                textView1.visibility = View.VISIBLE
            }
            textView7.setPadding(0, 0, 0, 0)
            textView8.text = MainActivity.fromHtml("<strong>Двунадзясятыя</strong><br> і вялікія сьвяты")
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView6.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView7.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView8.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView9.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView10.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView11.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView12.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView13.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                imageView14.setImageResource(R.drawable.znaki_ttk_whate)
                textView1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView2.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView3.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView4.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView5.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView6.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView7.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView8.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView9.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView10.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView11.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView12.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                textView13.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                line2.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                image1.setImageResource(R.drawable.znaki_krest_v_kruge_black)
                image2.setImageResource(R.drawable.znaki_krest_v_polukruge_black)
                image3.setImageResource(R.drawable.znaki_krest_black)
                image4.setImageResource(R.drawable.znaki_ttk_black_black)
                image5.setImageResource(R.drawable.znaki_red_kub_black)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            rootView = View.inflate(it, R.layout.tipicon, null)
            builder.setView(rootView)
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