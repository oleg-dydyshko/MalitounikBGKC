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
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_sabytie_settings.*

/**
 * Created by oleg on 21.7.17
 */
class DialogSabytieSettings : DialogFragment() {
    private lateinit var ringTone: Ringtone
    private var uriAlarm: Uri? = null
    private var uriNotification: Uri? = null
    private var uriRingtone: Uri? = null
    private val ringtonepicker = 90
    private var uri: Uri? = null
    private lateinit var k: SharedPreferences
    private lateinit var prefEditor: Editor
    private lateinit var alert: AlertDialog
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            notificationNotification.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            notificationAlarm.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            notificationRingtone.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            notificationPicker.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            uriNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            notificationAlarm.text = getString(R.string.uriAlarm, RingtoneManager.getRingtone(it, uriAlarm).getTitle(it))
            notificationNotification.text = getString(R.string.uriNotification, RingtoneManager.getRingtone(it, uriNotification).getTitle(it))
            notificationRingtone.text = getString(R.string.uriRingtone, RingtoneManager.getRingtone(it, uriRingtone).getTitle(it))
            val soundURI = k.getString("soundURI", "")
            uri = Uri.parse(soundURI)
            ringTone = RingtoneManager.getRingtone(it, uri)
            if (soundURI == "") notificationPicker.text = "Іншая мелодыя" else notificationPicker.text = getString(R.string.uriPicker, ringTone.getTitle(it))
            val sound = k.getInt("soundnotification", 0)
            notificationGrupRington.setOnCheckedChangeListener(grupRington())
            notificationNotification.isChecked = sound == 0
            notificationAlarm.isChecked = sound == 1
            notificationRingtone.isChecked = sound == 2
            notificationPicker.isChecked = sound == 3
            if (dzenNoch) guk.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            guk.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val gukInt = k.getInt("guk", 1)
            if (gukInt == 0) {
                guk.isChecked = false
                notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                notificationGrupRington.setOnCheckedChangeListener(null)
                notificationNotification.isClickable = false
                notificationAlarm.isClickable = false
                notificationRingtone.isClickable = false
                notificationPicker.isClickable = false
            } else {
                play.setOnClickListener(play())
                stop.setOnClickListener(stop())
            }
            guk.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                prefEditor = k.edit()
                if (isChecked) {
                    if (dzenNoch) {
                        notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                        notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                        notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                        notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                    } else {
                        notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    }
                    notificationGrupRington.setOnCheckedChangeListener(grupRington())
                    notificationNotification.isClickable = true
                    notificationAlarm.isClickable = true
                    notificationRingtone.isClickable = true
                    notificationPicker.isClickable = true
                    play.setOnClickListener(play())
                    stop.setOnClickListener(stop())
                    prefEditor.putInt("guk", 1)
                } else {
                    notificationNotification.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    notificationAlarm.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    notificationRingtone.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    notificationPicker.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                    notificationGrupRington.setOnCheckedChangeListener(null)
                    notificationNotification.isClickable = false
                    notificationAlarm.isClickable = false
                    notificationRingtone.isClickable = false
                    notificationPicker.isClickable = false
                    play.setOnClickListener(null)
                    stop.setOnClickListener(null)
                    prefEditor.putInt("guk", 0)
                }
                prefEditor.apply()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val ad = AlertDialog.Builder(it)
            rootView = View.inflate(it, R.layout.dialog_sabytie_settings, null)
            ad.setView(rootView)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
        }
        return alert
    }

    private fun play(): View.OnClickListener {
        return View.OnClickListener {
            activity?.let {
                ringTone.stop()
                when {
                    notificationAlarm.isChecked -> {
                        ringTone = RingtoneManager.getRingtone(it, uriAlarm)
                    }
                    notificationNotification.isChecked -> {
                        ringTone = RingtoneManager.getRingtone(it, uriNotification)
                    }
                    notificationRingtone.isChecked -> {
                        ringTone = RingtoneManager.getRingtone(it, uriRingtone)
                    }
                    notificationPicker.isChecked -> {
                        uri = Uri.parse(k.getString("soundURI", ""))
                        ringTone = RingtoneManager.getRingtone(it, uri)
                    }
                }
                ringTone.play()
            }
        }
    }

    private fun stop(): View.OnClickListener {
        return View.OnClickListener {
                ringTone.stop()
        }
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
                    startActivityForResult(intent1, ringtonepicker)
                }
            }
            prefEditor.apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ringtonepicker && resultCode == Activity.RESULT_OK) {
            uri = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            activity?.let {
                ringTone = RingtoneManager.getRingtone(it, uri)
                notificationPicker.text = getString(R.string.uriPicker, ringTone.getTitle(it))
            }
            prefEditor = k.edit()
            prefEditor.putString("soundURI", uri.toString())
            prefEditor.apply()
        }
    }
}