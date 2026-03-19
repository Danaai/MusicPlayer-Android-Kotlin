package com.example.musicapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicapp.data.db.dao.AlbumDao
import com.example.musicapp.data.db.dao.ArtistDao
import com.example.musicapp.data.db.dao.PlaylistDao
import com.example.musicapp.data.db.dao.SongDao
import com.example.musicapp.data.db.dao.UserDao
import com.example.musicapp.data.db.entity.AlbumEntity
import com.example.musicapp.data.db.entity.ArtistEntity
import com.example.musicapp.data.db.entity.FavoriteSongEntity
import com.example.musicapp.data.db.entity.FollowedArtistEntity
import com.example.musicapp.data.db.entity.PlaylistEntity
import com.example.musicapp.data.db.entity.PlaylistSongEntity
import com.example.musicapp.data.db.entity.RecentlyPlayedEntity
import com.example.musicapp.data.db.entity.SongEntity
import com.example.musicapp.data.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        FavoriteSongEntity::class,
        RecentlyPlayedEntity::class,
        FollowedArtistEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }

}