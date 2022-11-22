package ru.subnak.sapr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ru.subnak.sapr.domain.usecase.AddConstructionUseCase
import ru.subnak.sapr.domain.usecase.DeleteConstructionUseCase
import ru.subnak.sapr.domain.usecase.GetConstructionListUseCase
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val deleteConstructionUseCase: DeleteConstructionUseCase,
    private val getConstructionListUseCase: GetConstructionListUseCase
) : ViewModel() {

    val constructionList = getConstructionListUseCase.invoke()

}