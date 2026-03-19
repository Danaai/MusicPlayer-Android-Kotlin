package com.example.musicapp.data.mapper

import com.example.musicapp.data.db.entity.AlbumEntity
import com.example.musicapp.domain.model.Album

fun AlbumEntity.toDomain(): Album {
    return Album(
        id = id,
        title = title,
        artistId = artistId,
        imageUrl = imageUrl,
        releaseDate = releaseDate
    )
}