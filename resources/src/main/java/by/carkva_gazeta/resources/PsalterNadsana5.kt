package by.carkva_gazeta.resources

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BaseFragment
import by.carkva_gazeta.resources.databinding.NadsanPravila5Binding

class PsalterNadsana5 : BaseFragment(), View.OnClickListener {

    private var _binding: NadsanPravila5Binding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NadsanPravila5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            binding.textView1.setOnClickListener(this)
            binding.textView3.setOnClickListener(this)
            binding.textView5.setOnClickListener(this)
            binding.textView6.setOnClickListener(this)
            binding.textView7.setOnClickListener(this)
            binding.textView8.setOnClickListener(this)
            binding.textView9.setOnClickListener(this)
            binding.textView10.setOnClickListener(this)
            binding.textView11.setOnClickListener(this)
            binding.textView12.setOnClickListener(this)
            binding.textView13.setOnClickListener(this)
            binding.textView14.setOnClickListener(this)
            binding.textView15.setOnClickListener(this)
            binding.textView16.setOnClickListener(this)
            binding.textView17.setOnClickListener(this)
            binding.textView18.setOnClickListener(this)
            binding.textView19.setOnClickListener(this)
            binding.textView20.setOnClickListener(this)
            binding.textView21.setOnClickListener(this)
            binding.textView22.setOnClickListener(this)
            binding.textView23.setOnClickListener(this)
            binding.textView24.setOnClickListener(this)
            binding.textView25.setOnClickListener(this)
            binding.textView26.setOnClickListener(this)
            binding.textView27.setOnClickListener(this)
            binding.textView28.setOnClickListener(this)
            if (dzenNoch) {
                binding.t1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t6.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t7.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t8.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t9.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t10.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t11.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t12.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.t13.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView6.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView7.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView8.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView9.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView10.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView11.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView12.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView13.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView14.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView15.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView16.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView17.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView18.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView19.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView20.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView21.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView22.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView23.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView24.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView25.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView26.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView27.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
                binding.textView28.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.nadsanblack)
            }
        }
    }

    override fun onClick(v: View?) {
        activity?.let {
            val intent = Intent(it, NadsanContentActivity::class.java)
            var glava = 1
            when (v?.id ?: 0) {
                R.id.textView1 -> glava = 2
                R.id.textView5 -> glava = 4
                R.id.textView7 -> glava = 7
                R.id.textView8, R.id.textView16, R.id.textView12 -> glava = 18
                R.id.textView9 -> glava = 9
                R.id.textView11 -> glava = 12
                R.id.textView13 -> glava = 14
                R.id.textView15 -> glava = 19
                R.id.textView25 -> glava = 17
            }
            intent.putExtra("kafizma", glava)
            startActivity(intent)
        }
    }
}