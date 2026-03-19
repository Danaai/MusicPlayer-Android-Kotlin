package com.example.musicapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.db.entity.ArtistEntity
import com.example.musicapp.data.db.entity.FollowedArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Query("SELECT * FROM artists ORDER BY id DESC")
    fun getAllArtists(): Flow<List<ArtistEntity>>

    @Query("""SELECT * FROM artists WHERE name LIKE '%' || :query || '%'""")
    fun searchArtists(query: String): Flow<List<ArtistEntity>>

    @Query("""SELECT * FROM artists ORDER BY popularity DESC LIMIT :limit""")
    fun getTopArtists(limit: Int): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artists WHERE id = :artistId")
    suspend fun getArtistById(artistId: Long): ArtistEntity?

    @Query("""
        SELECT COUNT(*) FROM songs
        WHERE artistId = :artistId
    """)
    suspend fun getSongCountForArtist(artistId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followArtist(entity: FollowedArtistEntity)

    @Query("""
        DELETE FROM followed_artists
        WHERE userId = :userId AND artistId = :artistId
    """)
    suspend fun unfollowArtist(userId: Long, artistId: Long)

    @Query("""
    SELECT * FROM followed_artists
    WHERE userId = :userId
    """)
    fun getFollowedArtistIds(userId: Long): Flow<List<FollowedArtistEntity>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM followed_artists
            WHERE userId = :userId AND artistId = :artistId
        )
    """)
    suspend fun isFollowing(userId: Long, artistId: Long): Boolean

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM followed_artists
            WHERE userId = :userId AND artistId = :artistId
        )
    """)
    fun observeIsFollowing(userId: Long, artistId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity)

    @Update
    suspend fun updateArtist(artist: ArtistEntity)

    @Query("DELETE FROM artists WHERE id = :artistId")
    suspend fun deleteArtist(artistId: Long)
}