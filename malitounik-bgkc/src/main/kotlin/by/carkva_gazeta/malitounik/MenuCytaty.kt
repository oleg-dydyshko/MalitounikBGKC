package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import by.carkva_gazeta.malitounik.databinding.CytatyActivityBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.ceil


class MenuCytaty : BaseFragment() {

    private lateinit var k: SharedPreferences
    private var citataFileCount: Double = 0.0
    private val dzenNoch get() = (requireActivity() as BaseActivity).getBaseDzenNoch()
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private lateinit var adapterViewPager: MyPagerAdapter
    private var _binding: CytatyActivityBinding? = null
    private val binding get() = _binding!!

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
            val inputStream = resources.openRawResource(R.raw.citata)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            reader.forEachLine {
                citataFileCount++
            }
            adapterViewPager = MyPagerAdapter(it)
            binding.pager.adapter = adapterViewPager
            TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
                tab.text = getString(R.string.cytaty_staronka, position + 1)
            }.attach()
            if (dzenNoch) {
                binding.tabLayout.setTabTextColors(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(it, R.color.colorSecondary_text))), Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(it, R.color.colorPrimary_black))))
            }
            binding.pager.offscreenPageLimit = 1
            binding.pager.currentItem = k.getInt("menuCytatyPage", 0)
        }
    }

    override fun onPause() {
        super.onPause()
        val edit = k.edit()
        edit.putInt("menuCytatyPage", binding.pager.currentItem)
        edit.apply()
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
                    intent.setClassName(baseActivity, MainActivity.PASOCHNICALIST)
                    val idres = R.raw.citata
                    val inputStream = resources.openRawResource(idres)
                    val text = inputStream.use {
                        it.reader().readText()
                    }
                    intent.putExtra("resours", "citata")
                    intent.putExtra("title", getString(R.string.cytaty_z_biblii))
                    intent.putExtra("text", text)
                    startActivity(intent)
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
            val fragment = activity?.supportFragmentManager?.findFragmentByTag("f" + holder.itemId) as? MenuCytatyFragment
            fragment?.upDateTextView()
        }

        override fun getItemCount() = ceil(citataFileCount / 10).toInt()

        override fun createFragment(position: Int) = MenuCytatyFragment.newInstance(position)
    }
}