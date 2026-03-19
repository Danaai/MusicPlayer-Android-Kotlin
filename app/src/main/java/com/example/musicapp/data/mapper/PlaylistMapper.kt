package com.example.musicapp.data.mapper

import com.example.musicapp.data.db.entity.PlaylistEntity
import com.example.musicapp.domain.model.Playlist
import java.text.SimpleDateFormat
import java.util.*

fun PlaylistEntity.toDomain(
    ownerName: String,
    songCount: Int
): Playlist {

    return Playlist(
        id = id,
        name = name,
        description = description,
        ownerId = ownerUserId,
        ownerName = ownerName,
        imageUrl = imageUrl,
        songCount = songCount,
        isPublic = isPublic,
        createdAt = createdAt
    )
}