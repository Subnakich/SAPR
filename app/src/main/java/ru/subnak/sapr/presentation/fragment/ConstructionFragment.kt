package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.subnak.sapr.databinding.FragmentConstructionBinding
import ru.subnak.sapr.domain.model.Construction
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
    private var knotCount: Int = KNOT_COUNT_DEFAULT

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
        constructionViewModel = ViewModelProvider(this, viewModelFactory)[ConstructionViewModel::class.java]
        setupRecyclerView()
        constructionViewModel.construction.observe(viewLifecycleOwner){

        }
        constructionViewModel.rodList.observe(viewLifecycleOwner){
            rodListAdapter.submitList(it)
        }
        constructionViewModel.knotList.observe(viewLifecycleOwner){
            knotListAdapter.submitList(it)
        }

        launchRightMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        setupRodRecyclerView()
        setupKnotRecyclerView()
    }

    private fun setupRodRecyclerView() {
        val rvRodList = binding.rvRods
        rodListAdapter = RodListAdapter()
        rvRodList.adapter = rodListAdapter
        rvRodList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupKnotRecyclerView() {
        val rvKnotList = binding.rvKnots
        knotListAdapter = KnotListAdapter()
        rvKnotList.adapter = knotListAdapter
        rvKnotList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun launchEditMode() {
        constructionViewModel.getConstruction(constructionId)
//        binding.buttonSave.setOnClickListener {
//            shopItemViewModel.editShopItem(
//                binding.etName.text?.toString(),
//                binding.etCount.text?.toString()
//            )
//        }
    }

    private fun launchAddMode() {
//        binding.buttonSave.setOnClickListener {
//            shopItemViewModel.addShopItem(
//                binding.etName.text?.toString(),
//                binding.etCount.text?.toString()
//            )
//        }
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
            throw RuntimeException("Param screen mode is absent")
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
        } else {
            if (!args.containsKey(KNOT_COUNT)) {
                throw RuntimeException("Param knot count is absent")
            }
            knotCount = args.getInt(KNOT_COUNT, KNOT_COUNT_DEFAULT)
        }
    }

    companion object {

        private const val KNOT_COUNT_DEFAULT = 0

        private const val KNOT_COUNT = "extra_knot_count"
        private const val SCREEN_MODE = "extra_mode"
        private const val CONSTRUCTION_ID = "extra_construction_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddConstruction(knotCount: Int) =
            ConstructionFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                    putInt(KNOT_COUNT, knotCount)
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