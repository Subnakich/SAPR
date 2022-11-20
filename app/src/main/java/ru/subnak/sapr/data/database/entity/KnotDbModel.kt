package ru.subnak.sapr.data.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "knot",
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
data class KnotDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "knot_id")
    val id: Int,
    @ColumnInfo(name = "construction_id")
    var constructionId: Int,
    val x: Int,
    val prop: Boolean,
    val loadConcentrated: Int
)