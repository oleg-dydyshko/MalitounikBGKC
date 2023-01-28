package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity.Companion.toastView
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class MenuBibliatekaArtykuly : BaseListFragment() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<LinkedTreeMap<String, String>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            val localFile = File("${activity.filesDir}/history.json")
            if (MainActivity.isNetworkAvailable(true)) {
                if (!localFile.exists()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Malitounik.referens.child("/history.json").getFile(localFile).addOnFailureListener {
                            toastView(activity, getString(R.string.error))
                        }.await()
                        load(localFile)
                    }
                } else {
                    load(localFile)
                }
            } else if (MainActivity.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Malitounik.referens.child("/history.json").getFile(localFile).addOnFailureListener {
                        toastView(activity, getString(R.string.error))
                    }.await()
                    load(localFile)
                }
            } else {
                if (localFile.exists()) {
                    load(localFile)
                }
            }
            listView.isVerticalScrollBarEnabled = false
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                listView.setBackgroundResource(R.color.colorbackground_material_dark)
                listView.selector = ContextCompat.getDrawable(activity, R.drawable.selector_dark)
            }
        }
    }

    private fun load(localFile: File) {
        activity?.let { activity ->
            val gson = Gson()
            val text = localFile.readText()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
            data.addAll(gson.fromJson(text, type))
            listAdapter = MenuListAdaprer(activity)
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        activity?.let {
            val intent = Intent(it, BibliatekaArtykuly::class.java)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

    private inner class MenuListAdaprer(private val context: Activity) : ArrayAdapter<LinkedTreeMap<String, String>>(context, R.layout.simple_list_item_2, R.id.label, data) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            viewHolder.text.text = data[position]["link"]
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)
}