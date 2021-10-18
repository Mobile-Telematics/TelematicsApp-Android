package use_case

import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.domain.repository.CarServiceRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VehicleUseCase @Inject constructor(
    private val carServiceRepo: CarServiceRepo
) {

    private val TAG = "VehicleUserCase"


    /*account vehicle*/
    fun getVehicles(): Flow<List<Vehicle>> {

        return flow {
            val data = carServiceRepo.getVehicles()
            emit(data)
        }
    }

    fun updateVehicle(vehicle: Vehicle): Flow<Unit> {

        return flow {
            val data = carServiceRepo.updateVehicle(vehicle)
            emit(data)
        }
    }

    fun createVehicle(vehicle: Vehicle): Flow<Unit> {

        return flow {
            val data = carServiceRepo.createVehicle(vehicle)
            emit(data)
        }
    }

    fun deleteVehicle(vehicle: Vehicle): Flow<Unit> {

        return flow {
            val data = carServiceRepo.deleteVehicle(vehicle.token!!)
            emit(data)
        }
    }

    fun getManufacturers(): Flow<List<ManufacturerData>> {

        return flow {
            val data = carServiceRepo.getManufacturers()
            emit(data)
        }
    }

    fun getModels(manufacturerId: Int): Flow<List<ModelData>> {

        return flow{
            val data = carServiceRepo.getModels(manufacturerId)
            emit(data)
        }
    }
}