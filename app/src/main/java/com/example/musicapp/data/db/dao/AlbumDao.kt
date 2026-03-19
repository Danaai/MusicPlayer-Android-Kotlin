package com.example.musicapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.db.entity.AlbumEntity
import com.example.musicapp.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Query("SELECT * FROM albums ORDER BY id DESC")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("""SELECT * FROM albums WHERE title LIKE '%' || :query || '%'""")
    fun searchAlbums(query:String): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE artistId = :artistId")
    fun getAlbumsByArtist(artistId: Long): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: Long): AlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Update
    suspend fun updateAlbum(album: AlbumEntity)

    @Query("DELETE FROM albums WHERE id = :albumId")
    suspend fun deleteAlbum(albumId: Long)

}