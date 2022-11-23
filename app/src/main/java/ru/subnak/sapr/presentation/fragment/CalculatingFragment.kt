package ru.subnak.sapr.presentation.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.subnak.sapr.databinding.FragmentCalculatingBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.presentation.ConstructionApplication
import ru.subnak.sapr.presentation.ConstructionDrawable
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
    private lateinit var construction: Construction


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
            construction = it
            val myDrawing = ConstructionDrawable(construction)
            val bitmap = createBitmap(myDrawing)
            binding.constructionImage.setImageBitmap(bitmap)

        }


    }

    private fun createBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }


        bitmap = Bitmap.createBitmap(
            2000,
            2000,
            Bitmap.Config.ARGB_8888
        )
//        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
//            Bitmap.createBitmap(
//                1,
//                1,
//                Bitmap.Config.ARGB_8888
//            )
//        } else {
//            Bitmap.createBitmap(
//                drawable.intrinsicWidth,
//                drawable.intrinsicHeight,
//                Bitmap.Config.ARGB_8888
//            )
//        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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


        fun newInstanceConstruction(constructionId: Int) =
            CalculatingFragment().apply {
                arguments = Bundle().apply {
                    putInt(CONSTRUCTION_ID, constructionId)
                }
            }
    }
}