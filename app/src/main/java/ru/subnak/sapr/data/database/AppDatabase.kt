package ru.subnak.sapr.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.subnak.sapr.data.database.dao.ConstructionDao
import ru.subnak.sapr.data.database.entity.ConstructionDbModel
import ru.subnak.sapr.data.database.entity.NodeDbModel
import ru.subnak.sapr.data.database.entity.RodDbModel

@Database(
    entities = [
        ConstructionDbModel::class,
        RodDbModel::class,
        NodeDbModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rodDao(): ConstructionDao

    companion object {

        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "sapr.db"

        fun getInstance(application: Application): AppDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}