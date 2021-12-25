package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Spanned
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.toSpanned
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik.databinding.TraparyAndKandakiBinding
import kotlinx.coroutines.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class TraparyAndKandaki : DialogFragment() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<Bogaslujbovyia>()
    private var _binding: TraparyAndKandakiBinding? = null
    private val binding get() = _binding!!
    private lateinit var alert: AlertDialog
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences

    companion object {
        fun getInstance(lityrgia: Int, title: String, mun: Int, day: Int, ton: Int, ton_naidzelny: Boolean, ton_na_sviaty: Boolean, ton_na_viliki_post: Boolean, resurs: String, sviatyiaName: String, checkSviatyia: Boolean, year: Int): TraparyAndKandaki {
            val bundle = Bundle()
            bundle.putInt("lityrgia", lityrgia)
            bundle.putString("title", title)
            bundle.putInt("mun", mun)
            bundle.putInt("day", day)
            bundle.putInt("year", year)
            bundle.putInt("ton", ton)
            bundle.putBoolean("ton_naidzelny", ton_naidzelny)
            bundle.putBoolean("ton_na_sviaty", ton_na_sviaty)
            bundle.putBoolean("ton_na_viliki_post", ton_na_viliki_post)
            bundle.putString("resurs", resurs)
            bundle.putString("sviatyiaName", sviatyiaName)
            bundle.putBoolean("checkSviatyia", checkSviatyia)
            val trapary = TraparyAndKandaki()
            trapary.arguments = bundle
            return trapary
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        activity?.let {
            chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            _binding = TraparyAndKandakiBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            if (dzenNoch) binding.listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            else binding.listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_default)
            val c = Calendar.getInstance()
            val lityrgia = arguments?.getInt("lityrgia", 4) ?: 4
            val title = arguments?.getString("title", "") ?: ""
            val mun = arguments?.getInt("mun", c[Calendar.MONTH] + 1) ?: c[Calendar.MONTH] + 1
            val day = arguments?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
            val year = arguments?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
            val ton = arguments?.getInt("ton", 1) ?: 1
            val tonNadzelny = arguments?.getBoolean("ton_naidzelny", true) ?: true
            val tonNaSviaty = arguments?.getBoolean("ton_na_sviaty", false) ?: false
            val tonNaVilikiPost = arguments?.getBoolean("ton_na_viliki_post", false) ?: false
            val resurs = arguments?.getString("resurs") ?: ""
            val sviatyiaName = arguments?.getString("sviatyiaName", "no_sviatyia") ?: "no_sviatyia"
            val checkSviatyia = arguments?.getBoolean("checkSviatyia", false) ?: false
            binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnItemClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                when {
                    data[position].sviatyia -> {
                        val i = Intent(it, Opisanie::class.java)
                        i.putExtra("mun", mun)
                        i.putExtra("day", day)
                        i.putExtra("year", year)
                        startActivity(i)
                    }
                    data[position].post -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("resurs", resurs)
                        intent.putExtra("zmena_chastki", true)
                        intent.putExtra("title", title)
                        startActivity(intent)
                    }
                    else -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("resurs", "ton$ton")
                        intent.putExtra("title", "Тон $ton")
                        intent.putExtra("zmena_chastki", true)
                        startActivity(intent)
                    }
                }
                dialog?.cancel()
            }
            if (tonNaSviaty) {
                if (title == "") data.add(Bogaslujbovyia(getString(R.string.trsviata).toSpanned(), lityrgia, sviata = true))
                else data.add(Bogaslujbovyia(title.toSpanned(), lityrgia, sviata = true))
            }
            if (tonNaVilikiPost) {
                data.add(Bogaslujbovyia(title.toSpanned(), lityrgia, post = true))
            }
            if (tonNadzelny) {
                data.add(Bogaslujbovyia("Тон $ton".toSpanned(), lityrgia))
            } else {
                when (ton) {
                    1 -> {
                        data.add(Bogaslujbovyia("ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам".toSpanned(), lityrgia))
                    }
                    2 -> {
                        data.add(Bogaslujbovyia("АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю".toSpanned(), lityrgia))
                    }
                    3 -> {
                        data.add(Bogaslujbovyia("СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу".toSpanned(), lityrgia))
                    }
                    4 -> {
                        data.add(Bogaslujbovyia("ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю".toSpanned(), lityrgia))
                    }
                    5 -> {
                        data.add(Bogaslujbovyia("ПЯТНІЦА\nСлужба Крыжу Гасподняму".toSpanned(), lityrgia))
                    }
                    6 -> {
                        data.add(Bogaslujbovyia("Субота\nСлужба ўсім сьвятым і памёрлым".toSpanned(), lityrgia))
                    }
                }
            }
            if (checkSviatyia) {
                data.add(Bogaslujbovyia(MainActivity.fromHtml(sviatyiaName), lityrgia, sviatyia = true))
            }
            val adapter = TraparyAndKandakiAdaprer(it, data)
            binding.listView.adapter = adapter
            alert = builder.create()
        }
        return alert
    }

    private class TraparyAndKandakiAdaprer(context: Context, item: ArrayList<Bogaslujbovyia>) : ArrayAdapter<Bogaslujbovyia>(context, R.layout.simple_list_item_2, R.id.label, item) {
        private val data = item

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val chin = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            viewHolder.text.text = data[position].title
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class Bogaslujbovyia(val title: Spanned, val lityrgia: Int, val sviata: Boolean = false, val post: Boolean = false, val sviatyia: Boolean = false)
}