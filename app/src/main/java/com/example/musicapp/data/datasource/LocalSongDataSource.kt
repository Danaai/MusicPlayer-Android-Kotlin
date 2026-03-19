package com.example.musicapp.data.datasource

import com.example.musicapp.domain.model.Song


object LocalSongDataSource {

    private val songs = listOf(
        Song(
            id = 1L,
            title = "Hometown Glory",
            artistId = 2L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/5EOCjY5UZvQ/maxresdefault.jpg",
            releaseDate = "2007, from 19",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        Song(
            id = 2L,
            title = "Starboy",
            artistId = 2L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/QLCpqdqeoII/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLAL_oEUHJZlAAXBms-DglUI-OxBdw",
            releaseDate = "2016",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        Song(
            id = 4L,
            title = "Umbrella",
            artistId = 4L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/oVGRTQDqhs8/maxresdefault.jpg",
            releaseDate = "2007",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
        ),
        Song(
            id = 5L,
            title = "Just Dance",
            artistId = 5L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/zVH638r_X_I/maxresdefault.jpg",
            releaseDate = "2008",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
        ),
        Song(
            id = 6L,
            title = "Dark Horse",
            artistId = 6L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/GSHPm2JkbUw/maxresdefault.jpg",
            releaseDate = "2013",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"
        ),
        Song(
            id = 7L,
            title = "Peaches",
            artistId = 7L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/BydBU2pCkU8/maxresdefault.jpg",
            releaseDate = "2021",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3"
        ),
        Song(
            id = 8L,
            title = "For All the Dogs",
            artistId = 8L,
            durationMs = 210000,
            imageUrl = "https://linkstorage.linkfire.com/medialinks/images/ba5d89df-2d3e-440e-8db0-cf05ee8a3fe4/artwork-600x315.jpg",
            releaseDate = "2023",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
        ),
        Song(
            id = 9L,
            title = "Doo-Wops & Hooligans",
            artistId = 9L,
            durationMs = 210000,
            imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music114/v4/52/b1/45/52b1452b-229e-78db-231b-7b43fa0077cc/075679956491.jpg/1200x630bf-60.jpg",
            releaseDate = "2010",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3"
        ),
        Song(
            id = 10L,
            title = "We Can't Be Friends",
            artistId = 10L,
            durationMs = 210000,
            imageUrl = "https://i.ytimg.com/vi/T5mbQ8aQZw0/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLCcq7aLr-lkrU-AVfJ5rBh0EaHh5w",
            releaseDate = "Mar 2024",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3"
        ),

    )

    fun getAllSongs(): List<Song> = songs

    fun getSongById(songId: Long): Song? {
        return songs.find { it.id == songId }
    }

    fun getSongByArtist(artistId: Long): List<Song> {
        return songs.filter { it.artistId == artistId }
    }

}