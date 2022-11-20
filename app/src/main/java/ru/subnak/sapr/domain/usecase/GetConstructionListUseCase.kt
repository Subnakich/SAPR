package ru.subnak.sapr.domain.usecase

import ru.subnak.sapr.domain.repository.ConstructionRepository
import javax.inject.Inject

class GetConstructionListUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {

    fun invoke() = repository.getConstructionList()
}