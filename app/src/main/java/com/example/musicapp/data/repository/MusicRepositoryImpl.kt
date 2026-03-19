package com.example.musicapp.data.repository

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
import com.example.musicapp.data.mapper.toDomain
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepositoryImpl(
    private val songDao: SongDao,
    private val artistDao: ArtistDao,
    private val playlistDao: PlaylistDao,
    private val userDao: UserDao,
    private val albumDao: AlbumDao,
) : MusicRepository {

    // ===== SONG =====

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs()
            .map { it.map { entity -> entity.toDomain() } }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun getSongById(id: Long): Song? {
        return songDao.getSongById(id)?.toDomain()
    }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
        return songDao.getSongsByArtist(artistId)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun isFavorite(userId: Long, songId: Long): Boolean {
        return songDao.isFavorite(userId, songId)
    }

    override fun getFavoriteSongs(userId: Long): Flow<List<Song>> {
        return songDao.getFavoriteSongs(userId)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun addFavorite(userId: Long, songId: Long) {
        songDao.addFavorite(
            FavoriteSongEntity(userId, songId)
        )
    }

    override suspend fun removeFavorite(userId: Long, songId: Long) {
        songDao.removeFavorite(userId, songId)
    }

    override fun getFavoriteSongIds(userId: Long): Flow<List<FavoriteSongEntity>> {
        return songDao.getFavoriteSongIds(userId)
    }

    override fun getRecentlyPlayed(userId: Long): Flow<List<Song>> {
        return songDao.getRecentlyPlayed(userId)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun addRecentlyPlayed(userId: Long, songId: Long) {
        songDao.addRecentlyPlayed(
            RecentlyPlayedEntity(userId, songId)
        )

        songDao.trimHistory(userId)
    }

    override suspend fun clearHistory(userId: Long) {
        songDao.clearHistory(userId)
    }

    override suspend fun insertSong(song: Song) {
        songDao.insertSong(
            SongEntity(
                id = 0,
                title = song.title,
                artistId = song.artistId,
                albumId = song.albumId,
                durationMs = song.durationMs,
                audioUrl = song.audioUrl,
                imageUrl = song.imageUrl,
                releaseDate = song.releaseDate
            )
        )
    }

    override suspend fun updateSong(song: Song) {
        songDao.updateSong(
            SongEntity(
                id = song.id,
                title = song.title,
                artistId = song.artistId,
                albumId = song.albumId,
                genre = song.genre,
                durationMs = song.durationMs,
                audioUrl = song.audioUrl,
                imageUrl = song.imageUrl,
                releaseDate = song.releaseDate
            )
        )
    }

    override suspend fun deleteSong(songId: Long) {
        songDao.deleteSong(songId)
    }

    // ===== ARTIST =====

    override fun getAllArtists(): Flow<List<Artist>> {
        return artistDao.getAllArtists()
            .map { it.map { entity -> entity.toDomain() } }
    }

    override fun searchArtists(query: String): Flow<List<Artist>> {
        return artistDao.searchArtists(query)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override fun getTopArtists(limit: Int): Flow<List<Artist>> {
        return artistDao.getTopArtists(limit)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun getArtistById(id: Long): Artist? {
        return artistDao.getArtistById(id)?.toDomain()
    }

    override suspend fun followArtist(userId: Long, artistId: Long) {
        artistDao.followArtist(
            FollowedArtistEntity(userId, artistId)
        )
    }

    override suspend fun unfollowArtist(userId: Long, artistId: Long) {
        artistDao.unfollowArtist(userId, artistId)
    }

    override fun getFollowedArtistIds(userId: Long): Flow<List<FollowedArtistEntity>> {
        return artistDao.getFollowedArtistIds(userId)
    }

    override suspend fun isFollowing(userId: Long, artistId: Long): Boolean {
        return artistDao.isFollowing(userId, artistId)
    }

    override fun observeIsFollowing(userId: Long, artistId: Long): Flow<Boolean> {
        return artistDao.observeIsFollowing(userId, artistId)
    }

    override suspend fun insertArtist(artist: Artist) {
        artistDao.insertArtist(
            ArtistEntity(
                id = artist.id,
                name = artist.name,
                imageUrl = artist.imageUrl,
                info = artist.info,
                popularity = artist.popularity
            )
        )
    }

    override suspend fun updateArtist(artist: Artist) {
        artistDao.updateArtist(
            ArtistEntity(
                id = artist.id,
                name = artist.name,
                imageUrl = artist.imageUrl,
                info = artist.info,
                popularity = artist.popularity
            )
        )
    }

    override suspend fun deleteArtist(artistId: Long) {
        artistDao.deleteArtist(artistId)
    }

    // ===== PLAYLIST =====

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { entities ->
                entities.map { entity ->
                    val owner = userDao.getUserById(entity.ownerUserId)
                    val songCount = playlistDao.getSongCount(entity.id)

                    entity.toDomain(
                        ownerName = owner?.username ?: "Unknown",
                        songCount = songCount
                    )
                }
            }
    }

    override fun searchPlaylists(query: String): Flow<List<Playlist>> {
        return playlistDao.searchPlaylists(query)
            .map { it.map { entity -> entity.toDomain("Unknown", 20) } }
    }

    override fun getPlaylistsByUser(userId: Long): Flow<List<Playlist>> {
        return playlistDao.getPlaylistsByUser(userId)
            .map { entities ->
                entities.map { entity ->
                    val owner = userDao.getUserById(entity.ownerUserId)
                    val songCount = playlistDao.getSongCount(entity.id)

                    entity.toDomain(
                        ownerName = owner?.username ?: "Unknown",
                        songCount = songCount
                    )
                }
            }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = playlistDao.getPlaylistById(id) ?: return null
        val owner = userDao.getUserById(entity.ownerUserId)
        val songCount = playlistDao.getSongCount(entity.id)

        return entity.toDomain(
            ownerName = owner?.username ?: "Unknown",
            songCount = songCount
        )
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                ownerUserId = playlist.ownerId,
                imageUrl = playlist.imageUrl,
                isPublic = playlist.isPublic,
                createdAt = playlist.createdAt
            )
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                ownerUserId = playlist.ownerId,
                imageUrl = playlist.imageUrl,
                isPublic = playlist.isPublic,
                createdAt = playlist.createdAt
            )
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override fun getSongsByPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsInPlaylist(playlistId)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun addSongToPlaylist(songId: Long, playlistId: Long) {

        val count = playlistDao.getSongCount(playlistId)

        playlistDao.addSongToPlaylist(
            PlaylistSongEntity(
                playlistId = playlistId,
                songId = songId,
                position = count + 1
            )
        )
    }

    override suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long) {
        playlistDao.removeSongFromPlaylist(songId, playlistId)
    }

    // ===== ALBUM =====

    override fun getAllAlbums(): Flow<List<Album>> {
        return albumDao.getAllAlbums()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun searchAlbums(query: String): Flow<List<Album>> {
        return albumDao.searchAlbums(query)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun getAlbumById(id: Long): Album? {
        return albumDao.getAlbumById(id)?.toDomain()
    }

    override fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> {
        return albumDao.getAlbumsByArtist(artistId)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun insertAlbum(album: Album) {
        albumDao.insertAlbum(
            AlbumEntity(
                id = album.id,
                title = album.title,
                artistId = album.artistId,
                releaseDate = album.releaseDate,
                imageUrl = album.imageUrl
            )
        )
    }

    override suspend fun updateAlbum(album: Album) {
        albumDao.updateAlbum(
            AlbumEntity(
                id = album.id,
                title = album.title,
                artistId = album.artistId,
                releaseDate = album.releaseDate,
                imageUrl = album.imageUrl
            )
        )
    }

    override suspend fun deleteAlbum(albumId: Long) {
        albumDao.deleteAlbum(albumId)
    }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
        return songDao.getSongsByAlbum(albumId)
            .map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun addSongToAlbum(songId: Long, albumId: Long) {
        val song = songDao.getSongById(songId) ?: return

        songDao.updateSong(
            song.copy(albumId = albumId)
        )
    }

}