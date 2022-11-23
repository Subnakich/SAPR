package ru.subnak.sapr.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.usecase.GetConstructionUseCase
import javax.inject.Inject

class CalculatingViewModel @Inject constructor(
    private val getConstructionUseCase: GetConstructionUseCase
) : ViewModel() {

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch {
            _construction.value = getConstructionUseCase.invoke(constructionId)
        }
    }
}