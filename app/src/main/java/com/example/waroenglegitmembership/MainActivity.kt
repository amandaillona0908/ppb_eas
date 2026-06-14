package com.example.waroenglegitmembership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waroenglegitmembership.data.AppDatabase
import com.example.waroenglegitmembership.repository.MembershipRepository
import com.example.waroenglegitmembership.ui.screens.*
import com.example.waroenglegitmembership.ui.theme.WaroengLegitTheme
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel
import com.example.waroenglegitmembership.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaroengLegitTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Bangun rantai dependency: Database -> Repository -> ViewModel.
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember {
        MembershipRepository(database.memberDao(), database.transactionDao())
    }
    val viewModel: MembershipViewModel = viewModel(factory = ViewModelFactory(repository))

    // NavController mengatur perpindahan antar-screen.
    val navController = rememberNavController()

    // Navigation Structure PRD: Splash -> Home -> Member Detail
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true } // splash tidak bisa di-back
                }
            })
        }

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAddMember = { navController.navigate("add_member") },
                onMemberClick = { id -> navController.navigate("member_detail/$id") }
            )
        }

        composable("add_member") {
            AddMemberScreen(
                onBack = { navController.popBackStack() },
                onSave = { name, email, phone ->
                    viewModel.registerMember(name, email, phone)
                    navController.popBackStack()
                }
            )
        }

        // Route dengan parameter id member.
        composable("member_detail/{memberId}") { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId")?.toIntOrNull() ?: 0
            MemberDetailScreen(
                memberId = memberId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
