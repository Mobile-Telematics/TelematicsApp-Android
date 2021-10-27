package com.telematics.features.feed.model

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import com.telematics.domain.model.tracking.TripData
import com.telematics.feed.R

class ChangeDriverTypeDialog(
    context: Context
) {

    private var dialog: Dialog = Dialog(context)
    private lateinit var popupSelectTripTypeCancel: Button
    private lateinit var popupSelectTripTypeConfirm: Button
    private lateinit var popupSelectTripTypeDriver: ImageView
    private lateinit var popupSelectTripTypePassenger: ImageView
    private lateinit var popupSelectTripTypeBus: ImageView
    private lateinit var popupSelectTripTypeMotorcycle: ImageView
    private lateinit var popupSelectTripTypeTrain: ImageView
    private lateinit var popupSelectTripTypeTaxi: ImageView
    private lateinit var popupSelectTripTypeBicycle: ImageView
    private lateinit var popupSelectTripTypeOther: ImageView

    init {

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_change_driver_type)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        findViews()
    }


    fun showDialog(
        selectType: TripData.TripType?,
        callback: (type: TripData.TripType) -> Unit
    ) {

        var tempType = selectType

        /*views*/
        fun onSetSelectView(selectType: TripData.TripType?) {

            tempType = selectType

            val selectedBackgroundTint = null
            val selectedElevation = 0f
            val selectedImageTint = ColorStateList.valueOf(Color.WHITE)

            val unselectedBackgroundTint = ColorStateList.valueOf(Color.WHITE)
            val unselectedElevation = 10f
            val unselectedImageTint = ColorStateList.valueOf(Color.rgb(175, 175, 175))

            popupSelectTripTypeDriver.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeDriver.elevation = unselectedElevation
            popupSelectTripTypeDriver.imageTintList = unselectedImageTint
            popupSelectTripTypePassenger.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypePassenger.elevation = unselectedElevation
            popupSelectTripTypePassenger.imageTintList = unselectedImageTint
            popupSelectTripTypeBus.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeBus.elevation = unselectedElevation
            popupSelectTripTypeBus.imageTintList = unselectedImageTint
            popupSelectTripTypeMotorcycle.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeMotorcycle.elevation = unselectedElevation
            popupSelectTripTypeMotorcycle.imageTintList = unselectedImageTint
            popupSelectTripTypeTrain.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeTrain.elevation = unselectedElevation
            popupSelectTripTypeTrain.imageTintList = unselectedImageTint
            popupSelectTripTypeTaxi.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeTaxi.elevation = unselectedElevation
            popupSelectTripTypeTaxi.imageTintList = unselectedImageTint
            popupSelectTripTypeBicycle.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeBicycle.elevation = unselectedElevation
            popupSelectTripTypeBicycle.imageTintList = unselectedImageTint
            popupSelectTripTypeOther.backgroundTintList = unselectedBackgroundTint
            popupSelectTripTypeOther.elevation = unselectedElevation
            popupSelectTripTypeOther.imageTintList = unselectedImageTint

            when (selectType) {
                TripData.TripType.DRIVER -> {
                    popupSelectTripTypeDriver.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeDriver.elevation = selectedElevation
                    popupSelectTripTypeDriver.imageTintList = selectedImageTint
                }
                TripData.TripType.PASSENGER -> {
                    popupSelectTripTypePassenger.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypePassenger.elevation = selectedElevation
                    popupSelectTripTypePassenger.imageTintList = selectedImageTint
                }
                TripData.TripType.BUS -> {
                    popupSelectTripTypeBus.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeBus.elevation = selectedElevation
                    popupSelectTripTypeBus.imageTintList = selectedImageTint
                }
                TripData.TripType.MOTORCYCLE -> {
                    popupSelectTripTypeMotorcycle.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeMotorcycle.elevation = selectedElevation
                    popupSelectTripTypeMotorcycle.imageTintList = selectedImageTint
                }
                TripData.TripType.TRAIN -> {
                    popupSelectTripTypeTrain.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeTrain.elevation = selectedElevation
                    popupSelectTripTypeTrain.imageTintList = selectedImageTint
                }
                TripData.TripType.TAXI -> {
                    popupSelectTripTypeTaxi.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeTaxi.elevation = selectedElevation
                    popupSelectTripTypeTaxi.imageTintList = selectedImageTint
                }
                TripData.TripType.BICYCLE -> {
                    popupSelectTripTypeBicycle.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeBicycle.elevation = selectedElevation
                    popupSelectTripTypeBicycle.imageTintList = selectedImageTint
                }
                TripData.TripType.OTHER -> {
                    popupSelectTripTypeOther.backgroundTintList = selectedBackgroundTint
                    popupSelectTripTypeOther.elevation = selectedElevation
                    popupSelectTripTypeOther.imageTintList = selectedImageTint
                }
                else -> {
                }
            }
        }

        onSetSelectView(selectType)

        /*onClick*/
        popupSelectTripTypeDriver.setOnClickListener {
            onSetSelectView(TripData.TripType.DRIVER)
        }
        popupSelectTripTypePassenger.setOnClickListener {
            onSetSelectView(TripData.TripType.PASSENGER)
        }
        popupSelectTripTypeBus.setOnClickListener {
            onSetSelectView(TripData.TripType.BUS)
        }
        popupSelectTripTypeMotorcycle.setOnClickListener {
            onSetSelectView(TripData.TripType.MOTORCYCLE)
        }
        popupSelectTripTypeTrain.setOnClickListener {
            onSetSelectView(TripData.TripType.TRAIN)
        }
        popupSelectTripTypeTaxi.setOnClickListener {
            onSetSelectView(TripData.TripType.TAXI)
        }
        popupSelectTripTypeBicycle.setOnClickListener {
            onSetSelectView(TripData.TripType.BICYCLE)
        }
        popupSelectTripTypeOther.setOnClickListener {
            onSetSelectView(TripData.TripType.OTHER)
        }

        /*onCancelClick*/
        popupSelectTripTypeCancel.setOnClickListener {
            dialog.dismiss()
        }

        /*onConfirmClick*/
        popupSelectTripTypeConfirm.setOnClickListener {
            tempType?.let { tempType ->
                callback(tempType)
                dialog.dismiss()
            } ?: run {
                showSnackMessage("Please select trip role")
            }
        }

        dialog.show()
    }

    private fun findViews() {

        popupSelectTripTypeConfirm = dialog.findViewById(R.id.popupSelectTripTypeConfirm)
        popupSelectTripTypeCancel = dialog.findViewById(R.id.popupSelectTripTypeCancel)
        popupSelectTripTypeDriver = dialog.findViewById(R.id.popupSelectTripTypeDriver)
        popupSelectTripTypePassenger = dialog.findViewById(R.id.popupSelectTripTypePassenger)
        popupSelectTripTypeBus = dialog.findViewById(R.id.popupSelectTripTypeBus)
        popupSelectTripTypeMotorcycle = dialog.findViewById(R.id.popupSelectTripTypeMotorcycle)
        popupSelectTripTypeTrain = dialog.findViewById(R.id.popupSelectTripTypeTrain)
        popupSelectTripTypeTaxi = dialog.findViewById(R.id.popupSelectTripTypeTaxi)
        popupSelectTripTypeBicycle = dialog.findViewById(R.id.popupSelectTripTypeBicycle)
        popupSelectTripTypeOther = dialog.findViewById(R.id.popupSelectTripTypeOther)
    }

    private fun showSnackMessage(msg: String) {

        val view = dialog.findViewById<View>(R.id.change_driver_type_parent)
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
    }
}