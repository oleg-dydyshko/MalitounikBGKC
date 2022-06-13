package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSabytieSettingsBinding

class DialogSabytieSettings : DialogFragment(), View.OnClickListener {
    private lateinit var ringTone: Ringtone
    private var uriAlarm: Uri? = null
    private var uriNotification: Uri? = null
    private var uriRingtone: Uri? = null
    private var uri: Uri? = null
    private lateinit var k: SharedPreferences
    private lateinit var prefEditor: Editor
    private lateinit var alert: AlertDialog
    private var _binding: DialogSabytieSettingsBinding? = null
    private val binding get() = _binding!!

    private val ringtoneManagerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            uri = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            activity?.let {
                ringTone = RingtoneManager.getRingtone(it, uri)
                binding.notificationPicker.text = getString(R.string.uriPicker, ringTone.getTitle(it))
            }
            prefEditor = k.edit()
            prefEditor.putString("soundURI", uri.toString())
            prefEditor.apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            _binding = DialogSabytieSettingsBinding.inflate(LayoutInflater.from(it))
            binding.notificationNotification.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.notificationAlarm.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.notificationRingtone.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.notificationPicker.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            uriNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            binding.notificationAlarm.text = getString(R.string.uriAlarm, RingtoneManager.getRingtone(it, uriAlarm).getTitle(it))
            binding.notificationNotification.text = getString(R.string.uriNotification, RingtoneManager.getRingtone(it, uriNotification).getTitle(it))
            binding.notificationRingtone.text = getString(R.string.uriRingtone, RingtoneManager.getRingtone(it, uriRingtone).getTitle(it))
            val soundURI = k.getString("soundURI", "")
            uri = Uri.parse(soundURI)
            ringTone = RingtoneManager.getRingtone(it, uri)
            if (soundURI == "") binding.notificationPicker.text = getString(R.string.uriPicker2)
            else binding.notificationPicker.text = getString(R.string.uriPicker, ringTone.getTitle(it))
            val sound = k.getInt("soundnotification", 0)
            binding.notificationGrupRington.setOnCheckedChangeListener(grupRington())
            binding.notificationNotification.isChecked = sound == 0
            binding.notificationAlarm.isChecked = sound == 1
            binding.notificationRingtone.isChecked = sound == 2
            binding.notificationPicker.isChecked = sound == 3
            if (dzenNoch) binding.guk.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.guk.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val gukInt = k.getInt("guk", 1)
            if (gukInt == 0) {
                binding.guk.isChecked = false
                binding.notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                binding.notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                binding.notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                binding.notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                binding.notificationGrupRington.setOnCheckedChangeListener(null)
                binding.notificationNotification.isClickable = false
                binding.notificationAlarm.isClickable = false
                binding.notificationRingtone.isClickable = false
                binding.notificationPicker.isClickable = false
            } else {
                binding.play.setOnClickListener(this)
                binding.stop.setOnClickListener(this)
            }
            binding.guk.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                prefEditor = k.edit()
                if (isChecked) {
                    if (dzenNoch) {
                        binding.notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    } else {
                        binding.notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    }
                    binding.notificationGrupRington.setOnCheckedChangeListener(grupRington())
                    binding.notificationNotification.isClickable = true
                    binding.notificationAlarm.isClickable = true
                    binding.notificationRingtone.isClickable = true
                    binding.notificationPicker.isClickable = true
                    binding.play.setOnClickListener(this)
                    binding.stop.setOnClickListener(this)
                    prefEditor.putInt("guk", 1)
                } else {
                    binding.notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    binding.notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    binding.notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    binding.notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    binding.notificationGrupRington.setOnCheckedChangeListener(null)
                    binding.notificationNotification.isClickable = false
                    binding.notificationAlarm.isClickable = false
                    binding.notificationRingtone.isClickable = false
                    binding.notificationPicker.isClickable = false
                    binding.play.setOnClickListener(null)
                    binding.stop.setOnClickListener(null)
                    prefEditor.putInt("guk", 0)
                }
                prefEditor.apply()
            }
            val ad = AlertDialog.Builder(it, style)
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.play) {
            play()
        }
        if (id == R.id.stop) {
            stop()
        }
    }

    private fun play() {
        activity?.let {
            stop()
            when {
                binding.notificationAlarm.isChecked -> {
                    ringTone = RingtoneManager.getRingtone(it, uriAlarm)
                }
                binding.notificationNotification.isChecked -> {
                    ringTone = RingtoneManager.getRingtone(it, uriNotification)
                }
                binding.notificationRingtone.isChecked -> {
                    ringTone = RingtoneManager.getRingtone(it, uriRingtone)
                }
                binding.notificationPicker.isChecked -> {
                    uri = Uri.parse(k.getString("soundURI", ""))
                    ringTone = RingtoneManager.getRingtone(it, uri)
                }
            }
            ringTone.play()
        }
    }

    private fun stop() {
        ringTone.stop()
    }

    private fun grupRington(): RadioGroup.OnCheckedChangeListener {
        return RadioGroup.OnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            val sound2 = k.getInt("soundnotification", 0)
            prefEditor = k.edit()
            when (checkedId) {
                R.id.notificationNotification -> prefEditor.putInt("soundnotification", 0)
                R.id.notificationAlarm -> prefEditor.putInt("soundnotification", 1)
                R.id.notificationRingtone -> prefEditor.putInt("soundnotification", 2)
                R.id.notificationPicker -> if (sound2 != 3) {
                    ringTone.stop()
                    prefEditor.putInt("soundnotification", 3)
                    val intent1 = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                    intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выбар мелодыі для апавяшчэньняў:")
                    intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                    intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                    if (uri == null) uri = Uri.parse(k.getString("soundURI", ""))
                    intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri)
                    ringtoneManagerLauncher.launch(intent1)
                }
            }
            prefEditor.apply()
        }
    }
}