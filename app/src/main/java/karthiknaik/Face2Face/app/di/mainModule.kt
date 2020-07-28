package karthiknaik.Face2Face.app.di

import karthiknaik.Face2Face.app.repository.MainRepository
import karthiknaik.Face2Face.app.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { MainRepository(get()) }
    viewModel { MainViewModel(get()) }

}
