package kr.sjh.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.navigation.RootNavGraph
import kr.sjh.presentation.ui.theme.PickUpTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var condition = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            condition
        }
        setContent {
            PickUpTheme {
                val navController = rememberNavController()
                RootNavGraph(
                    navController,
                    onKeepOnScreenCondition = {
                        condition = false
                    }
                )
            }
        }
    }
}
