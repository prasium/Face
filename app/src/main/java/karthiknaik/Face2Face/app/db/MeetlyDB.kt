package karthiknaik.Face2Face.app.db

import karthiknaik.Face2Face.app.model.Meeting
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Meeting::class], version = 1, exportSchema = false)
abstract class Face2FaceDB : RoomDatabase() {

    abstract fun meetingDao(): MeetingDao

}