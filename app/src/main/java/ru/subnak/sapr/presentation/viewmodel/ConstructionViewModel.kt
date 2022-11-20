package ru.subnak.sapr.presentation.viewmodel

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
import javax.inject.Inject

class ConstructionViewModel @Inject constructor(
    private val addConstructionUseCase: AddConstructionUseCase,
    private val getConstructionUseCase: GetConstructionUseCase,
): ViewModel() {

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    private val _rodList = MutableLiveData<List<Rod>>()
    val rodList: LiveData<List<Rod>>
        get() = _rodList

    private val _knotList = MutableLiveData<List<Knot>>()
    val knotList: LiveData<List<Knot>>
        get() = _knotList

    //var rodList: MutableList<Rod> = mutableListOf()
    //var knotList: MutableList<Knot> = mutableListOf()

    fun addRod(construction: Construction) {
        viewModelScope.launch {
            addConstructionUseCase.invoke(construction)
        }
    }

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch{
            val construction = getConstructionUseCase.invoke(constructionId)
            _construction.value = construction
            _rodList.value = construction.rodValues.toMutableList()
            _knotList.value = construction.knotValues.toMutableList()
            //Log.d("ConstrViewModel", construction.toString())
        }
    }
}