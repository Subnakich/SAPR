package ru.subnak.sapr.di

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.subnak.sapr.data.database.AppDatabase
import ru.subnak.sapr.data.database.dao.ConstructionDao
import ru.subnak.sapr.data.repositoryimpl.ConstructionRepositoryImpl
import ru.subnak.sapr.domain.repository.ConstructionRepository

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindConstructionRepository(impl: ConstructionRepositoryImpl): ConstructionRepository

    companion object {

        @ApplicationScope
        @Provides
        fun provideConstructionDao(application: Application): ConstructionDao {
            return AppDatabase.getInstance(application).rodDao()
        }
    }
}