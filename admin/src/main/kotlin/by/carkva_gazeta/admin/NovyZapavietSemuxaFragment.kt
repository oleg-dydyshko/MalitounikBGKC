package by.carkva_gazeta.admin

import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import by.carkva_gazeta.admin.databinding.AdminBiblePageFragmentBinding
import by.carkva_gazeta.malitounik.BaseFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File

class NovyZapavietSemuxaFragment : BaseFragment() {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var _binding: AdminBiblePageFragmentBinding? = null
    private val binding get() = _binding!!
    private var urlJob: Job? = null
    private var mLastClickTime: Long = 0
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference

    override fun onDestroyView() {
        super.onDestroyView()
        urlJob?.cancel()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(Malitounik.applicationContext())
        kniga = arguments?.getInt("kniga") ?: 0
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pazicia") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AdminBiblePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_save) {
            sendPostRequest(kniga + 1, binding.textView.text.toString(), page + 1)
            return true
        }
        return false
    }

    private fun sendPostRequest(id: Int, spaw: String, sv: Int) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("Semucha", "txt")
                }
                val zag = "Разьдзел"
                referens.child("/chytanne/Semucha/biblian$id.txt").getFile(localFile).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val file = localFile.readText()
                        val file2 = file.split("===")
                        val fileNew = StringBuilder()
                        for ((count, element) in file2.withIndex()) {
                            val fil = element.trim()
                            var srtn = "\n"
                            var stringraz = ""
                            if (fil != "") {
                                if (count != 0) {
                                    srtn = "\n\n"
                                    stringraz = "===\n"
                                }
                                if (file2.size == count + 1) {
                                    srtn = "\n"
                                }
                                if (count == sv) {
                                    fileNew.append(stringraz + "//" + zag + " " + sv + "\n" + spaw.trim() + srtn)
                                } else {
                                    fileNew.append(stringraz + fil + srtn)
                                }
                            }
                        }
                        localFile.writer().use {
                            it.write(fileNew.toString())
                        }
                    } else {
                        activity?.let {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                }.await()
                referens.child("/chytanne/Semucha/biblian$id.txt").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                    activity?.let {
                        if (task.isSuccessful) {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.save))
                        } else {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }
                }.await()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (MainActivity.isNetworkAvailable()) {
            binding.progressBar2.visibility = View.VISIBLE
            var url = "/chytanne/Semucha/biblian1.txt"
            when (kniga) {
                0 -> url = "/chytanne/Semucha/biblian1.txt"
                1 -> url = "/chytanne/Semucha/biblian2.txt"
                2 -> url = "/chytanne/Semucha/biblian3.txt"
                3 -> url = "/chytanne/Semucha/biblian4.txt"
                4 -> url = "/chytanne/Semucha/biblian5.txt"
                5 -> url = "/chytanne/Semucha/biblian6.txt"
                6 -> url = "/chytanne/Semucha/biblian7.txt"
                7 -> url = "/chytanne/Semucha/biblian8.txt"
                8 -> url = "/chytanne/Semucha/biblian9.txt"
                9 -> url = "/chytanne/Semucha/biblian10.txt"
                10 -> url = "/chytanne/Semucha/biblian11.txt"
                11 -> url = "/chytanne/Semucha/biblian12.txt"
                12 -> url = "/chytanne/Semucha/biblian13.txt"
                13 -> url = "/chytanne/Semucha/biblian14.txt"
                14 -> url = "/chytanne/Semucha/biblian15.txt"
                15 -> url = "/chytanne/Semucha/biblian16.txt"
                16 -> url = "/chytanne/Semucha/biblian17.txt"
                17 -> url = "/chytanne/Semucha/biblian18.txt"
                18 -> url = "/chytanne/Semucha/biblian19.txt"
                19 -> url = "/chytanne/Semucha/biblian20.txt"
                20 -> url = "/chytanne/Semucha/biblian21.txt"
                21 -> url = "/chytanne/Semucha/biblian22.txt"
                22 -> url = "/chytanne/Semucha/biblian23.txt"
                23 -> url = "/chytanne/Semucha/biblian24.txt"
                24 -> url = "/chytanne/Semucha/biblian25.txt"
                25 -> url = "/chytanne/Semucha/biblian26.txt"
                26 -> url = "/chytanne/Semucha/biblian27.txt"
            }
            urlJob = CoroutineScope(Dispatchers.Main).launch {
                val sb = StringBuilder()
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("SemuchaRead", "txt")
                    }
                    referens.child(url).getFile(localFile).addOnSuccessListener {
                        val text = localFile.readText()
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
                    }.await()
                } catch (e: Throwable) {
                    activity?.let {
                        MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
                binding.textView.setText(sb.toString().trim())
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance(page: Int, kniga: Int, pazicia: Int): NovyZapavietSemuxaFragment {
            val fragmentFirst = NovyZapavietSemuxaFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}