package ru.subnak.sapr.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.domain.usecase.GetConstructionUseCase
import ru.subnak.sapr.presentation.CalculateComponent
import ru.subnak.sapr.presentation.CalculateComponent.CalculatedResult
import javax.inject.Inject


class CalculatingViewModel @Inject constructor(
    private val getConstructionUseCase: GetConstructionUseCase
) : ViewModel() {

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    private val _errorInputX = MutableLiveData<Boolean>()
    val errorInputX: LiveData<Boolean>
        get() = _errorInputX

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

    private var result: CalculatedResult? = null
    private var rodNumber = 1

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch {
            val construction = getConstructionUseCase.invoke(constructionId)
            _construction.value = construction
            nodeList.addAll(construction.nodeValues.toMutableList())
        }
    }

    fun setResult(construction: Construction) {
        result = CalculateComponent().calculate(construction)
    }

    fun setRodNumber(number: Int) {
        rodNumber = number
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
            _resultSx.value = result?.Sx?.get(rodNumber - 1)?.invoke(x)
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
}

