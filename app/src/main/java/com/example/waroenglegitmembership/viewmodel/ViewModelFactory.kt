package com.example.waroenglegitmembership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.waroenglegitmembership.repository.MembershipRepository

// Factory = "pabrik" untuk membuat ViewModel yang butuh parameter (repository).
// Tanpa ini, ViewModel dengan parameter tidak bisa dibuat oleh sistem.
class ViewModelFactory(private val repository: MembershipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MembershipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MembershipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
