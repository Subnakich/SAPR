package ru.subnak.sapr.domain.usecase

import ru.subnak.sapr.domain.repository.ConstructionRepository
import javax.inject.Inject

class GetConstructionUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {

    suspend fun invoke(constructionId: Int) = repository.getConstruction(constructionId)
}