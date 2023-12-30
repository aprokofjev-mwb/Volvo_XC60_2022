package com.apro.volvo.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink

sealed class NavRoute(
  val route: String,
  val pattern: String = route,
  val arguments: List<NamedNavArgument> = emptyList(),
  val deepLinks: List<NavDeepLink> = emptyList(),
) {
  object Dashboard : NavRoute(
    route = "Dashboard",
  )
}