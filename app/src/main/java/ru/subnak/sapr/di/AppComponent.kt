package ru.subnak.sapr.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.subnak.sapr.presentation.fragment.CalculatingFragment
import ru.subnak.sapr.presentation.fragment.ConstructionFragment
import ru.subnak.sapr.presentation.fragment.MainFragment

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        VIewModelModule::class
    ]
)
interface AppComponent {

    fun inject(fragment: MainFragment)
    fun inject(fragment: ConstructionFragment)
    fun inject(fragment: CalculatingFragment)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }
}