package ru.subnak.sapr.data.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import ru.subnak.sapr.data.database.entity.ConstructionDbModel
import ru.subnak.sapr.data.database.entity.NodeDbModel
import ru.subnak.sapr.data.database.entity.RodDbModel

data class ConstructionWithValuesDbModel(

    @Embedded
    val constructionDbModel: ConstructionDbModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "construction_id",
        entity = RodDbModel::class
    )
    val rodDbModels: List<RodDbModel>,
    @Relation(
        parentColumn = "id",
        entityColumn = "construction_id",
        entity = NodeDbModel::class
    )
    val nodeDbModels: List<NodeDbModel>
)
