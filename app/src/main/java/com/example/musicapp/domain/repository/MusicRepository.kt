package com.example.musicapp.domain.repository

import com.example.musicapp.data.db.entity.FavoriteSongEntity
import com.example.musicapp.data.db.entity.FollowedArtistEntity
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    // ===== SONG =====
    fun getAllSongs(): Flow<List<Song>>
    fun searchSongs(query: String): Flow<List<Song>>
    suspend fun getSongById(id: Long): Song?
    fun getSongsByArtist(artistId: Long): Flow<List<Song>>
    fun getFavoriteSongs(userId: Long): Flow<List<Song>>
    suspend fun isFavorite(userId: Long, songId: Long): Boolean
    suspend fun addFavorite(userId: Long, songId: Long)
    suspend fun removeFavorite(userId: Long, songId: Long)
    fun getFavoriteSongIds(userId: Long): Flow<List<FavoriteSongEntity>>
    fun getRecentlyPlayed(userId: Long): Flow<List<Song>>
    suspend fun addRecentlyPlayed(userId: Long, songId: Long)
    suspend fun clearHistory(userId: Long)
    suspend fun insertSong(song: Song)
    suspend fun updateSong(song: Song)
    suspend fun deleteSong(songId: Long)

    // ===== ARTIST =====
    fun getAllArtists(): Flow<List<Artist>>
    fun searchArtists(query: String): Flow<List<Artist>>
    fun getTopArtists(limit: Int): Flow<List<Artist>>
    suspend fun getArtistById(id: Long): Artist?
    suspend fun followArtist(userId: Long, artistId: Long)
    suspend fun unfollowArtist(userId: Long, artistId: Long)
    fun getFollowedArtistIds(userId: Long): Flow<List<FollowedArtistEntity>>
    suspend fun isFollowing(userId: Long, artistId: Long): Boolean
    fun observeIsFollowing(userId: Long, artistId: Long): Flow<Boolean>
    suspend fun insertArtist(artist: Artist)
    suspend fun updateArtist(artist: Artist)
    suspend fun deleteArtist(artistId: Long)

    // ===== PLAYLIST =====
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun searchPlaylists(query: String): Flow<List<Playlist>>
    fun getPlaylistsByUser(userId: Long): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Long)
    fun getSongsByPlaylist(playlistId: Long): Flow<List<Song>>
    suspend fun addSongToPlaylist(songId: Long, playlistId: Long)
    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long)

    // ===== ALBUM =====

    fun getAllAlbums(): Flow<List<Album>>
    fun searchAlbums(query: String): Flow<List<Album>>
    suspend fun getAlbumById(id: Long): Album?
    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>
    suspend fun insertAlbum(album: Album)
    suspend fun updateAlbum(album: Album)
    suspend fun deleteAlbum(albumId: Long)
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    suspend fun addSongToAlbum(songId: Long, albumId: Long)

}