package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.nadsan_pravila3.*

class PsalterNadsana3 internal constructor(private val activity: Activity) : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.nadsan_pravila3, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val chin = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        textView1.setOnClickListener(this)
        textView3.setOnClickListener(this)
        textView5.setOnClickListener(this)
        textView6.setOnClickListener(this)
        textView7.setOnClickListener(this)
        textView8.setOnClickListener(this)
        textView9.setOnClickListener(this)
        textView10.setOnClickListener(this)
        textView11.setOnClickListener(this)
        textView12.setOnClickListener(this)
        textView13.setOnClickListener(this)
        textView14.setOnClickListener(this)
        textView15.setOnClickListener(this)
        textView16.setOnClickListener(this)
        textView17.setOnClickListener(this)
        textView18.setOnClickListener(this)
        textView19.setOnClickListener(this)
        textView20.setOnClickListener(this)
        textView21.setOnClickListener(this)
        textView22.setOnClickListener(this)
        textView23.setOnClickListener(this)
        textView24.setOnClickListener(this)
        textView25.setOnClickListener(this)
        textView26.setOnClickListener(this)
        textView27.setOnClickListener(this)
        textView28.setOnClickListener(this)
        if (dzenNoch) {
            t1.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t3.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t4.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t5.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t6.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t7.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t8.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t9.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t10.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t11.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t12.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t13.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView1.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView2.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView3.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView4.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView5.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView6.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView7.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView8.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView9.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView10.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView11.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView12.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView13.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView14.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView15.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView16.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView17.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView18.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView19.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView20.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView21.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView22.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView23.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView24.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView25.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView26.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView27.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            textView28.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
            t1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t6.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t7.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t8.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t9.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t10.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t11.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t12.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            t13.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView6.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView7.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView8.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView9.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView10.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView11.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView12.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView13.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView14.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView15.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView16.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView17.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView18.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView19.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView20.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView21.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView22.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView23.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView24.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView25.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView26.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView27.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            textView28.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
        }
    }

    override fun onClick(v: View?) {
        val intent = Intent(activity, NadsanContentActivity::class.java)
        var glava = 1
        when (v?.id ?: 0) {
            R.id.textView1 -> glava = 2
            R.id.textView5, R.id.textView15 -> glava = 4
            R.id.textView7, R.id.textView16 -> glava = 7
            R.id.textView8 -> glava = 10
            R.id.textView9 -> glava = 11
            R.id.textView10 -> glava = 14
            R.id.textView11 -> glava = 15
            R.id.textView12, R.id.textView23 -> glava = 19
            R.id.textView13 -> glava = 20
            R.id.textView14 -> glava = 3
            R.id.textView17 -> glava = 8
            R.id.textView19 -> glava = 9
            R.id.textView20 -> glava = 12
            R.id.textView21 -> glava = 13
            R.id.textView24 -> glava = 18
            R.id.textView25 -> glava = 16
            R.id.textView28 -> glava = 1
        }
        intent.putExtra("kafizma", glava)
        startActivity(intent)
    }

}