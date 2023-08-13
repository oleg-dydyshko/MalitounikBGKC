package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import by.carkva_gazeta.malitounik.databinding.MenuParafiiBgkcBinding

class MenuParafiiBgkc : BaseFragment() {
    private var mLastClickTime: Long = 0
    private val dzenNoch: Boolean
        get() = (requireActivity() as BaseActivity).getBaseDzenNoch()
    private lateinit var k: SharedPreferences
    private var _binding: MenuParafiiBgkcBinding? = null
    private val binding get() = _binding!!
    private val groups = ArrayList<ArrayList<MenuListData>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuParafiiBgkcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BaseActivity)?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (dzenNoch) binding.elvMain.selector = ContextCompat.getDrawable(activity, R.drawable.selector_dark)
            else binding.elvMain.selector = ContextCompat.getDrawable(activity, R.drawable.selector_default)
            binding.label.text = getString(R.string.bgkc_kuryia)
            binding.label.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                binding.label.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                binding.label.setBackgroundResource(R.drawable.selector_dark)
                binding.label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            }
            binding.label.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("autoscrollOFF", true)
                    intent.putExtra("title", binding.label.text)
                    intent.putExtra("resurs", "dzie_kuryja")
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
            }
            val children1 = ArrayList<MenuListData>()
            val children2 = ArrayList<MenuListData>()
            val children3 = ArrayList<MenuListData>()
            val children4 = ArrayList<MenuListData>()
            children1.add(MenuListData("Барысаў", "dzie_barysau"))
            children1.add(MenuListData("Жодзіна", "dzie_zodzina"))
            children1.add(MenuListData("Заслаўе", "dzie_zaslauje"))
            children1.add(MenuListData("Маладэчна", "dzie_maladechna"))
            children1.add(MenuListData("Мар\'іна Горка", "dzie_marjinahorka"))
            children1.add(MenuListData("Менск", "dzie_miensk"))
            children1.sort()
            children1.add(0, MenuListData("Цэнтральны дэканат", "dzie_centr_dekan"))
            groups.add(children1)
            children2.add(MenuListData("Віцебск", "dzie_viciebsk"))
            children2.add(MenuListData("Ворша", "dzie_vorsha"))
            children2.add(MenuListData("Гомель", "dzie_homel"))
            children2.add(MenuListData("Полацак", "dzie_polacak"))
            children2.add(MenuListData("Магілёў", "dzie_mahilou"))
            children2.sort()
            children2.add(0, MenuListData("Усходні дэканат", "dzie_usxod_dekan"))
            groups.add(children2)
            children3.add(MenuListData("Баранавічы", "dzie_baranavichy"))
            children3.add(MenuListData("Берасьце", "dzie_bierascie"))
            children3.add(MenuListData("Горадня", "dzie_horadnia"))
            children3.add(MenuListData("Івацэвічы", "dzie_ivacevichy"))
            children3.add(MenuListData("Ліда", "dzie_lida"))
            children3.add(MenuListData("Наваградак", "dzie_navahradak"))
            children3.add(MenuListData("Пінск", "dzie_pinsk"))
            children3.add(MenuListData("Слонім", "dzie_slonim"))
            children3.sort()
            children3.add(0, MenuListData("Заходні дэканат", "dzie_zaxod_dekan"))
            groups.add(children3)
            children4.add(MenuListData("Антвэрпан (Бельгія)", "dzie_antverpan"))
            children4.add(MenuListData("Лондан (Вялікабрытанія)", "dzie_londan"))
            children4.add(MenuListData("Варшава (Польшча)", "dzie_varshava"))
            children4.add(MenuListData("Вільня (Літва)", "dzie_vilnia"))
            children4.add(MenuListData("Вена (Аўстрыя)", "dzie_viena"))
            children4.add(MenuListData("Калінінград (Расея)", "dzie_kalininhrad"))
            children4.add(MenuListData("Прага (Чэхія)", "dzie_praha"))
            children4.add(MenuListData("Рым (Італія)", "dzie_rym"))
            children4.add(MenuListData("Санкт-Пецярбург (Расея)", "dzie_sanktpieciarburg"))
            children4.add(MenuListData("Беласток (Польшча)", "dzie_bielastok"))
            children4.add(MenuListData("Кракаў (Польшча)", "dzie_krakau"))
            children4.sort()
            groups.add(children4)
            val adapter = ExpListAdapterPrafiiBgkc(activity, groups)
            binding.elvMain.setAdapter(adapter)
            if (dzenNoch) {
                binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
                binding.elvMain.selector = ContextCompat.getDrawable(activity, R.drawable.selector_dark)
            } else {
                binding.elvMain.selector = ContextCompat.getDrawable(activity, R.drawable.selector_default)
            }
            binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnChildClickListener true
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("autoscrollOFF", true)
                    intent.putExtra("title", groups[groupPosition][childPosition].title)
                    intent.putExtra("resurs", groups[groupPosition][childPosition].resurs)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
                false
            }
        }
    }

    private class ExpListAdapterPrafiiBgkc(private val mContext: Activity, private val groups: ArrayList<ArrayList<MenuListData>>) : BaseExpandableListAdapter() {
        override fun getGroupCount(): Int {
            return groups.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return groups[groupPosition].size
        }

        override fun getGroup(groupPosition: Int): Any {
            return groups[groupPosition]
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return groups[groupPosition][childPosition]
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = GroupViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            rootView.textGroup.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val bgkc = mContext.resources.getStringArray(R.array.bgkc)
            rootView.textGroup.text = bgkc[groupPosition]
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            rootView.textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if ((mContext as BaseActivity).getBaseDzenNoch())
                rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition].title
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}