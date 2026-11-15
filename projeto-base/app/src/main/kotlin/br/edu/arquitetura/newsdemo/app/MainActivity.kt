package br.edu.arquitetura.newsdemo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import br.edu.arquitetura.newsdemo.core.ui.NewsTheme
import br.edu.arquitetura.newsdemo.feature.favorites.FavoritesRoute
import br.edu.arquitetura.newsdemo.feature.news.NewsDetailRoute
import br.edu.arquitetura.newsdemo.feature.news.NewsRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NewsTheme { NewsApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsApp() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route ?: "news"
    val detail = current.startsWith("detail/")
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (detail) "Leitura" else if (current == "favorites") "Favoritos" else "Notícias") },
                navigationIcon = {
                    if (detail) IconButton(onClick = { nav.popBackStack() }, modifier = Modifier.size(48.dp).testTag("back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                })
        },
        bottomBar = {
            if (!detail) NavigationBar {
                listOf("news" to "Notícias", "favorites" to "Favoritos").forEach { (route, title) ->
                    NavigationBarItem(selected = current == route, onClick = {
                        nav.navigate(route) {
                            popUpTo(nav.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }, icon = {
                        Icon(if (route == "news") Icons.Outlined.Newspaper else Icons.Outlined.Bookmarks, contentDescription = null)
                    }, label = { Text(title) }, modifier = Modifier.testTag("nav-$route"))
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "news", modifier = Modifier.padding(padding)) {
            composable("news") { NewsRoute(hiltViewModel(), onOpen = { nav.navigate("detail/$it") }) }
            composable("favorites") { FavoritesRoute(hiltViewModel(), onOpen = { nav.navigate("detail/$it") }) }
            composable("detail/{newsId}", arguments = listOf(navArgument("newsId") { type = NavType.StringType })) {
                NewsDetailRoute(hiltViewModel())
            }
        }
    }
}
