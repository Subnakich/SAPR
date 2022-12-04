package ru.subnak.sapr.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.domain.usecase.AddConstructionUseCase
import ru.subnak.sapr.domain.usecase.EditConstructionUseCase
import ru.subnak.sapr.domain.usecase.GetConstructionUseCase
import ru.subnak.sapr.presentation.BitmapCache
import ru.subnak.sapr.presentation.ConstructionDrawable
import javax.inject.Inject

class ConstructionViewModel @Inject constructor(
    private val addConstructionUseCase: AddConstructionUseCase,
    private val getConstructionUseCase: GetConstructionUseCase,
    private val editConstructionUseCase: EditConstructionUseCase
) : ViewModel() {

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    private val _rodMutableList = mutableListOf<Rod>()
    private val _rodList = MutableLiveData<List<Rod>>()
    val rodList: LiveData<List<Rod>>
        get() = _rodList

    private val _nodeMutableList = mutableListOf<Node>()
    private val _nodeList = MutableLiveData<List<Node>>()
    val nodeList: LiveData<List<Node>>
        get() = _nodeList

    private val _errorInputX = MutableLiveData<Boolean>()
    val errorInputX: LiveData<Boolean>
        get() = _errorInputX

    private val _errorInputSquare = MutableLiveData<Boolean>()
    val errorInputSquare: LiveData<Boolean>
        get() = _errorInputSquare

    private val _errorInputElasticModule = MutableLiveData<Boolean>()
    val errorInputElasticModule: LiveData<Boolean>
        get() = _errorInputElasticModule

    private val _errorInputTension = MutableLiveData<Boolean>()
    val errorInputTension: LiveData<Boolean>
        get() = _errorInputTension

    fun addConstruction() {
        viewModelScope.launch {
            val construction = Construction(
                System.currentTimeMillis(),
                _nodeMutableList,
                _rodMutableList
            )
            val bitmap = getBitmap(construction)
//            val bitmap = getBitmap(construction)?.let {
//                BitmapCache().saveToCacheAndGetUri(it)
//            }
            val constructionForSave = Construction(
                System.currentTimeMillis(),
                _nodeMutableList,
                _rodMutableList,
                bitmap
            )
            addConstructionUseCase.invoke(constructionForSave)
        }
    }

    fun editConstruction() {
        viewModelScope.launch {
            _construction.value?.let {
                val construction = it.copy(
                    nodeValues = _nodeMutableList.toList(),
                    rodValues = _rodMutableList.toList()
                )
                val bitmap = getBitmap(construction)
                val constructionForSave = it.copy(
                    nodeValues = _nodeMutableList.toList(),
                    rodValues = _rodMutableList.toList(),
                    img = bitmap
                )
                editConstructionUseCase.invoke(constructionForSave)
            }

        }
    }

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch {
            val construction = getConstructionUseCase.invoke(constructionId)
            _construction.value = construction
            _rodMutableList.addAll(construction.rodValues.toList())
            _nodeMutableList.addAll(construction.nodeValues.toList())
            _rodList.value = construction.rodValues.toList()
            _nodeList.value = construction.nodeValues.toList()
        }
    }

    private fun getBitmap(construction: Construction): Bitmap? {
        val drawable = ConstructionDrawable(construction)

        val bitmap = Bitmap.createBitmap(
            2000,
            1000,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun checkPropAndCountOfRods(): Int {
        var result = ERROR_NULL

        if (_nodeMutableList.size - _rodMutableList.size == 1) {
            _nodeMutableList.forEach {
                if (it.prop) {
                    return ERROR_NULL
                } else {
                    result = ERROR_PROP
                }
            }
        } else {
            result = ERROR_ROD
        }
        return result
    }


    fun addNode(
        inputX: String?,
        inputLoadConcentrated: String?,
        inputProp: Boolean
    ): Boolean {
        val x = parseDouble(inputX)
        val loadConcentrated = parseDouble(inputLoadConcentrated)
        val validFields = validateInputAddNode(x)
        if (validFields) {
            val node = Node(
                x,
                loadConcentrated,
                inputProp,
                _nodeMutableList.size
            )
            _nodeMutableList.add(node)
            _nodeList.value = _nodeMutableList.toList()
            return true
        }
        return false
    }

    fun editNode(
        inputX: String?,
        inputLoadConcentrated: String?,
        inputProp: Boolean,
        nodeIndex: Int
    ): Boolean {
        val x = parseDouble(inputX)
        val loadConcentrated = parseDouble(inputLoadConcentrated)
        val validFields = validateInputEditNode(x, nodeIndex)
        if (validFields) {
            val node = Node(x, loadConcentrated, inputProp, nodeIndex)
            _nodeMutableList[nodeIndex] = node
            _nodeList.value = _nodeMutableList.toList()
            return true
        }
        return false
    }

    fun deleteNode(node: Node, nodeIndex: Int) {
        _nodeMutableList.remove(node)
        _nodeMutableList.forEachIndexed { index, item ->
            if (index >= nodeIndex) {
                _nodeMutableList[index] = item.copy(nodeId = index)
            }
        }
        if (_rodMutableList.isNotEmpty()) {
            if (_rodMutableList.size >= _nodeMutableList.size) {
                _rodMutableList.removeLast()
                _rodList.value = _rodMutableList.toList()
            }
        }
        _nodeList.value = _nodeMutableList.toList()
    }

    fun addRod(
        inputSquare: String?,
        inputElasticModule: String?,
        inputLoadRunning: String?,
        inputVoltage: String?
    ): Boolean {
        val square = parseDouble(inputSquare)
        val elasticModule = parseDouble(inputElasticModule)
        val loadRunning = parseDouble(inputLoadRunning)
        val voltage = parseDouble(inputVoltage)
        val validFields = validateInputRod(square, elasticModule, voltage)
        if (validFields) {
            val rod = Rod(
                square,
                elasticModule,
                voltage,
                loadRunning,
                _rodMutableList.size
            )
            _rodMutableList.add(rod)
            _rodList.value = _rodMutableList.toList()
            return true
        }
        return false
    }

    fun editRod(
        inputSquare: String?,
        inputElasticModule: String?,
        inputLoadRunning: String?,
        inputVoltage: String?,
        rodIndex: Int
    ): Boolean {
        val square = parseDouble(inputSquare)
        val elasticModule = parseDouble(inputElasticModule)
        val loadRunning = parseDouble(inputLoadRunning)
        val voltage = parseDouble(inputVoltage)
        val validFields = validateInputRod(square, elasticModule, voltage)
        if (validFields) {
            val rod = Rod(
                square,
                elasticModule,
                voltage,
                loadRunning,
                rodIndex
            )
            _rodMutableList[rodIndex] = rod
            _rodList.value = _rodMutableList.toList()
            return true
        }
        return false
    }

    fun deleteRod(rod: Rod, rodIndex: Int) {
        _rodMutableList.remove(rod)
        _rodMutableList.forEachIndexed { index, item ->
            if (index >= rodIndex) {
                _rodMutableList[index] = item.copy(rodId = index)
            }
        }
        _rodList.value = _rodMutableList.toList()
    }

    private fun parseDouble(s: String?): Double {
        return try {
            s?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun validateInputAddNode(x: Double): Boolean {
        var result = true
        if (_nodeMutableList.isNotEmpty()) {
            if (x <= _nodeMutableList.last().x) {
                result = false
                _errorInputX.value = true
            }
        }
        return result
    }

    private fun validateInputEditNode(x: Double, nodeIndex: Int): Boolean {
        var result = true
        _nodeMutableList.forEachIndexed { index, node ->
            if (nodeIndex > index) {
                if (x <= node.x) {
                    result = false
                    _errorInputX.value = true
                }
            } else if (nodeIndex < index) {
                if (x > node.x) {
                    result = false
                    _errorInputX.value = true
                }
            }
        }
        return result
    }

    private fun validateInputRod(
        square: Double,
        elasticModule: Double,
        voltage: Double
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
            _errorInputTension.value = true
        }
        return result
    }

    fun getNodeListSize(): Int {
        return _nodeMutableList.size
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

    fun resetErrorInputTension() {
        _errorInputTension.value = false
    }

    companion object {

        const val ERROR_NULL = 100
        const val ERROR_PROP = 101
        const val ERROR_ROD = 102
    }
}