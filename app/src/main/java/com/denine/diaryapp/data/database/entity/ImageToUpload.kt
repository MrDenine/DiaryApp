package com.denine.diaryapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.denine.diaryapp.utils.Constants.IMAGE_TO_DELETE_TABLE
import com.denine.diaryapp.utils.Constants.IMAGE_TO_UPLOAD_TABLE


@Entity(tableName = IMAGE_TO_UPLOAD_TABLE)
data class ImageToUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)