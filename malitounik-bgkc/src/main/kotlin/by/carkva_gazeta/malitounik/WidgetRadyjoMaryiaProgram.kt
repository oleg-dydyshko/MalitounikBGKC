package by.carkva_gazeta.malitounik

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class WidgetRadyjoMaryiaProgram : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val checkInternet = intent.extras?.getBoolean("checkInternet", false) ?: false
        if (checkInternet) {
            val dialog = DialogNoInternet()
            dialog.show(supportFragmentManager, "DialogNoInternet")
        } else {
            val dialog = DialogProgramPadoiMaryia()
            dialog.show(supportFragmentManager, "DialogWidgetProgramPadoiMaryia")
        }
    }
}