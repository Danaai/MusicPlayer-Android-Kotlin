package com.example.musicapp.data.mapper

import com.example.musicapp.data.db.entity.ArtistEntity
import com.example.musicapp.domain.model.Artist

fun ArtistEntity.toDomain(): Artist {
    return Artist(
        id = id,
        name = name,
        imageUrl = imageUrl,
        info = info,
        popularity = popularity
    )
}