package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import by.carkva_gazeta.malitounik.databinding.CalindarGridBinding
import by.carkva_gazeta.malitounik.databinding.GridItemBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import java.util.*

class DialogCalindarGrid : DialogFragment() {

    companion object {
        fun getInstance(post: Int, ton: Int, denNedzeli: Int, data: Int, mun: Int, raznicia: Int, svityiaName: String, year: Int, datareal: Int, munreal: Int, dayOfYear: String): DialogCalindarGrid {
            val bundle = Bundle()
            bundle.putInt("post", post)
            bundle.putInt("ton", ton)
            bundle.putInt("denNedzeli", denNedzeli)
            bundle.putInt("data", data)
            bundle.putInt("mun", mun)
            bundle.putInt("year", year)
            bundle.putInt("raznicia", raznicia)
            bundle.putString("svityiaName", svityiaName)
            bundle.putInt("datareal", datareal)
            bundle.putInt("munreal", munreal)
            bundle.putString("dayOfYear", dayOfYear)
            val dialog = DialogCalindarGrid()
            dialog.arguments = bundle
            return dialog
        }
    }

    private lateinit var alert: AlertDialog
    private var _binding: CalindarGridBinding? = null
    private val binding get() = _binding!!
    private var mLastClickTime: Long = 0
    private var post = 0
    private var ton = 0
    private var denNedzeli = Calendar.SUNDAY
    private var data = 1
    private var mun = 1
    private var datareal = 1
    private var munreal = 1
    private var year = 2020
    private var dayOfYear = "0"
    private var raznicia = 400
    private var issetSvityia = true
    private var sviatyaName = "no_sviatyia"
    private var mItemArray = ArrayList<Int>()
    private val slugba = SlugbovyiaTextu()

    private fun getImage(id: Int, imageWhite: Boolean = false, imageSecondary: Boolean = false): Int {
        if (id == 1) {
            if (imageWhite) return R.drawable.moon2_white
            if (imageSecondary) return 0
            return R.drawable.moon2_black
        }
        if (id == 2) {
            if (imageWhite) return R.drawable.moon_white
            if (imageSecondary) return R.drawable.moon_secondary
            return R.drawable.moon_black
        }
        if (id == 3) {
            if (imageWhite) return R.drawable.sun2_white
            if (imageSecondary) return R.drawable.sun2_secondary
            return R.drawable.sun2_black
        }
        if (id == 4) {
            if (imageWhite) return R.drawable.sun_white
            if (imageSecondary) return R.drawable.sun_secondary
            return R.drawable.sun_black
        }
        if (id == 5) {
            if (imageWhite) return R.drawable.clock_white
            if (imageSecondary) return R.drawable.clock_secondary
            return R.drawable.clock_black
        }
        if (id == 6) {
            if (imageWhite) return R.drawable.carkva_white
            if (imageSecondary) return R.drawable.carkva_secondary
            return R.drawable.carkva_black
        }
        if (id == 7) {
            if (imageWhite) return R.drawable.man_white
            if (imageSecondary) return R.drawable.man_secondary
            return R.drawable.man_black
        }
        if (id == 8) {
            if (imageWhite) return R.drawable.book_white
            if (imageSecondary) return R.drawable.book_secondary
            return R.drawable.book_black
        }
        if (id == 9) {
            if (imageWhite) return R.drawable.kanon_white
            if (imageSecondary) return R.drawable.kanon_secondary
            return R.drawable.kanon_black
        }
        return 0
    }

