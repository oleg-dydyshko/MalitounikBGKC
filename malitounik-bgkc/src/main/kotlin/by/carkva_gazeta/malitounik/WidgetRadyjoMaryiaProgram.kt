package by.carkva_gazeta.malitounik

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class WidgetRadyjoMaryiaProgram : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = DialogWidgetProgramPadoiMaryia()
        dialog.show(supportFragmentManager, "DialogWidgetProgramPadoiMaryia")
    }
}