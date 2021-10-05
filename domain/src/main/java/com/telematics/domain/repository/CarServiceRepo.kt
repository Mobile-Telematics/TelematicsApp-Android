package com.telematics.domain.repository

import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.domain.model.carservice.Vehicle

interface CarServiceRepo {
    suspend fun getVehicles(): List<Vehicle>
    suspend fun createVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicleToken: String)
    suspend fun getManufacturers(): List<ManufacturerData>
    suspend fun getModels(manufacturerId: Int): List<ModelData>
}