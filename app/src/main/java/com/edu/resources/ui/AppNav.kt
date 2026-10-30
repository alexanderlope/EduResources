package com.edu.resources.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.edu.resources.data.ResourcesViewModel
import com.edu.resources.data.SessionViewModel
import com.edu.resources.data.User
import com.edu.resources.ui.auth.LoginScreen
import com.edu.resources.ui.auth.RegisterScreen
import com.edu.resources.ui.components.LoadingBox
import com.edu.resources.ui.profile.ProfileScreen
import com.edu.resources.ui.resources.DetailScreen
import com.edu.resources.ui.resources.FavoritesScreen
import com.edu.resources.ui.resources.ResourcesScreen
import com.edu.resources.ui.teacher.ResourceFormScreen
import com.edu.resources.ui.teacher.TeacherScreen
import com.edu.resources.ui.theme.AppColors

private object Routes {
    const val RECURSOS = "recursos"
    const val FAVORITOS = "favoritos"
    const val GESTION = "gestion"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{id}"
    const val FORM = "form?id={id}"
}

private data class Tab(val route: String, val label: String, val selected: ImageVector, val unselected: ImageVector)

private val TabExplorar = Tab(Routes.RECURSOS, "Explorar", Icons.Filled.LocalLibrary, Icons.Outlined.LocalLibrary)
private val TabFavoritos = Tab(Routes.FAVORITOS, "Favoritos", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
private val TabGestion = Tab(Routes.GESTION, "Gestión", Icons.Filled.Settings, Icons.Outlined.Settings)
private val TabPerfil = Tab(Routes.PERFIL, "Perfil", Icons.Filled.Person, Icons.Outlined.PersonOutline)

@Composable
fun AppRoot(session: SessionViewModel = viewModel()) {
    val ready by session.ready.collectAsState()
    val user by session.user.collectAsState()
    val current = user

    Box(Modifier.fillMaxSize().background(AppColors.Bg)) {
        when {
            !ready -> LoadingBox(null)
            current == null -> AuthNav(session)
            else -> MainNav(session, current)
        }
    }
}

@Composable
private fun AuthNav(session: SessionViewModel) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "login") {
        composable("login") {
            LoginScreen(session, onRegister = { nav.navigate("register") })
        }
        composable("register") {
            RegisterScreen(
                session,
                onBack = { nav.popBackStack() },
                onDone = { nav.popBackStack() },
            )
        }
    }
}

@Composable
private fun MainNav(session: SessionViewModel, user: User) {
    val nav = rememberNavController()
    val resources: ResourcesViewModel = viewModel()
    LaunchedEffect(user.id) { resources.load() }

    val tabs = if (user.isDocente) listOf(TabExplorar, TabGestion, TabPerfil)
    else listOf(TabExplorar, TabFavoritos, TabPerfil)

    val entry by nav.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    val showBar = tabs.any { it.route == currentRoute }

    Scaffold(
        containerColor = AppColors.Bg,
        contentWindowInsets = if (showBar) WindowInsets(0) else WindowInsets.navigationBars,
        bottomBar = {
            if (showBar) {
                NavigationBar(
                    containerColor = AppColors.Surface,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                ) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(if (selected) tab.selected else tab.unselected, tab.label) },
                            label = { Text(tab.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppColors.Accent,
                                selectedTextColor = AppColors.Accent,
                                unselectedIconColor = AppColors.Dim,
                                unselectedTextColor = AppColors.Dim,
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = Routes.RECURSOS, modifier = Modifier.padding(padding)) {
            composable(Routes.RECURSOS) {
                ResourcesScreen(resources, session, onOpen = { nav.navigate("detalle/${Uri.encode(it)}") })
            }
            composable(Routes.FAVORITOS) {
                FavoritesScreen(session, onOpen = { nav.navigate("detalle/${Uri.encode(it)}") })
            }
            composable(Routes.GESTION) {
                TeacherScreen(
                    resources,
                    onAdd = { nav.navigate("form") },
                    onEdit = { nav.navigate("form?id=${Uri.encode(it)}") },
                )
            }
            composable(Routes.PERFIL) {
                ProfileScreen(user, onLogout = session::logout)
            }
            composable(
                Routes.DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { e ->
                DetailScreen(
                    id = e.arguments?.getString("id").orEmpty(),
                    vm = resources,
                    session = session,
                    user = user,
                    onBack = { nav.popBackStack() },
                )
            }
            composable(
                Routes.FORM,
                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null }),
            ) { e ->
                ResourceFormScreen(
                    id = e.arguments?.getString("id"),
                    vm = resources,
                    onBack = { nav.popBackStack() },
                )
            }
        }
    }
}
