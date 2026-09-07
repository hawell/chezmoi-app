package org.chordsoft.chezmoi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import org.chordsoft.chezmoi.ui.screens.MainScreen
import org.chordsoft.chezmoi.ui.theme.ChezMoiTheme
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SearchAddressViewModel

class MainActivity : ComponentActivity() {
    private lateinit var mapViewModel: MapViewModel
    private lateinit var searchAddressViewModel: SearchAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapViewModel = ViewModelProvider(
            this,
            MapViewModel.factory(),
        )[MapViewModel::class.java]
        searchAddressViewModel = ViewModelProvider(
            this,
            SearchAddressViewModel.factory(),
        )[SearchAddressViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ChezMoiTheme {
                MainScreen(Modifier, mapViewModel, searchAddressViewModel)
            }
        }
    }
}
