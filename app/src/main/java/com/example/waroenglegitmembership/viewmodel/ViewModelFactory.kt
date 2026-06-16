package com.example.waroenglegitmembership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.waroenglegitmembership.repository.MembershipRepository

/** Factory untuk membuat ViewModel yang membutuhkan repository. */
class ViewModelFactory(
    private val repository: MembershipRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MembershipViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return MembershipViewModel(repository) as T
    }
}