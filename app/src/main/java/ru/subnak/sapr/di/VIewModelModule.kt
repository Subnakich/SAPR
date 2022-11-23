package ru.subnak.sapr.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.subnak.sapr.presentation.viewmodel.CalculatingViewModel
import ru.subnak.sapr.presentation.viewmodel.ConstructionViewModel
import ru.subnak.sapr.presentation.viewmodel.MainViewModel

@Module
interface VIewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConstructionViewModel::class)
    fun bindConstructionViewModel(viewModel: ConstructionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CalculatingViewModel::class)
    fun bindCalculatingViewModel(viewModel: CalculatingViewModel): ViewModel
}