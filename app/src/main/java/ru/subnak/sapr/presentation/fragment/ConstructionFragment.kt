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
import androidx.recyclerview.widget.RecyclerView
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.DialogNodeBinding
import ru.subnak.sapr.databinding.DialogRodBinding
import ru.subnak.sapr.databinding.FragmentConstructionBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.presentation.ConstructionApplication
import ru.subnak.sapr.presentation.adapter.NodeListAdapter
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
    private lateinit var nodeListAdapter: NodeListAdapter

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
        setupClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.btnAddNode.setOnClickListener {
            nodesDialog()
        }
        binding.btnAddRod.setOnClickListener {
            val rodListSize = constructionViewModel.getRodListSize()
            val nodeListSize = constructionViewModel.getNodeListSize()
            if (rodListSize < nodeListSize - 1) {
                rodsDialog()
            } else {
                Toast.makeText(requireContext(), R.string.toast_rod_to_many, Toast.LENGTH_LONG)
                    .show()
            }
        }


    }

    private fun nodesDialog(node: Node? = null, position: Int? = null) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val nodeBinding = DialogNodeBinding.inflate(layoutInflater)
        alertDialog.setView(nodeBinding.root)
        observeViewModelForNodeDialog(nodeBinding)
        nodeEditTextAddTextChangedListeners(nodeBinding)
        if (node !== null) {
            if (position !== null) {
                nodeBinding.etNodeCoordX.setText(node.x.toString())
                nodeBinding.etNodeLoadConcentrated.setText(node.loadConcentrated.toString())
                nodeBinding.cbNodeProp.isChecked = node.prop

                nodeBinding.buttonNodeApply.setOnClickListener {
                    val closeDialog = constructionViewModel.editNode(
                        nodeBinding.etNodeCoordX.text?.toString(),
                        nodeBinding.etNodeLoadConcentrated.text?.toString(),
                        nodeBinding.cbNodeProp.isChecked,
                        position
                    )
                    if (closeDialog) {
                        alertDialog.dismiss()
                    }
                }
            }
        } else {
            nodeBinding.buttonNodeApply.setOnClickListener {
                val closeDialog = constructionViewModel.addNode(
                    nodeBinding.etNodeCoordX.text?.toString(),
                    nodeBinding.etNodeLoadConcentrated.text?.toString(),
                    nodeBinding.cbNodeProp.isChecked
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

    private fun observeViewModelForNodeDialog(nodeBinding: DialogNodeBinding) {
        constructionViewModel.errorInputX.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.dialog_node_error_input_x)
            } else {
                null
            }
            nodeBinding.etNodeCoordX.error = message
        }
    }

    private fun nodeEditTextAddTextChangedListeners(nodeBinding: DialogNodeBinding) {
        nodeBinding.etNodeCoordX.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                constructionViewModel.resetErrorInputX()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun rodsDialog(rod: Rod? = null, position: Int? = null) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val rodBinding = DialogRodBinding.inflate(layoutInflater)
        alertDialog.setView(rodBinding.root)
        observeViewModelForRodDialog(rodBinding)
        rodEditTextAddTextChangedListeners(rodBinding)
        if (rod !== null) {
            if (position !== null) {
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
                        position
                    )
                    if (closeDialog) {
                        alertDialog.dismiss()
                    }
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
        setupNodeRecyclerView()
        setupRodRecyclerView()
    }

    private fun setupNodeRecyclerView() {
        val rvNodeList = binding.rvNodes
        rvNodeList.setHasFixedSize(false)
        rvNodeList.isNestedScrollingEnabled = false
        nodeListAdapter = NodeListAdapter()
        rvNodeList.adapter = nodeListAdapter
        rvNodeList.layoutManager = LinearLayoutManager(requireContext())
        setupNodeRecyclerViewClickListeners(rvNodeList)
    }

    private fun setupRodRecyclerView() {
        val rvRodList = binding.rvRods
        rvRodList.setHasFixedSize(false)
        rvRodList.isNestedScrollingEnabled = false
        rodListAdapter = RodListAdapter()
        rvRodList.adapter = rodListAdapter
        rvRodList.layoutManager = LinearLayoutManager(requireContext())
        setupRodRecyclerViewClickListeners(rvRodList)
    }

    private fun setupNodeRecyclerViewClickListeners(rvNodeList: RecyclerView) {
        nodeListAdapter.onNodeListClickListener = { node, view ->
            val position = rvNodeList.getChildAdapterPosition(view)
            nodesDialog(node, position)
        }
        nodeListAdapter.onNodeListLongClickListener = { node, view ->
            val position = rvNodeList.getChildAdapterPosition(view)
            constructionViewModel.deleteNode(node, position)
        }
    }

    private fun setupRodRecyclerViewClickListeners(rvRodList: RecyclerView) {
        rodListAdapter.onRodListClickListener = { rod, view ->
            val position = rvRodList.getChildAdapterPosition(view)
            rodsDialog(rod, position)
        }
        rodListAdapter.onRodListLongClickListener = { rod, view ->
            val position = rvRodList.getChildAdapterPosition(view)
            constructionViewModel.deleteRod(rod, position)
        }
    }

    private fun observeViewModel() {
        constructionViewModel.nodeList.observe(viewLifecycleOwner) {
            nodeListAdapter.submitList(it.toList())
            if (it.isEmpty()) {
                binding.rvNodes.visibility = View.INVISIBLE
                binding.tvEmptyRvNodes.visibility = View.VISIBLE
            } else {
                binding.rvNodes.visibility = View.VISIBLE
                binding.tvEmptyRvNodes.visibility = View.INVISIBLE
            }
        }
        constructionViewModel.rodList.observe(viewLifecycleOwner) {
            rodListAdapter.submitList(it.toList())
            if (it.isEmpty()) {
                binding.rvRods.visibility = View.INVISIBLE
                binding.tvEmptyRvRods.visibility = View.VISIBLE
            } else {
                binding.rvRods.visibility = View.VISIBLE
                binding.tvEmptyRvRods.visibility = View.INVISIBLE
            }
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun launchEditMode() {
        constructionViewModel.getConstruction(constructionId)
        binding.btnSaveConstruction.setOnClickListener {
            if (constructionViewModel.checkPropAndCountOfRods() == ConstructionViewModel.ERROR_TYPE_NULL) {
                constructionViewModel.editConstruction()
            } else if (constructionViewModel.checkPropAndCountOfRods() == ConstructionViewModel.ERROR_TYPE_PROP) {
                Toast.makeText(requireContext(), R.string.toast_need_support, Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), R.string.toast_need_rods, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun launchAddMode() {
        binding.tvEmptyRvNodes.visibility = View.VISIBLE
        binding.tvEmptyRvRods.visibility = View.VISIBLE
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

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
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