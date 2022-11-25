package ru.subnak.sapr.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Knot
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.domain.usecase.AddConstructionUseCase
import ru.subnak.sapr.domain.usecase.GetConstructionUseCase
import ru.subnak.sapr.presentation.ConstructionDrawable
import javax.inject.Inject

class ConstructionViewModel @Inject constructor(
    private val addConstructionUseCase: AddConstructionUseCase,
    private val getConstructionUseCase: GetConstructionUseCase,
) : ViewModel() {

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    private val _rodMutableList = mutableListOf<Rod>()
    private val _rodList = MutableLiveData<List<Rod>>()
    val rodList: LiveData<List<Rod>>
        get() = _rodList

    private val _knotMutableList = mutableListOf<Knot>()
    private val _knotList = MutableLiveData<List<Knot>>()
    val knotList: LiveData<List<Knot>>
        get() = _knotList

    // Input errors
    private val _errorInputX = MutableLiveData<Boolean>()
    val errorInputX: LiveData<Boolean>
        get() = _errorInputX

    private val _errorInputSquare = MutableLiveData<Boolean>()
    val errorInputSquare: LiveData<Boolean>
        get() = _errorInputSquare

    private val _errorInputElasticModule = MutableLiveData<Boolean>()
    val errorInputElasticModule: LiveData<Boolean>
        get() = _errorInputElasticModule

    private val _errorInputVoltage = MutableLiveData<Boolean>()
    val errorInputVoltage: LiveData<Boolean>
        get() = _errorInputVoltage

    fun addConstruction() {
        viewModelScope.launch {
            val construction = Construction(
                System.currentTimeMillis(),
                _knotMutableList,
                _rodMutableList,
            )
            val bitmap = createBitmapForSave(construction)
            val constructionForSave = Construction(
                System.currentTimeMillis(),
                _knotMutableList,
                _rodMutableList,
                bitmap
            )
            addConstructionUseCase.invoke(constructionForSave)
        }
    }

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch {
            val construction = getConstructionUseCase.invoke(constructionId)
            _construction.value = construction
            _rodMutableList.addAll(construction.rodValues)
            _knotMutableList.addAll(construction.knotValues)
            _rodList.value = construction.rodValues
            _knotList.value = construction.knotValues
        }
    }

    private fun createBitmapForSave(construction: Construction): Bitmap? {
        val myDrawing = ConstructionDrawable(construction)
        return createBitmap(myDrawing)
    }

    private fun createBitmap(drawable: Drawable): Bitmap? {

        val bitmap = Bitmap.createBitmap(
            getConstructionImageWidth(),
            800,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getConstructionImageWidth(): Int {
        return _knotMutableList.last().x + _knotMutableList.size * 500
    }

    fun deleteKnotFromList(knot: Knot) {
        _knotMutableList.remove(knot)
        _knotList.value = _knotMutableList
    }

    fun checkPropAndCountOfRods(): Int {
        var result = ERROR_TYPE_NULL

        if (_knotMutableList.size - _rodMutableList.size == 1) {
            _knotMutableList.forEach {
                if (it.prop) {
                    return ERROR_TYPE_NULL
                } else {
                    result = ERROR_TYPE_PROP
                }
            }
        } else {
            result = ERROR_TYPE_ROD
        }
        return result
    }

    fun addKnot(inputX: String?, inputLoadConcentrated: String?, inputProp: Boolean): Boolean {
        val x = parseInt(inputX)
        val loadConcentrated = parseInt(inputLoadConcentrated)
        val validFields = validateInputAddKnot(x)
        if (validFields) {
            val knot = Knot(x, loadConcentrated, inputProp, _knotMutableList.size + 1)
            _knotMutableList.add(knot)
            _knotList.value = _knotMutableList
            return true
        }
        return false
    }

    fun editKnot(
        inputX: String?,
        inputLoadConcentrated: String?,
        inputProp: Boolean,
        knotIndex: Int
    ): Boolean {
        val x = parseInt(inputX)
        val loadConcentrated = parseInt(inputLoadConcentrated)
        val validFields = validateInputEditKnot(x, knotIndex)
        if (validFields) {
            Log.d("kke", knotIndex.toString())
            val knot = Knot(x, loadConcentrated, inputProp, knotIndex+1)
            _knotMutableList[knotIndex] = knot
            _knotList.value = _knotMutableList
            return true
        }
        return false
    }

    fun addRod(
        inputSquare: String?,
        inputElasticModule: String?,
        inputLoadRunning: String?,
        inputVoltage: String?
    ): Boolean {
        val square = parseInt(inputSquare)
        val elasticModule = parseInt(inputElasticModule)
        val loadRunning = parseInt(inputLoadRunning)
        val voltage = parseInt(inputVoltage)
        val validFields = validateInputRod(square, elasticModule, voltage)
        if (validFields) {
            val rod = Rod(
                square,
                elasticModule,
                voltage,
                loadRunning,
                _rodMutableList.size + 1
            )
            _rodMutableList.add(rod)
            _rodList.value = _rodMutableList
            return true
        }
        return false
    }

    fun editRod(
        inputSquare: String?,
        inputElasticModule: String?,
        inputLoadRunning: String?,
        inputVoltage: String?,
        rodNumber: Int
    ): Boolean {
        val square = parseInt(inputSquare)
        val elasticModule = parseInt(inputElasticModule)
        val loadRunning = parseInt(inputLoadRunning)
        val voltage = parseInt(inputVoltage)
        val validFields = validateInputRod(square, elasticModule, voltage)
        if (validFields) {
            val rod = Rod(
                square,
                elasticModule,
                voltage,
                loadRunning,
                rodNumber
            )
            _rodMutableList[rodNumber - 1] = rod
            _rodList.value = _rodMutableList
            return true
        }
        return false
    }

    private fun parseInt(s: String?): Int {
        return try {
            s?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInputAddKnot(x: Int): Boolean {
        var result = true
        if (_knotMutableList.isNotEmpty()) {
            if (x <= _knotMutableList.last().x) {
                result = false
                _errorInputX.value = true
            }
        }
        return result
    }

    private fun validateInputEditKnot(x: Int, knotIndex: Int): Boolean {
        var result = true
        _knotMutableList.forEachIndexed { index, knot ->
            if (knotIndex > index) {
                if (x <= knot.x) {
                    result = false
                    _errorInputX.value = true
                }
            } else if (knotIndex < index) {
                if (x > knot.x) {
                    result = false
                    _errorInputX.value = true
                }
            }
        }
        return result
    }


    private fun validateInputRod(
        square: Int,
        elasticModule: Int,
        voltage: Int
    ): Boolean {
        var result = true
        if (square <= 0) {
            result = false
            _errorInputSquare.value = true
        }
        if (elasticModule <= 0) {
            result = false
            _errorInputElasticModule.value = true
        }
        if (voltage <= 0) {
            result = false
            _errorInputVoltage.value = true
        }
        return result
    }

    fun getKnotListSize(): Int {
        return _knotMutableList.size
    }

    fun getRodListSize(): Int {
        return _rodMutableList.size
    }

    fun resetErrorInputX() {
        _errorInputX.value = false
    }

    fun resetErrorInputSquare() {
        _errorInputSquare.value = false
    }

    fun resetErrorInputElasticModule() {
        _errorInputElasticModule.value = false
    }

    fun resetErrorInputVoltage() {
        _errorInputVoltage.value = false
    }

    companion object {

        const val ERROR_TYPE_NULL = 100
        const val ERROR_TYPE_PROP = 101
        const val ERROR_TYPE_ROD = 102
    }
}