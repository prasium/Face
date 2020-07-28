package karthiknaik.Face2Face.app.repository

import karthiknaik.Face2Face.app.db.MeetingDao
import karthiknaik.Face2Face.app.model.Meeting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val meetingDao: MeetingDao) {

    suspend fun addMeetingToDb(meeting: Meeting) = withContext(Dispatchers.IO) {
        meetingDao.insertMeeting(meeting)
    }

}