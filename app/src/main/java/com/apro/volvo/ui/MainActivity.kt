package com.apro.volvo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apro.volvo.ui.navigation.NavRoute
import com.apro.volvo.ui.screens.Dashboard
import com.apro.volvo.ui.theme.VolvoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)



    setContent {

      val navController = rememberNavController()

      VolvoTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(
            navController = navController,
            startDestination = NavRoute.Dashboard.route,
          ) {
            composable(NavRoute.Dashboard.route) {
              Dashboard(
                onSignInClick = {
                  println(">>> click on sign in")
                }
              )
            }
          }
        }
      }
    }
  }
}

