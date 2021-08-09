package com.telematics.content.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telematics.content.R

abstract class BaseFragment : Fragment() {

    fun setBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }

    fun hideKeyboard() {

        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setWhiteNavigationBar() {
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.navigationMenuBarOnLogin)
    }

    fun setGreenNavigationBar() {
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.navigationMenuBarOnMain)
    }
}