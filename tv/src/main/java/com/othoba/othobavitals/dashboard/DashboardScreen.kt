package com.othoba.othobavitals.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.TV_1080p
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Text
import com.othoba.othobavitals.ui.theme.OthobaVitalsTheme

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
    onLoaded: () -> Unit = {}
) {
    val credentials by viewModel.credentials.collectAsState()
    val url by viewModel.grafanaUrl.collectAsState()

    if (credentials == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Please open the Othoba Vitals Mobile app to sign in.",
                color = Color.White
            )
        }
    } else {
        GrafanaWebView(
            url = url,
            credentials = credentials!!,
            modifier = modifier,
            onLoaded = onLoaded
        )
    }
}

@Preview(device = TV_1080p)
@Composable
private fun DashboardScreenPreview() {
    OthobaVitalsTheme {
        DashboardScreen()
    }
}