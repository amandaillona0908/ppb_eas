package com.example.waroenglegitmembership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waroenglegitmembership.data.AppDatabase
import com.example.waroenglegitmembership.repository.MembershipRepository
import com.example.waroenglegitmembership.ui.screens.*
import com.example.waroenglegitmembership.ui.theme.WaroengLegitTheme
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel
import com.example.waroenglegitmembership.viewmodel.ViewModelFactory

/** Daftar rute navigasi aplikasi. */
private object Routes {
    const val SPLASH = "splash"
    const val ROLE = "role"
    const val BARISTA_HOME = "barista_home"
    const val ADD_MEMBER = "add_member"
    const val BARISTA_DETAIL = "detail/barista/{memberId}"
    const val CUSTOMER_LOGIN = "customer_login"
    const val CUSTOMER_DASHBOARD = "customer_dashboard/{memberId}"

    fun baristaDetail(memberId: Int) = "detail/barista/$memberId"
    fun customerDashboard(memberId: Int) = "customer_dashboard/$memberId"
}

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
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember {
        MembershipRepository(database.memberDao(), database.transactionDao())
    }
    val viewModel: MembershipViewModel = viewModel(factory = ViewModelFactory(repository))
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(Routes.ROLE) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.ROLE) {
            RoleScreen(
                onBaristaLogin = { navController.navigate(Routes.BARISTA_HOME) },
                onCustomerLogin = { navController.navigate(Routes.CUSTOMER_LOGIN) }
            )
        }

        // ---------- Barista ----------
        composable(Routes.BARISTA_HOME) {
            BaristaHomeScreen(
                viewModel = viewModel,
                onAddMember = { navController.navigate(Routes.ADD_MEMBER) },
                onMemberClick = { id -> navController.navigate(Routes.baristaDetail(id)) },
                onLogout = { navController.backToRole() }
            )
        }

        composable(Routes.ADD_MEMBER) {
            AddMemberScreen(
                onBack = { navController.popBackStack() },
                onSave = { name, email, phone, onResult ->
                    viewModel.registerMember(name, email, phone) { defaultPass, memberId ->
                        onResult(defaultPass, memberId)
                    }
                }
            )
        }

        composable(Routes.BARISTA_DETAIL) { entry ->
            val memberId = entry.arguments?.getString("memberId")?.toIntOrNull() ?: 0
            MemberDetailScreen(
                memberId = memberId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- Customer ----------
        composable(Routes.CUSTOMER_LOGIN) {
            CustomerLoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { id ->
                    navController.navigate(Routes.customerDashboard(id)) {
                        popUpTo(Routes.CUSTOMER_LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CUSTOMER_DASHBOARD) { entry ->
            val memberId = entry.arguments?.getString("memberId")?.toIntOrNull() ?: 0
            CustomerDashboardScreen(
                memberId = memberId,
                viewModel = viewModel,
                onLogout = { navController.backToRole() }
            )
        }
    }
}

/** Kembali ke halaman pilih role dan bersihkan back stack. */
private fun NavHostController.backToRole() {
    navigate(Routes.ROLE) { popUpTo(Routes.ROLE) { inclusive = true } }
}