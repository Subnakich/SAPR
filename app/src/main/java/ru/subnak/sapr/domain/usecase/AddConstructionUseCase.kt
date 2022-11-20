package ru.subnak.sapr.domain.usecase

import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.repository.ConstructionRepository
import javax.inject.Inject

class AddConstructionUseCase @Inject constructor(
    private val repository: ConstructionRepository
) {

    suspend fun invoke(construction: Construction) = repository.addConstruction(construction)
}