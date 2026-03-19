package com.example.musicapp.ui.admin.user_management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _deleteTarget = MutableStateFlow<User?>(null)
    private val _currentPage = MutableStateFlow(1)

    val uiState: StateFlow<UserManagementUiState> =
        combine(
            repository.getAllUsers(),
            _searchQuery,
            _deleteTarget,
            _currentPage
        ) { users, query, deleteTarget, page ->
            val filtered =
                if (query.isBlank()) users
                else users.filter {
                    it.username.contains(query, true) ||
                    it.email.contains(query, true)
                }

            val pageSize = 8
            val totalPages = (filtered.size + pageSize - 1) / pageSize

            val safePage = page.coerceIn(1, totalPages.coerceAtLeast(1))

            val pagedUsers = filtered
                .drop((safePage - 1)*pageSize)
                .take(pageSize)

            UserManagementUiState(
                users = pagedUsers,
                searchQuery = query,
                showDeleteDialog = deleteTarget != null,
                userToDelete = deleteTarget,
                currentPage = safePage,
                totalPages = totalPages
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserManagementUiState()
        )

    fun onSearchChange(value: String) {
        _searchQuery.value = value
    }

    fun onDeleteClick(user: User) {
        _deleteTarget.value = user
    }

    fun onBanClick(user: User) {
        viewModelScope.launch {
            repository.setUserBanned(
                userId = user.id,
                isBanned = !user.isBanned
            )
        }
    }

    fun dismissDeleteDialog() {
        _deleteTarget.value = null
    }

    fun confirmDelete() {
        viewModelScope.launch {
            _deleteTarget.value?.let {
                repository.deleteUser(it.id)
            }
            _deleteTarget.value = null
        }
    }

    fun nextPage() {
        _currentPage.update { it + 1 }
    }

    fun prevPage() {
        _currentPage.update { (it - 1).coerceAtLeast(1) }
    }

    fun goToPage(page: Int) {
        _currentPage.value = page
    }

}

data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val userToDelete: User? = null,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)

class AddUserViewModel(
    private val repository: UserRepository,
    private val userId: Long? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUserUiState())
    val uiState: StateFlow<AddUserUiState> = _uiState.asStateFlow()

    init {
        userId?.let { loadUser(it) }
    }

    private fun loadUser(id: Long) {
        viewModelScope.launch {
            repository.getUserById(id)?.let { user ->
                _uiState.value = AddUserUiState(
                    userName = user.username,
                    email = user.email,
                    password = user.password,
                    avtUrl = user.avtUrl,
                    bio = user.bio,
                    role = user.role,
                    createdAt = user.createdAt,
                    isBanned = user.isBanned
                )
            }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(userName = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onRoleChange(value: String) {
        _uiState.update { it.copy(role = value) }
    }

    fun saveUser() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val user = User(
                id = userId ?: 0,
                username = state.userName,
                email = state.email,
                password = state.password,
                avtUrl = state.avtUrl,
                bio = state.bio,
                role = state.role,
                createdAt = state.createdAt ?: System.currentTimeMillis().toString(),
                isBanned = state.isBanned
            )

            if(userId == null) {
                repository.insertUser(user)
            } else {
                repository.updateUser(user)
            }

            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

}

data class AddUserUiState(
    val userName: String = "",
    val email: String = "",
    val password: String = "",

    val avtUrl: String? = null,
    val bio: String? = null,
    val role: String = "user",

    val createdAt: String? = null,
    val isBanned: Boolean = false,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)