package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.subnak.sapr.databinding.FragmentCalculatingBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.presentation.ConstructionApplication
import ru.subnak.sapr.presentation.viewmodel.CalculatingViewModel
import ru.subnak.sapr.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject


class CalculatingFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var calculatingViewModel: CalculatingViewModel

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
        calculatingViewModel =
            ViewModelProvider(this, viewModelFactory)[CalculatingViewModel::class.java]
        calculatingViewModel.getConstruction(constructionId)

        calculatingViewModel.construction.observe(viewLifecycleOwner) {
            binding.constructionImage.setImageBitmap(it.img)

        }


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


        fun newInstanceCalculating(constructionId: Int) =
            CalculatingFragment().apply {
                arguments = Bundle().apply {
                    putInt(CONSTRUCTION_ID, constructionId)
                }
            }
    }
}