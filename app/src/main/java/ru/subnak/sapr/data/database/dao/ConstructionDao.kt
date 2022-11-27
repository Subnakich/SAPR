package ru.subnak.sapr.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.subnak.sapr.data.database.entity.ConstructionDbModel
import ru.subnak.sapr.data.database.entity.NodeDbModel
import ru.subnak.sapr.data.database.entity.RodDbModel
import ru.subnak.sapr.data.database.relation.ConstructionWithValuesDbModel

@Dao
interface ConstructionDao {

    @Transaction
    @Query("SELECT * FROM construction WHERE id=:constructionId")
    suspend fun getConstruction(constructionId: Int): ConstructionWithValuesDbModel

    @Query("DELETE FROM construction WHERE id=:constructionId")
    suspend fun deleteConstruction(constructionId: Int)

    @Transaction
    suspend fun addConstruction(construction: ConstructionWithValuesDbModel) {
        val list = addConstruction(construction.constructionDbModel)
        construction.nodeDbModels.forEach {
            it.constructionId = list.toInt()
        }
        construction.rodDbModels.forEach {
            it.constructionId = list.toInt()
        }
        addNodes(construction.nodeDbModels)
        addRods(construction.rodDbModels)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addConstruction(construction: ConstructionDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRods(rods: List<RodDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNodes(knots: List<NodeDbModel>)

    @Query("SELECT * FROM construction")
    fun getConstructionList(): LiveData<List<ConstructionDbModel>>
}