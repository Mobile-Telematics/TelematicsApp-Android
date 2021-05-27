package com.telematics.features.account.model

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialog {

    fun create(
        editText: EditText,
        clearFocusView: View,
        title: String,
        nextField: EditText,
        minDate: Long? = null,
        maxDate: Long? = null

    ) {
        val calendar = Calendar.getInstance()
        if (editText.text.toString().isNotEmpty()) {
            calendar.time =
                try {
                    SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).parse(editText.text.toString())
                        ?: Date()
                } catch (e: Exception) {
                    Date()
                }
        }

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateInStr = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(calendar.time)
            editText.setText(dateInStr)
            if (nextField.text.isEmpty()) nextField.requestFocus() else clearFocusView.requestFocus()
        }

        val dialog = DatePickerDialog(
            clearFocusView.context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnCancelListener { (clearFocusView.requestFocus()) }

        maxDate?.let { dialog.datePicker.maxDate = it }
        minDate?.let { dialog.datePicker.minDate = it }

        try {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            if (e is NullPointerException) {
                System.err.print("Produce in for DatePickerDialog")
            }
        }
        dialog.setTitle(title)
        if (!dialog.isShowing) dialog.show()
    }
}