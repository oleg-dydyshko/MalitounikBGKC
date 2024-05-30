package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminDialogSviatyiaBibleDisplayBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding

class DialogSvityiaBible : DialogFragment() {
    private lateinit var dialog: AlertDialog
    private var _binding: AdminDialogSviatyiaBibleDisplayBinding? = null
    private val binding get() = _binding!!
    private val arrayList = ArrayList<Bible>()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = AdminDialogSviatyiaBibleDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.text = getString(R.string.title_biblia)
            builder.setView(binding.root)

            arrayList.add(Bible("Паводле Мацьвея", 28))
            arrayList.add(Bible("Паводле Марка", 16))
            arrayList.add(Bible("Паводле Лукаша", 24))
            arrayList.add(Bible("Паводле Яна", 21))
            arrayList.add(Bible("Дзеі Апосталаў", 28))
            arrayList.add(Bible("Якава", 5))
            arrayList.add(Bible("1-е Пятра", 5))
            arrayList.add(Bible("2-е Пятра", 3))
            arrayList.add(Bible("1-е Яна Багаслова", 5))
            arrayList.add(Bible("2-е Яна Багаслова", 1))
            arrayList.add(Bible("3-е Яна Багаслова", 1))
            arrayList.add(Bible("Юды", 1))
            arrayList.add(Bible("Да Рымлянаў", 16))
            arrayList.add(Bible("1-е да Карынфянаў", 16))
            arrayList.add(Bible("2-е да Карынфянаў", 13))
            arrayList.add(Bible("Да Галятаў", 6))
            arrayList.add(Bible("Да Эфэсянаў", 6))
            arrayList.add(Bible("Да Піліпянаў", 4))
            arrayList.add(Bible("Да Каласянаў", 4))
            arrayList.add(Bible("1-е да Фесаланікійцаў", 5))
            arrayList.add(Bible("2-е да Фесаланікійцаў", 3))
            arrayList.add(Bible("1-е да Цімафея", 6))
            arrayList.add(Bible("2-е да Цімафея", 4))
            arrayList.add(Bible("Да Ціта", 3))
            arrayList.add(Bible("Да Філімона", 1))
            arrayList.add(Bible("Да Габрэяў", 13))
            arrayList.add(Bible("Адкрыцьцё (Апакаліпсіс)", 22))
            val fullGlav = ArrayList<Int>()
            for (e in 1..28) fullGlav.add(e)
            binding.spinner1.adapter = BibleAdapterTitle(it, arrayList)
            binding.spinner2.adapter = BibleAdapterFullglav(it, fullGlav)
            builder.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface, _: Int ->
                val bibleTitle = binding.spinner1.selectedItem as Bible
                val bibleTitlePosition = binding.spinner1.selectedItemPosition
                val bibleFullGlav = binding.spinner2.selectedItem as Int
                if (bibleFullGlav <= bibleTitle.fullglav) {
                    val intent = Intent(it, NovyZapavietSemuxa::class.java)
                    intent.putExtra("kniga", bibleTitlePosition)
                    intent.putExtra("glava", bibleFullGlav - 1)
                    startActivity(intent)
                } else {
                    MainActivity.toastView(it, getString(R.string.error))
                }
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            dialog = builder.create()
        }
        return dialog
    }

    private class BibleAdapterTitle(private val activity: Activity, private val dataTimes: ArrayList<Bible>) : ArrayAdapter<Bible>(activity, R.layout.simple_list_item_1, dataTimes) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = dataTimes[position].title
            textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataTimes.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = dataTimes[position].title
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class BibleAdapterFullglav(private val activity: Activity, private val dataTimes: ArrayList<Int>) : ArrayAdapter<Int>(activity, R.layout.simple_list_item_1, dataTimes) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = dataTimes[position].toString()
            textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataTimes.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = dataTimes[position].toString()
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class Bible(val title: String, val fullglav: Int)
}