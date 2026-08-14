package com.craftlanka.app

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class NavigationManager(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int
) {

    /**
     * Replaces the current fragment inside containerId with a new fragment.
     */
    fun replaceFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        clearBackStack: Boolean = false
    ) {
        // 1. If clearBackStack is true, erase previous screen history
        if (clearBackStack) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        // 2. Start swapping the fragment inside the container
        val transaction = fragmentManager.beginTransaction()
            .replace(containerId, fragment)

        // 3. Remember this screen so pressing physical Back button returns here
        if (addToBackStack && !clearBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }

        // 4. Safely apply the change
        transaction.commitAllowingStateLoss()
    }
}