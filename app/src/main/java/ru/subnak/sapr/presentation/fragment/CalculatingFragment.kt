package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.slider.Slider
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.FragmentCalculatingBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.presentation.ConstructionApplication
import ru.subnak.sapr.presentation.viewmodel.CalculatingViewModel
import ru.subnak.sapr.presentation.viewmodel.ViewModelFactory
import java.math.MathContext
import java.math.RoundingMode
import javax.inject.Inject


class CalculatingFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CalculatingViewModel

    private var _binding: FragmentCalculatingBinding? = null
    private val binding: FragmentCalculatingBinding
        get() = _binding ?: throw RuntimeException("FragmentCalculatingBinding = null")

    private var constructionId: Int = Construction.UNDEFINED_ID


    private val component by lazy {
        (requireActivity().application as ConstructionApplication).component
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CalculatingViewModel::class.java]
        viewModel.getConstruction(constructionId)
        setEditTextAddTextChangedListener()
        viewModel.construction.observe(viewLifecycleOwner) {
            binding.constructionImage.setImageURI(Uri.parse(it.img))
            viewModel.setResult(it)
            val numberOfRods = it.rodValues.size.toFloat()
            if (numberOfRods > ONE) {
                binding.sbResultSelectX.valueTo = numberOfRods
                binding.sbResultSelectX.valueFrom = 1F
                binding.sbResultSelectX.value = 1F
            } else {
                binding.tvResultSelectX.text = getString(R.string.there_is_only_one_rod_here)
                binding.sbResultSelectX.isEnabled = false
                binding.sbResultSelectX.valueTo = 2F
                binding.sbResultSelectX.valueFrom = 0F
                binding.sbResultSelectX.value = 1F
            }

            binding.sbResultSelectX.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {

                }

                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.setRodNumber(slider.value.toInt())
                }
            })
        }
        viewModel.errorInputX.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.dialog_node_error_input_x)
            } else {
                null
            }
            binding.etNodeCoordXResult.error = message
            binding.tvResultNx.text = getString(R.string.m_dash)
            binding.tvResultUx.text = getString(R.string.m_dash)
            binding.tvResultSx.text = getString(R.string.m_dash)
        }

        viewModel.resultNx.observe(viewLifecycleOwner) {
            binding.tvResultNx.text =
                it.toBigDecimal(MathContext(5, RoundingMode.HALF_UP)).toEngineeringString()
        }
        viewModel.resultUx.observe(viewLifecycleOwner) {
            binding.tvResultUx.text =
                it.toBigDecimal(MathContext(5, RoundingMode.HALF_UP)).toEngineeringString()
        }
        viewModel.resultSx.observe(viewLifecycleOwner) {
            binding.tvResultSx.text =
                it.toBigDecimal(MathContext(5, RoundingMode.HALF_UP)).toEngineeringString()
        }


    }

    private fun setEditTextAddTextChangedListener() {
        binding.etNodeCoordXResult.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.calculateLocalResult(s.toString())
                if (count == 0) {
                    binding.tvResultNx.text = getString(R.string.m_dash)
                    binding.tvResultUx.text = getString(R.string.m_dash)
                    binding.tvResultSx.text = getString(R.string.m_dash)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun parseArguments() {
        val args = requireArguments()
        if (!args.containsKey(CONSTRUCTION_ID)) {
            throw RuntimeException("Param construction id is absent")
        }
        constructionId = args.getInt(CONSTRUCTION_ID, Construction.UNDEFINED_ID)

    }

    companion object {

        private const val CONSTRUCTION_ID = "extra_construction_id"
        private const val ONE = 1


        fun newInstanceCalculating(constructionId: Int) =
            CalculatingFragment().apply {
                arguments = Bundle().apply {
                    putInt(CONSTRUCTION_ID, constructionId)
                }
            }
    }
}