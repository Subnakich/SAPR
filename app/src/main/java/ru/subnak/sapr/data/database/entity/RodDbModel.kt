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
    indices = [Index("construction_id")]
)
data class RodDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rod_id")
    val id: Int,
    @ColumnInfo(name = "construction_id")
    var constructionId: Int,
    val square: Int,
    val elasticModule: Int,
    val voltage: Int,
    val loadRunning: Int
)