package ru.subnak.sapr.data.repositoryimpl

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.subnak.sapr.data.database.dao.ConstructionDao
import ru.subnak.sapr.data.mapper.ConstructionMapper
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.repository.ConstructionRepository
import javax.inject.Inject

class ConstructionRepositoryImpl @Inject constructor(
    private val mapper: ConstructionMapper,
    private val constructionDao: ConstructionDao
) : ConstructionRepository {
    override suspend fun getConstruction(constructionId: Int): Construction {
        return mapper.mapConstructionDbModelWithValuesToEntity(
            constructionDao.getConstruction(constructionId)
        )
    }

    override suspend fun addConstruction(construction: Construction) {
        constructionDao.addConstruction(mapper.mapConstructionWithValuesEntityToDbModel(construction))
    }

    override suspend fun deleteConstruction(construction: Construction) {
        constructionDao.deleteConstruction(construction.id)
    }

    override fun getConstructionList(): LiveData<List<Construction>> = Transformations.map(
        constructionDao.getConstructionList()
    ) {
        mapper.mapListConstructionToEntity(it)
    }
}