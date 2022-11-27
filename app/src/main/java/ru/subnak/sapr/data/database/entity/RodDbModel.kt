package ru.subnak.sapr.data.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "rod",
    foreignKeys = [
        ForeignKey(
            entity = ConstructionDbModel::class,
            parentColumns = ["id"],
            childColumns = ["construction_id"],
            onDelete = CASCADE
        )
    ],
    primaryKeys = ["rod_id", "construction_id"],
    indices = [Index("construction_id")]
)
data class RodDbModel(
    @ColumnInfo(name = "rod_id")
    val rodId: Int,
    @ColumnInfo(name = "construction_id")
    var constructionId: Int,
    val square: Double,
    val elasticModule: Double,
    val voltage: Double,
    val loadRunning: Double
)