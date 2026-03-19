package com.example.musicapp.data.mapper

import com.example.musicapp.data.db.entity.SongEntity
import com.example.musicapp.domain.model.Song

fun SongEntity.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artistId = artistId,
        albumId = albumId,
        durationMs = durationMs,
        genre = genre,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        releaseDate = releaseDate
    )
}