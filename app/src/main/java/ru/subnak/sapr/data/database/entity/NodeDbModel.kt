package ru.subnak.sapr.data.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "node",
    foreignKeys = [
        ForeignKey(
            entity = ConstructionDbModel::class,
            parentColumns = ["id"],
            childColumns = ["construction_id"],
            onDelete = CASCADE
        )
    ],
    primaryKeys = ["node_id", "construction_id"],
    indices = [Index("construction_id")]
)
data class NodeDbModel(
    @ColumnInfo(name = "node_id")
    val nodeId: Int,
    @ColumnInfo(name = "construction_id")
    var constructionId: Int,
    val x: Double,
    val prop: Boolean,
    val loadConcentrated: Double
)