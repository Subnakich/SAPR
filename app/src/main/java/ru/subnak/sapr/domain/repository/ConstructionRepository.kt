package ru.subnak.sapr.domain.repository

import androidx.lifecycle.LiveData
import ru.subnak.sapr.domain.model.Construction

interface ConstructionRepository {

    suspend fun getConstruction(constructionId: Int): Construction

    suspend fun addConstruction(construction: Construction)

    suspend fun editConstruction(construction: Construction)

    suspend fun deleteConstruction(construction: Construction)

    fun getConstructionList(): LiveData<List<Construction>>
}