package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.FragmentMainBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.presentation.ConstructionApplication
import ru.subnak.sapr.presentation.adapter.ConstructionListAdapter
import ru.subnak.sapr.presentation.viewmodel.MainViewModel
import ru.subnak.sapr.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject

class MainFragment : Fragment() {


    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding ?: throw RuntimeException("FragmentMainBinding == null")

    private lateinit var constructionListAdapter: ConstructionListAdapter

    private val component by lazy {
        (requireActivity().application as ConstructionApplication).component
    }

    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        val rv = setupRecyclerView()
        viewModel.constructionList.observe(viewLifecycleOwner) {
            constructionListAdapter.submitList(it) {
                rv.scrollToPosition(constructionListAdapter.itemCount-1)
            }
        }
        setupButtonAdd()
    }

    private fun setupButtonAdd() {
        binding.floatingActionButton.setOnClickListener {
            launchFragment(ConstructionFragment.newInstanceAddConstruction())
        }
    }

    private fun setupRecyclerView(): RecyclerView {
        val rvConstructionList = binding.rvHistory
        constructionListAdapter = ConstructionListAdapter()
        rvConstructionList.adapter = constructionListAdapter
        val layoutManager =
            LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvConstructionList.layoutManager = layoutManager
        setupClickListener()
        return rvConstructionList
    }

    private fun setupClickListener() {
        constructionListAdapter.onConstructionListClickListener = {
            val fragment = CalculatingFragment.newInstanceCalculating(it.id)
            launchFragment(fragment)
        }
        constructionListAdapter.onConstructionListLongClickListener = {
            AlertDialog.Builder(requireContext())
                .setItems(R.array.dialog_choose_action_construction) { d, c ->
                    when (c) {
                        0 -> launchFragment(CalculatingFragment.newInstanceCalculating(it.id))
                        1 -> launchFragment(ConstructionFragment.newInstanceEditConstruction(it.id))
                        2 -> deleteDialog(it)
                    }
                    d.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun deleteDialog(construction: Construction) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_message_construction)
            .setPositiveButton(R.string.yes) { d, _ ->
                viewModel.deleteConstruction(construction)
                d.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel) { d, _ ->
                d.dismiss()
            }
            .create()
            .show()
    }

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }


}