package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class WidgetRadyjoMaryiaProgram : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = DialogWidgetProgramPadoiMaryia()
        dialog.show(supportFragmentManager, "DialogWidgetProgramPadoiMaryia")
        val intent = Intent(this, WidgetRadyjoMaryia::class.java)
        intent.putExtra("action", 30)
        sendBroadcast(intent)
    }
}