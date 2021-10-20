package com.telematics.data.repository

import com.telematics.data.api.CarServiceApi
import com.telematics.data.mappers.toCarUpdateBody
import com.telematics.data.mappers.toManufacturerData
import com.telematics.data.mappers.toModelData
import com.telematics.data.mappers.toVehicle
import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.domain.repository.CarServiceRepo
import com.telematics.domain.repository.UserRepo
import javax.inject.Inject

class CarServiceRepoImpl @Inject constructor(
    private val carServiceApi: CarServiceApi,
    private val userRepo: UserRepo
) : CarServiceRepo {

    override suspend fun getVehicles(): List<Vehicle> {

        val deviceToken = userRepo.getDeviceToken()
        val data = carServiceApi.getVehicles(deviceToken).result
        val listVehicle = data?.cars?.map { carRest ->
            val vehicle = carRest.toVehicle()
            val isActivated = !carServiceApi.getVehicleDevices(deviceToken, carRest.token!!)
                .result?.elms.isNullOrEmpty()
            vehicle.activated = isActivated
            return@map vehicle
        }
        return listVehicle ?: emptyList()
    }

    override suspend fun createVehicle(vehicle: Vehicle) {

        val deviceToken = userRepo.getDeviceToken()
        val carUpdateBody = vehicle.toCarUpdateBody()
        carServiceApi.addVehicle(deviceToken, carUpdateBody)
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {

        val deviceToken = userRepo.getDeviceToken()
        val carUpdateBody = vehicle.toCarUpdateBody()
        carServiceApi.updateVehicle(deviceToken, carUpdateBody, vehicle.token!!)
    }

    override suspend fun deleteVehicle(vehicleToken: String) {

        val deviceToken = userRepo.getDeviceToken()
        carServiceApi.deleteVehicle(deviceToken, vehicleToken)
    }

    override suspend fun getManufacturers(): List<ManufacturerData> {

        val data = carServiceApi.getManufacturers().result
        return data?.map { it.toManufacturerData() } ?: emptyList()
    }

    override suspend fun getModels(manufacturerId: Int): List<ModelData> {

        val data = carServiceApi.getModels(manufacturerId).result
        return data?.map { it.toModelData() } ?: emptyList()
    }
}