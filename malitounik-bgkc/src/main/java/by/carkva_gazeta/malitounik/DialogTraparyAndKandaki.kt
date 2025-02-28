package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik.databinding.TraparyAndKandakiBinding
import kotlinx.coroutines.Job
import java.util.Calendar


class DialogTraparyAndKandaki : DialogFragment() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<SlugbovyiaTextuData>()
    private var _binding: TraparyAndKandakiBinding? = null
    private val binding get() = _binding!!
    private lateinit var alert: AlertDialog
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        (activity as? SlugbovyiaTextu)?.let { activity ->
            chin = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            _binding = TraparyAndKandakiBinding.inflate(layoutInflater)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(activity, style)
            builder.setView(binding.root)
            if (dzenNoch) {
                binding.listView.selector = ContextCompat.getDrawable(activity, R.drawable.selector_dark)
            } else {
                binding.listView.selector = ContextCompat.getDrawable(activity, R.drawable.selector_default)
            }
            val ton = arguments?.getInt("ton", 0) ?: 0
            val denNedzeli = arguments?.getInt("denNedzeli", 0) ?: 0
            binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnItemClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val intent = Intent()
                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                intent.putExtra("resurs", data[position].resource)
                intent.putExtra("zmena_chastki", true)
                intent.putExtra("title", data[position].title)
                startActivity(intent)
            }
            val dayOfYear = arguments?.getInt("dayOfYear", 1) ?: 1
            val calendar = Calendar.getInstance()
            data.addAll(activity.loadSluzbaDayList(arguments?.getInt("slugbaType", SlugbovyiaTextu.VIACZERNIA) ?: SlugbovyiaTextu.VIACZERNIA, dayOfYear, arguments?.getInt("year", calendar[Calendar.YEAR]) ?: calendar[Calendar.YEAR]))
            if (ton != 0) data.add(SlugbovyiaTextuData(0, "Тон $ton", "ton$ton", SlugbovyiaTextu.VIACZERNIA))
            if (denNedzeli > Calendar.SUNDAY) {
                val listTonKognyDzen = activity.getTextTonNaKoznyDzenList()[denNedzeli - 2]
                data.add(SlugbovyiaTextuData(0, listTonKognyDzen.title, listTonKognyDzen.resurs, SlugbovyiaTextu.VIACZERNIA))
            }
            val adapter = TraparyAndKandakiAdaprer(activity, data)
            binding.listView.adapter = adapter
            alert = builder.create()
        }
        return alert
    }

    private class TraparyAndKandakiAdaprer(private val activity: BaseActivity, private val data: ArrayList<SlugbovyiaTextuData>) : ArrayAdapter<SlugbovyiaTextuData>(activity, R.layout.simple_list_item_2, R.id.label, data) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = activity.getBaseDzenNoch()
            viewHolder.text.text = data[position].title
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    companion object {
        fun getInstance(slugbaType: Int, dayOfYear: Int, ton: Int, denNedzeli: Int, year: Int): DialogTraparyAndKandaki {
            val bundle = Bundle()
             bundle.putInt("slugbaType", slugbaType)
            bundle.putInt("dayOfYear", dayOfYear)
            bundle.putInt("ton", ton)
            bundle.putInt("denNedzeli", denNedzeli)
            bundle.putInt("year", year)
            val trapary = DialogTraparyAndKandaki()
            trapary.arguments = bundle
            return trapary
        }
    }

    private class ViewHolder(var text: TextView)
}