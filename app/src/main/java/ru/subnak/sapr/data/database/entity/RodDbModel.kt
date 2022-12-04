package ru.subnak.sapr.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

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
    val tension: Double,
    val loadRunning: Double
)