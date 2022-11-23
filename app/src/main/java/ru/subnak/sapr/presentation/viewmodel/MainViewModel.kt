package ru.subnak.sapr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.usecase.DeleteConstructionUseCase
import ru.subnak.sapr.domain.usecase.GetConstructionListUseCase
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val deleteConstructionUseCase: DeleteConstructionUseCase,
    private val getConstructionListUseCase: GetConstructionListUseCase
) : ViewModel() {

    val constructionList = getConstructionListUseCase.invoke()

    fun deleteConstruction(construction: Construction) {
        viewModelScope.launch {
            deleteConstructionUseCase.invoke(construction)
        }
    }
}