    private fun getTitle(id: Int): String {
        when (id) {
            1 -> return getString(R.string.viachernia)
            2 -> return getString(R.string.raviachernica)
            3 -> return getString(R.string.paunochnica)
            4 -> return getString(R.string.utran)
            5 -> return getString(R.string.gadziny)
            6 -> return getString(R.string.liturgia)
            7 -> return getString(R.string.jyci)
            8 -> return getString(R.string.piarliny)
            9 -> return getString(R.string.ustau)
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slugba.onDestroy()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        activity?.let {
            _binding = CalindarGridBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            slugba.loadPiarliny()
            alert = builder.create()
            post = arguments?.getInt("post") ?: 0
            ton = arguments?.getInt("ton") ?: 0
            denNedzeli = arguments?.getInt("denNedzeli") ?: 1
            data = arguments?.getInt("data") ?: 1
            mun = arguments?.getInt("mun") ?: 1
            datareal = arguments?.getInt("datareal") ?: 1
            dayOfYear = arguments?.getString("dayOfYear") ?: "0"
            munreal = arguments?.getInt("munreal") ?: 1
            year = arguments?.getInt("year", year) ?: 2020
            raznicia = arguments?.getInt("raznicia", 400) ?: 400
            sviatyaName = arguments?.getString("svityiaName", "no_sviatyia") ?: "no_sviatyia"
            issetSvityia = sviatyaName.contains("no_sviatyia")
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getString("caliandarGrid", "") != "") {
                try {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(ArrayList::class.java, Integer::class.java).type
                    mItemArray = gson.fromJson(k.getString("caliandarGrid", ""), type)
                } catch (e: Throwable) {
                    val edit = k.edit()
                    edit.remove("caliandarGrid")
                    edit.apply()
                    for (i in 1..9) mItemArray.add(i)
                }
            } else {
                for (i in 1..9) mItemArray.add(i)
            }
            binding.dragGridView.setLayoutManager(GridLayoutManager(it, 3))
            val listAdapter = ItemAdapter(it, mItemArray, R.id.item_layout, true)
            binding.dragGridView.setAdapter(listAdapter, true)
            binding.dragGridView.setCanDragHorizontally(true)
            binding.dragGridView.setCanDragVertically(true)
            binding.dragGridView.setCustomDragItem(null)
            binding.dragGridView.setDragListListener(object : DragListView.DragListListener {
                override fun onItemDragStarted(position: Int) {
                }

                override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
                }

                override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                    if (fromPosition != toPosition) {
                        val gson = Gson()
                        val edit = it.getSharedPreferences("biblia", Context.MODE_PRIVATE).edit()
                        edit.putString("caliandarGrid", gson.toJson(mItemArray))
                        edit.apply()
                    }
                }
            })
        }
        return alert
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private inner class ItemAdapter(private val activity: Activity, list: ArrayList<Int>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<Int, ItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            when (post) {
                1 -> view.itemLayout.setBackgroundResource(R.drawable.selector_grid_bez_posta)
                2 -> view.itemLayout.setBackgroundResource(R.drawable.selector_grid_post)
                3 -> {
                    view.itemLayout.setBackgroundResource(R.drawable.selector_grid_strogi_post)
                    view.text.setTextColor(ContextCompat.getColor(parent.context, R.color.colorWhite))
                }
                4 -> {
                    if (dzenNoch) view.itemLayout.setBackgroundResource(R.drawable.selector_grid_red_dark)
                    else view.itemLayout.setBackgroundResource(R.drawable.selector_grid_red)
                    view.text.setTextColor(ContextCompat.getColor(parent.context, R.color.colorWhite))
                }
                else -> view.itemLayout.setBackgroundResource(R.drawable.selector_grid_gray)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = getTitle(mItemList[position])
            holder.mText.text = text
            holder.itemView.tag = mItemList[position]
            when {
                mItemList[position] == 4 && !(slugba.checkUtran(raznicia, dayOfYear.toInt()) || denNedzeli == Calendar.SUNDAY) -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 7 && issetSvityia -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 8 && !slugba.checkParliny(data, mun) -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 2 && !(slugba.checkPavichrrnica(raznicia, dayOfYear.toInt())) -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 5 && !slugba.checkVialikiaGadziny(raznicia) -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 3 && !(slugba.checkPaunochnica(raznicia, dayOfYear.toInt())) -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 9 -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                mItemList[position] == 6 && !(slugba.checkLiturgia(raznicia, dayOfYear.toInt()) || (ton == 0 && denNedzeli != Calendar.SUNDAY) || ton != 0) -> {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                }
                else -> {
                    if (post == 3 || post == 4) {
                        holder.mImage.setImageResource(getImage(mItemList[position], imageWhite = true))
                    } else {
                        holder.mImage.setImageResource(getImage(mItemList[position]))
                    }
                }
            }
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].toLong()
        }

        private inner class ViewHolder(itemView: GridItemBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            var mImage = itemView.image
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (itemList[adapterPosition].toInt() == 7) {
                    if (!issetSvityia) {
                        val i = Intent(activity, Opisanie::class.java)
                        i.putExtra("mun", munreal)
                        i.putExtra("day", datareal)
                        i.putExtra("year", year)
                        startActivity(i)
                    }
                    return
                }
                if (itemList[adapterPosition].toInt() == 8) {
                    if (slugba.checkParliny(data, mun)) {
                        val i = Intent(activity, Piarliny::class.java)
                        i.putExtra("mun", munreal)
                        i.putExtra("day", datareal)
                        startActivity(i)
                    }
                    return
                }
                if (!MainActivity.checkmoduleResources()) {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                    return
                }
                when (itemList[adapterPosition].toInt()) {
                    1 -> {
                        when {
                            slugba.checkViachernia(raznicia, dayOfYear.toInt()) -> {
                                val intent = Intent()
                                val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.VIACHERNIA)
                                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("resurs", resours)
                                intent.putExtra("zmena_chastki", true)
                                intent.putExtra("title", slugba.getTitle(resours))
                                startActivity(intent)
                            }
                            else -> {
                                val intent = Intent(activity, SubMenuBogashlugbovyaViachernia::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                    2 -> {
                        when {
                            slugba.checkPavichrrnica(raznicia, dayOfYear.toInt()) -> {
                                val intent = Intent()
                                val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.PAVIACHERNICA)
                                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("resurs", resours)
                                intent.putExtra("zmena_chastki", true)
                                intent.putExtra("title", slugba.getTitle(resours))
                                startActivity(intent)
                            }
                        }
                    }
                    3 -> {
                        when {
                            slugba.checkPaunochnica(raznicia, dayOfYear.toInt()) -> {
                                val intent = Intent()
                                val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.PAUNOCHNICA)
                                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("resurs", resours)
                                intent.putExtra("zmena_chastki", true)
                                intent.putExtra("title", slugba.getTitle(resours))
                                startActivity(intent)
                            }
                        }
                    }
                    5 -> {
                        when {
                            slugba.checkVialikiaGadziny(raznicia) -> {
                                val intent = Intent()
                                val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.VIALIKIAGADZINY)
                                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("resurs", resours)
                                intent.putExtra("zmena_chastki", true)
                                intent.putExtra("title", slugba.getTitle(resours))
                                startActivity(intent)
                            }
                        }
                    }
                    6 -> {
                        when {
                            slugba.checkLiturgia(raznicia, dayOfYear.toInt()) -> {
                                if (denNedzeli == Calendar.SUNDAY && ton != 0) {
                                    val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.LITURGIA)
                                    val traparyAndKandaki = DialogTraparyAndKandaki.getInstance(4, slugba.getTitle(resours), ton, true, ton_na_sviaty = false, ton_na_viliki_post = true, resurs = resours)
                                    traparyAndKandaki.show(childFragmentManager, "traparyAndKandaki")
                                } else {
                                    val intent = Intent()
                                    val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.LITURGIA)
                                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                    intent.putExtra("resurs", resours)
                                    intent.putExtra("zmena_chastki", true)
                                    intent.putExtra("title", slugba.getTitle(resours))
                                    startActivity(intent)
                                }
                            }
                            else -> {
                                if (ton != 0) {
                                    val intent = Intent()
                                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                    intent.putExtra("resurs", "ton$ton")
                                    intent.putExtra("title", "Тон $ton")
                                    intent.putExtra("zmena_chastki", true)
                                    startActivity(intent)
                                } else if (denNedzeli != 1) {
                                    val intent = Intent()
                                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                    intent.putExtra("resurs", "ton${denNedzeli - 1}_budni")
                                    val title = when (denNedzeli - 1) {
                                        1 -> "ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам"
                                        2 -> "АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю"
                                        3 -> "СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу"
                                        4 -> "ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю"
                                        5 -> "ПЯТНІЦА\nСлужба Крыжу Гасподняму"
                                        else -> "Субота\nСлужба ўсім сьвятым і памёрлым"
                                    }
                                    intent.putExtra("title", title)
                                    intent.putExtra("zmena_chastki", true)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    4 -> {
                        when {
                            slugba.checkUtran(raznicia, dayOfYear.toInt()) -> {
                                val intent = Intent()
                                val resours = slugba.getResource(raznicia, dayOfYear.toInt(), SlugbovyiaTextu.UTRAN)
                                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("resurs", resours)
                                intent.putExtra("zmena_chastki", true)
                                intent.putExtra("title", slugba.getTitle(resours))
                                startActivity(intent)
                            }
                            denNedzeli == 1 -> {
                                val intent = Intent()
                                intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", "Ютрань нядзельная (у скароце)")
                                intent.putExtra("zmena_chastki", true)
                                intent.putExtra("resurs", "jutran_niadzelnaja")
                                startActivity(intent)
                            }
                        }
                    }
                }

            }
        }

        init {
            itemList = list
        }
    }
}