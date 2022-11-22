package ru.subnak.sapr.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.subnak.sapr.data.database.entity.ConstructionDbModel
import ru.subnak.sapr.data.database.entity.KnotDbModel
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
        construction.rodDbModels.forEach {
            it.constructionId = list.toInt()
        }
        construction.knotDbModels.forEach {
            it.constructionId = list.toInt()
        }
        addKnots(construction.knotDbModels)
        addRods(construction.rodDbModels)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addConstruction(construction: ConstructionDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRods(rods: List<RodDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addKnots(knots: List<KnotDbModel>)

    @Query("SELECT * FROM construction")
    fun getConstructionList(): LiveData<List<ConstructionDbModel>>
}