package by.carkva_gazeta.malitounik

import android.hardware.SensorEvent
import android.os.Bundle


class WidgetRadyjoMaryiaProgram : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val checkInternet = intent.extras?.getBoolean("checkInternet", false) ?: false
        if (checkInternet) {
            val dialog = DialogNoInternet()
            dialog.show(supportFragmentManager, "DialogNoInternet")
        } else {
            val dialog = DialogProgramRadoiMaryia.getInstance(true)
            dialog.show(supportFragmentManager, "DialogWidgetProgramPadoiMaryia")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }
}