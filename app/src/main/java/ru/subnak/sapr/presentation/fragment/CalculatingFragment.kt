package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        loadData()
        return binding.root
    }

    private fun loadData() {
        binding.progressCircular.visibility = View.VISIBLE
        binding.scrollViewCalculating.visibility = View.GONE

    }

    private fun rodToString(number: Int): String {
        return String.format(
            requireContext().getString(R.string.text_zero),
            number
        )
    }

    private fun setupPickerButtonListeners(numberOfRods: Int) {
        binding.buttonMinus.setOnClickListener {
            if (viewModel.rodNumber > 1) {
                viewModel.rodNumber--
                binding.tvRodNumber.text = rodToString(viewModel.rodNumber)
            }
        }
        binding.buttonPlus.setOnClickListener {
            if (viewModel.rodNumber < numberOfRods) {
                viewModel.rodNumber++
                binding.tvRodNumber.text = rodToString(viewModel.rodNumber)
            }
        }
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
            val numberOfRods = it.rodValues.size
            if (numberOfRods > ONE) {
                binding.buttonMinus.visibility = View.VISIBLE
                binding.buttonPlus.visibility = View.VISIBLE
                binding.tvRodNumber.visibility = View.VISIBLE
                setupPickerButtonListeners(numberOfRods)
            } else {
                binding.buttonMinus.visibility = View.GONE
                binding.buttonPlus.visibility = View.GONE
                binding.tvRodNumber.visibility = View.GONE
            }
            binding.tvRodNumber.text = rodToString(ONE)
            binding.progressCircular.visibility = View.GONE
            binding.scrollViewCalculating.visibility = View.VISIBLE
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
        viewModel.errorSx.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvResultSx.setTextColor(resources.getColor(R.color.red))
            } else {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> binding.tvResultSx.setTextColor(
                        resources.getColor(
                            R.color.md_theme_dark_onSurfaceVariant
                        )
                    )
                    Configuration.UI_MODE_NIGHT_NO -> binding.tvResultSx.setTextColor(
                        resources.getColor(
                            R.color.md_theme_light_onSurfaceVariant
                        )
                    )
                }

            }
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
                viewModel.resetErrorSx()
                viewModel.calculateLocalResult(s.toString())
                if (count == 0) {
                    binding.tvResultNx.text = getString(R.string.m_dash)
                    binding.tvResultUx.text = getString(R.string.m_dash)
                    binding.tvResultSx.text = getString(R.string.m_dash)
                    viewModel.resetErrorSx()
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