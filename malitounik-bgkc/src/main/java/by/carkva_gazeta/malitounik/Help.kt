package by.carkva_gazeta.malitounik

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.HelpBinding
import com.google.android.play.core.review.ReviewManagerFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class Help : BaseActivity() {

    private lateinit var binding: HelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = HelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.movementMethod = LinkMovementMethod.getInstance()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }
        val inputStream = resources.openRawResource(R.raw.help)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                builder.append(line)
            }
        }
        val ss = SpannableString(MainActivity.fromHtml(builder.toString()))
        val t1 = ss.indexOf("Google play")
        ss.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val manager = ReviewManagerFactory.create(this@Help)
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        manager.launchReviewFlow(this@Help, reviewInfo)
                    }
                }

            }
        }, t1, t1 + 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textView.text = ss
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getString(R.string.help)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }
}