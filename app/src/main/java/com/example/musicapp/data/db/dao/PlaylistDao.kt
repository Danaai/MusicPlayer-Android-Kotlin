package com.example.musicapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.db.entity.AlbumEntity
import com.example.musicapp.data.db.entity.PlaylistEntity
import com.example.musicapp.data.db.entity.PlaylistSongEntity
import com.example.musicapp.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("""SELECT * FROM playlists WHERE name LIKE '%' || :query || '%'""")
    fun searchPlaylists(query: String): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE ownerUserId = :userId")
    fun getPlaylistsByUser(userId: Long): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(entity: PlaylistSongEntity)

    @Query(
        """
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps
        ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.position ASC
    """
    )
    fun getSongsInPlaylist(playlistId: Long): Flow<List<SongEntity>>

    @Query(
        """
        DELETE FROM playlist_songs
        WHERE songId = :songId AND playlistId = :playlistId 
    """
    )
    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long)

    @Query(
        """
        SELECT COUNT(*) FROM playlist_songs
        WHERE playlistId = :playlistId
    """
    )
    suspend fun getSongCount(playlistId: Long): Int

}