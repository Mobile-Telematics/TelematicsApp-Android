package com.telematics.content.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
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

    fun showMessage(msg: String) {

        if (isAdded) {
            val view = requireActivity().findViewById<View>(android.R.id.content)
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    fun showMessage(@StringRes stringRes: Int) {

        showMessage(getString(stringRes))
    }

    private fun showAnswerDialog(
        title: String,
        onPositive: (() -> Unit)?,
        onNegative: (() -> Unit)? = null
    ) {

        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton(R.string.dialog_yes) { d, _ ->
                onPositive?.let {
                    onPositive()
                }
                d.dismiss()
            }
            setNegativeButton(R.string.dialog_no) { d, _ ->
                onNegative?.invoke()
                d.dismiss()
            }
            setCancelable(true)
            setTitle(title)
        }.show()
    }

    fun showAnswerDialog(
        @StringRes stringRes: Int,
        onPositive: (() -> Unit)?,
        onNegative: (() -> Unit)? = null
    ) {

        showAnswerDialog(getString(stringRes), onPositive, onNegative)
    }
}