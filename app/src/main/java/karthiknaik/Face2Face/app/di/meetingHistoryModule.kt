package karthiknaik.Face2Face.app.di

import karthiknaik.Face2Face.app.repository.MeetingHistoryRepository
import karthiknaik.Face2Face.app.viewmodel.MeetingHistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val meetingHistoryModule = module {

    single { MeetingHistoryRepository(get()) }
    viewModel { MeetingHistoryViewModel(get()) }

}
