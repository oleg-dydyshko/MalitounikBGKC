package by.carkva_gazeta.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.carkva_gazeta.admin.databinding.AdminBiblePageFragmentBinding
import by.carkva_gazeta.malitounik.MainActivity
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class StaryZapavietSemuxaFragment : Fragment() {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var _binding: AdminBiblePageFragmentBinding? = null
    private val binding get() = _binding!!
    private var urlJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        urlJob?.cancel()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        kniga = arguments?.getInt("kniga") ?: 0
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pazicia") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AdminBiblePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            sendPostRequest(kniga + 1, binding.textView.text.toString(), page + 1)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendPostRequest(id: Int, spaw: String, sv: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            val response = StringBuffer()
            var responseCodeS = 500
            withContext(Dispatchers.IO) {
                var zag = "Разьдзел"
                if (id == 19) zag = "Псальма"
                var reqParam = URLEncoder.encode("z", "UTF-8") + "=" + URLEncoder.encode("s", "UTF-8")
                reqParam += "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id.toString(), "UTF-8")
                reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                reqParam += "&" + URLEncoder.encode("save", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                reqParam += "&" + URLEncoder.encode("spaw", "UTF-8") + "=" + URLEncoder.encode(spaw, "UTF-8")
                reqParam += "&" + URLEncoder.encode("zag", "UTF-8") + "=" + URLEncoder.encode(zag, "UTF-8")
                reqParam += "&" + URLEncoder.encode("sv", "UTF-8") + "=" + URLEncoder.encode(sv.toString(), "UTF-8")
                val mURL = URL("https://carkva-gazeta.by/biblija/index.php")
                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    val wr = OutputStreamWriter(outputStream)
                    wr.write(reqParam)
                    wr.flush()
                    responseCodeS = responseCode
                    BufferedReader(InputStreamReader(inputStream)).use {
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                    }
                }
            }
            activity?.let {
                if (responseCodeS == 200) {
                    MainActivity.toastView(it, response.toString())
                } else {
                    MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            binding.textView.setText(savedInstanceState.getString("spaw"))
        } else {
            binding.progressBar2.visibility = View.VISIBLE
            var url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias1.txt"
            when (kniga) {
                0 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias1.txt"
                1 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias2.txt"
                2 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias3.txt"
                3 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias4.txt"
                4 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias5.txt"
                5 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias6.txt"
                6 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias7.txt"
                7 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias8.txt"
                8 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias9.txt"
                9 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias10.txt"
                10 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias11.txt"
                11 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias12.txt"
                12 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias13.txt"
                13 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias14.txt"
                14 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias15.txt"
                15 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias16.txt"
                16 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias17.txt"
                17 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias18.txt"
                18 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias19.txt"
                19 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias20.txt"
                20 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias21.txt"
                21 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias22.txt"
                22 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias23.txt"
                23 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias24.txt"
                24 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias25.txt"
                25 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias26.txt"
                26 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias27.txt"
                27 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias28.txt"
                28 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias29.txt"
                29 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias30.txt"
                30 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias31.txt"
                31 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias32.txt"
                32 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias33.txt"
                33 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias34.txt"
                34 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias35.txt"
                35 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias36.txt"
                36 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias37.txt"
                37 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias38.txt"
                38 -> url = "https://www.carkva-gazeta.by/chytanne/Semucha/biblias39.txt"
            }
            urlJob = CoroutineScope(Dispatchers.Main).launch {
                val sb = StringBuilder()
                withContext(Dispatchers.IO) {
                    val inputStream = URL(url)
                    val text = inputStream.readText()
                    val split = text.split("===")
                    val knig = split[page + 1]
                    val split2 = knig.split("\n")
                    split2.forEach {
                        val t1 = it.indexOf("//")
                        if (t1 != -1) {
                            sb.append(it.substring(0, t1)).append("\n")
                        } else {
                            sb.append(it).append("\n")
                        }
                    }
                }
                binding.textView.setText(sb.toString().trim())
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("spaw", binding.textView.text.toString())
    }

    companion object {
        fun newInstance(page: Int, kniga: Int, pazicia: Int): StaryZapavietSemuxaFragment {
            val fragmentFirst = StaryZapavietSemuxaFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}