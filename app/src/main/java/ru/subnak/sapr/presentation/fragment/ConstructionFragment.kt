package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.DialogKnotBinding
import ru.subnak.sapr.databinding.DialogRodBinding
import ru.subnak.sapr.databinding.FragmentConstructionBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Knot
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.presentation.ConstructionApplication
import ru.subnak.sapr.presentation.adapter.KnotListAdapter
import ru.subnak.sapr.presentation.adapter.RodListAdapter
import ru.subnak.sapr.presentation.viewmodel.ConstructionViewModel
import ru.subnak.sapr.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject


class ConstructionFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentConstructionBinding? = null
    private val binding: FragmentConstructionBinding
        get() = _binding ?: throw RuntimeException("FragmentConstructionBinding = null")

    private lateinit var constructionViewModel: ConstructionViewModel

    private lateinit var rodListAdapter: RodListAdapter
    private lateinit var knotListAdapter: KnotListAdapter

    private var screenMode: String = MODE_UNKNOWN
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
        _binding = FragmentConstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constructionViewModel =
            ViewModelProvider(this, viewModelFactory)[ConstructionViewModel::class.java]
        setupRecyclerView()
        observeViewModel()
        launchRightMode()
        setClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setClickListeners() {
        binding.btnAddKnot.setOnClickListener {
            knotsDialog()
        }
        binding.btnAddRod.setOnClickListener {
            val rodListSize = constructionViewModel.getRodListSize()
            val knotListSize = constructionViewModel.getKnotListSize()
            if (rodListSize < knotListSize - 1) {
                rodsDialog()
            } else {
                Toast.makeText(requireContext(), R.string.toast_rod_to_many, Toast.LENGTH_LONG)
                    .show()
            }
        }
        binding.btnSaveConstruction.setOnClickListener {
            if (constructionViewModel.checkPropAndCountOfRods() == ConstructionViewModel.ERROR_TYPE_NULL) {
                constructionViewModel.addConstruction()
            } else if (constructionViewModel.checkPropAndCountOfRods() == ConstructionViewModel.ERROR_TYPE_PROP) {
                Toast.makeText(requireContext(), R.string.toast_need_support, Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), R.string.toast_need_rods, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun knotsDialog(knot: Knot? = null) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val knotBinding = DialogKnotBinding.inflate(layoutInflater)
        alertDialog.setView(knotBinding.root)
        observeViewModelForKnotDialog(knotBinding)
        knotEditTextAddTextChangedListeners(knotBinding)
        if (knot !== null) {
            knotBinding.etKnotCoordX.setText(knot.x.toString())
            knotBinding.etKnotLoadConcentrated.setText(knot.loadConcentrated.toString())
            knotBinding.cbKnotProp.isChecked = knot.prop

            knotBinding.buttonKnotApply.setOnClickListener {
                val closeDialog = constructionViewModel.editKnot(
                    knotBinding.etKnotCoordX.text?.toString(),
                    knotBinding.etKnotLoadConcentrated.text?.toString(),
                    knotBinding.cbKnotProp.isChecked,
                    knot.knotNumber
                )
                if (closeDialog) {
                    alertDialog.dismiss()
                }
            }
        } else {
            knotBinding.buttonKnotApply.setOnClickListener {
                val closeDialog = constructionViewModel.addKnot(
                    knotBinding.etKnotCoordX.text?.toString(),
                    knotBinding.etKnotLoadConcentrated.text?.toString(),
                    knotBinding.cbKnotProp.isChecked
                )
                if (closeDialog) {
                    alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
        alertDialog.setOnCancelListener {
            constructionViewModel.resetErrorInputX()
        }
    }

    private fun observeViewModelForKnotDialog(knotBinding: DialogKnotBinding) {
        constructionViewModel.errorInputX.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.dialog_knot_error_input_x)
            } else {
                null
            }
            knotBinding.etKnotCoordX.error = message
        }
    }

    private fun knotEditTextAddTextChangedListeners(knotBinding: DialogKnotBinding) {
        knotBinding.etKnotCoordX.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                constructionViewModel.resetErrorInputX()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun rodsDialog(rod: Rod? = null) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val rodBinding = DialogRodBinding.inflate(layoutInflater)
        alertDialog.setView(rodBinding.root)
        observeViewModelForRodDialog(rodBinding)
        rodEditTextAddTextChangedListeners(rodBinding)
        if (rod !== null) {
            rodBinding.etRodSquare.setText(rod.square.toString())
            rodBinding.etRodElasticModule.setText(rod.elasticModule.toString())
            rodBinding.etRodLoadRunning.setText(rod.loadRunning.toString())
            rodBinding.etRodVoltage.setText(rod.voltage.toString())

            rodBinding.buttonRodApply.setOnClickListener {
                val closeDialog = constructionViewModel.editRod(
                    rodBinding.etRodSquare.text?.toString(),
                    rodBinding.etRodElasticModule.text?.toString(),
                    rodBinding.etRodLoadRunning.text?.toString(),
                    rodBinding.etRodVoltage.text?.toString(),
                    rod.rodNumber
                )
                if (closeDialog) {
                    alertDialog.dismiss()
                }
            }
        } else {
            rodBinding.buttonRodApply.setOnClickListener {
                val closeDialog = constructionViewModel.addRod(
                    rodBinding.etRodSquare.text?.toString(),
                    rodBinding.etRodElasticModule.text?.toString(),
                    rodBinding.etRodLoadRunning.text?.toString(),
                    rodBinding.etRodVoltage.text?.toString()
                )
                if (closeDialog) {
                    alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
        alertDialog.setOnCancelListener {
            constructionViewModel.resetErrorInputSquare()
            constructionViewModel.resetErrorInputElasticModule()
            constructionViewModel.resetErrorInputVoltage()
        }
    }

    private fun observeViewModelForRodDialog(rodBinding: DialogRodBinding) {
        constructionViewModel.errorInputSquare.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.dialog_rod_error_input_square)
            } else {
                null
            }
            rodBinding.etRodSquare.error = message
        }
        constructionViewModel.errorInputElasticModule.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.dialog_rod_error_input_elastic_module)
            } else {
                null
            }
            rodBinding.etRodElasticModule.error = message
        }
        constructionViewModel.errorInputVoltage.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.dialog_rod_error_input_voltage)
            } else {
                null
            }
            rodBinding.etRodVoltage.error = message
        }
    }

    private fun rodEditTextAddTextChangedListeners(rodBinding: DialogRodBinding) {
        rodBinding.etRodSquare.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                constructionViewModel.resetErrorInputSquare()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        rodBinding.etRodElasticModule.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                constructionViewModel.resetErrorInputElasticModule()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        rodBinding.etRodVoltage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                constructionViewModel.resetErrorInputVoltage()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun setupRecyclerView() {
        setupRodRecyclerView()
        setupKnotRecyclerView()
        setupRecyclersClickListeners()
    }

    private fun setupRodRecyclerView() {
        val rvRodList = binding.rvRods
        rvRodList.setHasFixedSize(false)
        rvRodList.isNestedScrollingEnabled = false
        rodListAdapter = RodListAdapter()
        rvRodList.adapter = rodListAdapter
        rvRodList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupKnotRecyclerView() {
        val rvKnotList = binding.rvKnots
        rvKnotList.setHasFixedSize(false)
        rvKnotList.isNestedScrollingEnabled = false
        knotListAdapter = KnotListAdapter()
        rvKnotList.adapter = knotListAdapter
        rvKnotList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupRecyclersClickListeners() {
        knotListAdapter.onKnotListClickListener = {
            knotsDialog(it)
        }
        rodListAdapter.onRodListClickListener = {
            rodsDialog(it)
        }
    }

    private fun observeViewModel() {
        constructionViewModel.rodList.observe(viewLifecycleOwner) {
            rodListAdapter.submitList(it.toList())
        }
        constructionViewModel.knotList.observe(viewLifecycleOwner) {
            knotListAdapter.submitList(it.toList())
        }
    }

    private fun launchEditMode() {
        constructionViewModel.getConstruction(constructionId)

    }

    private fun launchAddMode() {

    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun parseArguments() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Argument is null")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(CONSTRUCTION_ID)) {
                throw RuntimeException("Param construction id is absent")
            }
            constructionId = args.getInt(CONSTRUCTION_ID, Construction.UNDEFINED_ID)
        }
    }

    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val CONSTRUCTION_ID = "extra_construction_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddConstruction() =
            ConstructionFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }

        fun newInstanceEditConstruction(constructionId: Int) =
            ConstructionFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(CONSTRUCTION_ID, constructionId)
                }
            }

    }
}