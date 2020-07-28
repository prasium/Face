package karthiknaik.Face2Face.app.di

import karthiknaik.Face2Face.app.db.Face2FaceDB
import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single { provideRoomDatabase(androidApplication()) }
    single { provideMeetingDao(get()) }
}

private fun provideRoomDatabase(androidApplication: Application) =
    Room.databaseBuilder(androidApplication, Face2FaceDB::class.java, "Face2Face-db").build()

private fun provideMeetingDao(Face2FaceDB: Face2FaceDB) =
    Face2FaceDB.meetingDao()