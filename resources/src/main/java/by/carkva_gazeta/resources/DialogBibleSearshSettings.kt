package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.SimpleListItem4Binding
import by.carkva_gazeta.resources.databinding.DialogBibleSearshSettingsBinding

class DialogBibleSearshSettings : DialogFragment() {
    private lateinit var prefEditors: Editor
    private var mListener: DiallogBibleSearshListiner? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogBibleSearshSettingsBinding? = null
    private val binding get() = _binding!!
    private var check1 = true
    private var check2 = 0
    private var check3 = 0

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

    internal interface DiallogBibleSearshListiner {
        fun setSettingsPegistrbukv(pegistrbukv: Boolean)
        fun setSettingsSlovocalkam(slovocalkam: Int)
        fun setSettingsBibliaSeash(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DiallogBibleSearshListiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DiallogBibleSearshListiner")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("check1", check1)
        outState.putInt("check2", check2)
        outState.putInt("check3", check3)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogBibleSearshSettingsBinding.inflate(LayoutInflater.from(it))
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            prefEditors = chin.edit()
            var style = R.style.AlertDialogTheme
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            val textViewZaglavie = binding.title
            textViewZaglavie.text = resources.getString(R.string.settings_poshuk)
            val checkBox = binding.checkBox
            if (savedInstanceState == null) {
                check1 = chin.getBoolean("pegistrbukv", true)
                check2 = chin.getInt("slovocalkam", 0)
                check3 = chin.getInt("biblia_seash", 0)
            } else {
                check1 = savedInstanceState.getBoolean("check1")
                check2 = savedInstanceState.getInt("check2")
                check2 = savedInstanceState.getInt("check3")
            }
            if (!check1) checkBox.isChecked = true
            checkBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    prefEditors.putBoolean("pegistrbukv", false)
                } else {
                    prefEditors.putBoolean("pegistrbukv", true)
                }
                prefEditors.apply()
                mListener?.setSettingsPegistrbukv(isChecked)
            }
            val checkBox1 = binding.checkBox2
            if (check2 == 1) checkBox1.isChecked = true
            checkBox1.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    prefEditors.putInt("slovocalkam", 1)
                    mListener?.setSettingsSlovocalkam(1)
                } else {
                    prefEditors.putInt("slovocalkam", 0)
                    mListener?.setSettingsSlovocalkam(0)
                }
                prefEditors.apply()

            }
            val zavet = arguments?.getInt("zavet") ?: 1
            val data = if (zavet == 3) arrayOf(getString(R.string.psalter))
            else resources.getStringArray(R.array.serche_bible)
            if (zavet == 4) {
                binding.spinner6.visibility = View.GONE
            }
            val spinner = binding.spinner6
            val arrayAdapter = DialogBibleAdapter(it, data)
            spinner.adapter = arrayAdapter
            if (zavet != 3) {
                spinner.setSelection(check3)
                spinner.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        mListener?.setSettingsBibliaSeash(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        return builder.create()
    }

    private inner class DialogBibleAdapter(private val context: Activity, private val name: Array<String>) : ArrayAdapter<String?>(context, R.layout.simple_list_item_1, name) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch)
                textView.setBackgroundResource(R.drawable.selector_dark)
            else
                textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem4Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch)
                viewHolder.text.setBackgroundResource(R.drawable.selector_dark)
            else
                viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            viewHolder.text.gravity = Gravity.START
            viewHolder.text.text = name[position]
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    companion object {
        fun getInstance(zavet: Int): DialogBibleSearshSettings {
            val instance = DialogBibleSearshSettings()
            val args = Bundle()
            args.putInt("zavet", zavet)
            instance.arguments = args
            return instance
        }
    }
}