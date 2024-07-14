package com.denine.diaryapp.data.repository

import com.denine.diaryapp.model.Diary
import com.denine.diaryapp.model.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate,List<Diary>>>

interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries():Flow<Diaries>
    fun getSelectedDiary(diaryId:ObjectId): Flow<RequestState<Diary>>
}