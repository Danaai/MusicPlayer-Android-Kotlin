package com.example.musicapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.db.entity.FavoriteSongEntity
import com.example.musicapp.data.db.entity.RecentlyPlayedEntity
import com.example.musicapp.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("SELECT * FROM songs where id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE artistId = :artistId")
    fun getSongsByArtist(artistId: Long): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY id DESC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("""
        SELECT song.* FROM songs song
        LEFT JOIN artists artist ON song.artistId = artist.id
        WHERE song.title LIKE '%' || :query || '%'
            OR artist.name LIKE '%' || :query || '%'
        """)
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteSongEntity)

    @Query("DELETE FROM favorite_songs WHERE userId = :userId AND songId = :songId")
    suspend fun removeFavorite(userId: Long, songId: Long)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM favorite_songs
            WHERE userId = :userId AND songId = :songId
        )
    """)
    suspend fun isFavorite(userId: Long, songId: Long): Boolean

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN favorite_songs f
        on s.id = f.songId
        WHERE f.userId = :userId
        ORDER BY f.addedAt DESC
    """)
    fun getFavoriteSongs(userId: Long): Flow<List<SongEntity>>

    @Query("""
    SELECT * FROM favorite_songs
    WHERE userId = :userId
    """)
    fun getFavoriteSongIds(userId: Long): Flow<List<FavoriteSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecentlyPlayed(entity: RecentlyPlayedEntity)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN recently_played r
        ON s.id = r.songId
        WHERE r.userId = :userId
        ORDER BY r.playedAt DESC
    """)
    fun getRecentlyPlayed(userId: Long): Flow<List<SongEntity>>

    @Query("""
        DELETE FROM recently_played
        WHERE userId = :userId
        AND songId NOT IN (
            SELECT songId FROM recently_played
            WHERE userId = :userId
            ORDER BY playedAt DESC
            LIMIT 30
        )
    """)
    suspend fun trimHistory(userId: Long)

    @Query("DELETE FROM recently_played WHERE userId = :userId")
    suspend fun clearHistory(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteSong(songId: Long)

    @Query("SELECT * FROM songs WHERE albumId = :albumId")
    fun getSongsByAlbum(albumId: Long): Flow<List<SongEntity>>

}