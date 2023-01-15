package ru.subnak.sapr.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.domain.usecase.GetConstructionUseCase
import ru.subnak.sapr.presentation.CalculateComponent
import ru.subnak.sapr.presentation.CalculateComponent.CalculatedResult
import javax.inject.Inject
import kotlin.math.abs


class CalculatingViewModel @Inject constructor(
    private val getConstructionUseCase: GetConstructionUseCase
) : ViewModel() {

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    private val _errorInputX = MutableLiveData<Boolean>()
    val errorInputX: LiveData<Boolean>
        get() = _errorInputX

    private val _errorSx = MutableLiveData<Boolean>()
    val errorSx: LiveData<Boolean>
        get() = _errorSx

    private val _resultNx = MutableLiveData<Double>()
    val resultNx: LiveData<Double>
        get() = _resultNx

    private val _resultSx = MutableLiveData<Double>()
    val resultSx: LiveData<Double>
        get() = _resultSx

    private val _resultUx = MutableLiveData<Double>()
    val resultUx: LiveData<Double>
        get() = _resultUx

    private val nodeList = mutableListOf<Node>()
    private val rodList = mutableListOf<Rod>()

    private var result: CalculatedResult? = null

    var rodNumber = 1

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch {
            val construction = getConstructionUseCase.invoke(constructionId)
            _construction.value = construction
            nodeList.addAll(construction.nodeValues.toMutableList())
            rodList.addAll(construction.rodValues.toMutableList())
        }
    }

    fun setResult(construction: Construction) {
        result = CalculateComponent().calculate(construction)
    }

    private fun parseDouble(s: String?): Double {
        return try {
            s?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun calculateLocalResult(inputX: String?) {
        val x = parseDouble(inputX)
        val validField = validateInputX(x)
        if (validField) {
            _resultUx.value = result?.Ux?.get(rodNumber - 1)?.invoke(x)
            _resultNx.value = result?.Nx?.get(rodNumber - 1)?.invoke(x)
            val sx = result?.Sx?.get(rodNumber - 1)?.invoke(x)
            _resultSx.value = sx
            sx?.let {
                if (abs(it) > rodList[rodNumber - 1].tension) {
                    _errorSx.value = true
                }
            }
        }
    }

    private fun validateInputX(x: Double): Boolean {
        var result = true
        val xDivide = if (rodNumber > 1) {
            nodeList[rodNumber - 1].x
        } else {
            0.0
        }
        if (x > nodeList[rodNumber].x - xDivide) {
            result = false
            _errorInputX.value = true
        }
        return result
    }

    fun resetErrorSx() {
        _errorSx.value = false
    }
}

