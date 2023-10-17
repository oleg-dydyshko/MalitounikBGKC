package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import by.carkva_gazeta.malitounik.databinding.CytatyActivityBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*


class MenuPiarliny : BaseFragment() {

    private lateinit var k: SharedPreferences
    private val dzenNoch get() = (requireActivity() as BaseActivity).getBaseDzenNoch()
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private lateinit var adapterViewPager: MyPagerAdapter
    private var _binding: CytatyActivityBinding? = null
    private val binding get() = _binding!!
    private val piarliny = ArrayList<PiarlinyData>()
    private var piarlinyJob: Job? = null
    private val piarlinyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        piarlinyJob = CoroutineScope(Dispatchers.Main).launch {
            getPiarliny()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onDialogFontSize() {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CytatyActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            if (dzenNoch) {
                binding.tabLayout.setTabTextColors(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(it, R.color.colorSecondary_text))), Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(it, R.color.colorPrimary_black))))
            }
            binding.pager.offscreenPageLimit = 1
            piarlinyJob = CoroutineScope(Dispatchers.Main).launch {
                getPiarliny()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        piarlinyJob?.cancel()
        val edit = k.edit()
        edit.putInt("menuPiarlinyPage", binding.pager.currentItem)
        edit.apply()
    }

    private suspend fun getPiarliny() {
        activity?.let { fragmentActivity ->
            val localFile = File("${fragmentActivity.filesDir}/piarliny.json")
            if (MainActivity.isNetworkAvailable()) {
                val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
                pathReference.getFile(localFile).await()
            }
            if (localFile.exists()) {
                try {
                    val piarlin = ArrayList<ArrayList<String>>()
                    val builder = localFile.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    piarlin.addAll(gson.fromJson(builder, type))
                    piarliny.clear()
                    piarlin.forEach {
                        piarliny.add(PiarlinyData(it[0].toLong(), it[1]))
                    }
                    piarliny.sort()
                } catch (_: Throwable) {
                }
            }
            adapterViewPager = MyPagerAdapter(fragmentActivity)
            binding.pager.adapter = adapterViewPager
            TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
                val data = GregorianCalendar()
                data.timeInMillis = piarliny[position].time * 1000L
                tab.text = data[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[data[Calendar.MONTH]]
            }.attach()
            binding.pager.currentItem = k.getInt("menuPiarlinyPage", 0)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(childFragmentManager, "font")
            return true
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(childFragmentManager, "brightness")
            return true
        }
        if (id == R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            val prefEditor = k.edit()
            if (item.isChecked) {
                prefEditor?.putBoolean("dzen_noch", true)
            } else {
                prefEditor?.putBoolean("dzen_noch", false)
            }
            prefEditor?.apply()
            activity?.recreate()
            return true
        }
        if (id == R.id.action_carkva) {
            (activity as? BaseActivity)?.let { baseActivity ->
                if (baseActivity.checkmodulesAdmin()) {
                    val intent = Intent()
                    val pos = piarliny[binding.pager.currentItem]
                    intent.putExtra("time", pos.time * 1000)
                    intent.setClassName(baseActivity, MainActivity.ADMINPIARLINY)
                    piarlinyLauncher.launch(intent)
                } else {
                    MainActivity.toastView(baseActivity, getString(R.string.error))
                }
            }
            return true
        }
        return false
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val fragment = activity?.supportFragmentManager?.findFragmentByTag("f" + holder.itemId) as? MenuPiarlinyFragment
            fragment?.upDateTextView()
        }

        override fun getItemCount() = piarliny.size

        override fun createFragment(position: Int) = MenuPiarlinyFragment.newInstance(position)
    }

    private data class PiarlinyData(var time: Long, var data: String) : Comparable<PiarlinyData> {
        override fun compareTo(other: PiarlinyData): Int {
            if (this.time > other.time) {
                return 1
            } else if (this.time < other.time) {
                return -1
            }
            return 0
        }
    }
}