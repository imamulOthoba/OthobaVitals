package com.othoba.othobavitals.dashboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    fun getCredentials(): StateFlow<Pair<String, String>?>
}

class AuthRepositoryImpl : AuthRepository {
    // Hardcoded for now. Later this can wrap TvAuthServer for the mobile app pairing flow.
    private val _credentials = MutableStateFlow<Pair<String, String>?>(Pair("admin", "6bCTqiPn36"))

    override fun getCredentials(): StateFlow<Pair<String, String>?> = _credentials.asStateFlow()
}