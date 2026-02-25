package app.aplication.appproductos.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.aplication.appproductos.contact.presentation.detail.ContactDetailScreen
import app.aplication.appproductos.contact.presentation.list.ContactListScreen

sealed class Screen(val route: String) {
    object ContactList : Screen("contact_list")
    object AddContact : Screen("contact_detail/-1")
    object EditContact : Screen("contact_detail/{contactId}") {
        fun createRoute(contactId: Int) = "contact_detail/$contactId"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ContactList.route
    ) {
        composable(Screen.ContactList.route) {
            ContactListScreen(
                onNavigateToAdd = { navController.navigate(Screen.AddContact.route) },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditContact.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.EditContact.route,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStack ->
            val contactId = backStack.arguments?.getInt("contactId") ?: -1
            ContactDetailScreen(
                contactId = contactId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
