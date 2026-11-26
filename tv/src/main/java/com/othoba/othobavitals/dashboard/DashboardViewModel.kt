package com.othoba.othobavitals.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    val credentials: StateFlow<Pair<String, String>?> = authRepository.getCredentials()

    // URL constructed cleanly here in the ViewModel state
    private val _grafanaUrl = MutableStateFlow(
        "https://grafana.othoba.com/d/1d38ed6c-a28c-4c59-81c4-d2aa8b435621/k8s-dashboard-copy?orgId=1&from=now-30m&to=now&var-origin_prometheus=&var-Node=\${'$'}__all&var-NameSpace=othoba&var-Container=othoba-web&var-Pod=\${'$'}__all&var-Filters=&refresh=auto&kiosk=tv"
    )
    val grafanaUrl: StateFlow<String> = _grafanaUrl.asStateFlow()
}