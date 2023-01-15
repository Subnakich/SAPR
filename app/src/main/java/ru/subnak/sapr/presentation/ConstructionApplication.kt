package ru.subnak.sapr.presentation

import android.app.Application
import ru.subnak.sapr.di.DaggerAppComponent

class ConstructionApplication : Application() {

    val component by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()


        //DynamicColors.applyToActivitiesIfAvailable(this)
    }
}