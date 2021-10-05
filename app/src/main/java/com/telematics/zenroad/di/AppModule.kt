package com.telematics.zenroad.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.telematics.authentication.data.Authentication
import com.telematics.data.BuildConfig
import com.telematics.data.api.*
import com.telematics.data.interceptor.ErrorInterceptor
import com.telematics.data.interceptor.InstanceValuesInterceptor
import com.telematics.data.interceptor.MainInterceptor
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.data.model.tracking.TripsMapper
import com.telematics.data.repository.*
import com.telematics.data.tracking.TrackingApiImpl
import com.telematics.data.utils.ImageLoader
import com.telematics.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext application: Context): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideTransactionsUrl(): String {
        return BuildConfig.userServiceUrl
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Reusable
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Reusable
    @Provides
    fun provideGsonConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val applicationLabel = context.applicationInfo.loadLabel(context.packageManager).toString()
        return context.getSharedPreferences(
            "${applicationLabel}_app_shared_prefs",
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun provideMainInterceptor(
        refreshApi: RefreshApi,
        sessionRepository: SessionRepo
    ): MainInterceptor {

        return MainInterceptor(sessionRepository, refreshApi)
    }

    @Singleton
    @Provides
    fun provideInstanceValuesInterceptor(): InstanceValuesInterceptor = InstanceValuesInterceptor()

    @Singleton
    @Provides
    fun provideErrorInterceptor(gson: Gson): ErrorInterceptor = ErrorInterceptor(gson)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        mainInterceptor: MainInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        instanceValuesInterceptor: InstanceValuesInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(mainInterceptor)
            addInterceptor(instanceValuesInterceptor)
            authenticator(mainInterceptor)
            addInterceptor(loggingInterceptor)
            addInterceptor(errorInterceptor)
        }.build()
    }

    @Singleton
    @Provides
    fun provideLoginApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): LoginApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.userServiceUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(LoginApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDriveCoinsApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): DriveCoinsApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.driveCoinUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(DriveCoinsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserStatisticsApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): UserStatisticsApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.userStatisticsUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(UserStatisticsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideLeaderboardApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): LeaderboardApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.leaderboardUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(LeaderboardApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTripEventTypeApi(
        loggingInterceptor: HttpLoggingInterceptor,
        converterFactory: Converter.Factory
    ): TripEventTypeApi {

        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
        }.build()

        val retrofit = Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.tripEventTypeUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(TripEventTypeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRefreshApi(
        loggingInterceptor: HttpLoggingInterceptor,
        converterFactory: Converter.Factory,
    ): RefreshApi {
        val client = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor)
        }.build()
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.userServiceUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(RefreshApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCarServiceApi(
        loggingInterceptor: HttpLoggingInterceptor,
        converterFactory: Converter.Factory
    ): CarServiceApi {
        val client = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor)
        }.build()
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.carServiceUrl)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(CarServiceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepo(loginApi: LoginApi): UserServicesRepo = AuthRepoImpl(loginApi)

    @Provides
    @Singleton
    fun provideSessionRepo(sharedPreferences: SharedPreferences): SessionRepo =
        SessionRepoImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideUserRepo(sharedPreferences: SharedPreferences): UserRepo =
        UserRepoImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideDashboardRepo(
        driveCoinsApi: DriveCoinsApi,
        userStatisticsApi: UserStatisticsApi,
        leaderboardApi: LeaderboardApi,
        userRepo: UserRepo
    ): StatisticRepo = StatisticRepoImpl(driveCoinsApi, userStatisticsApi, leaderboardApi, userRepo)

    @Provides
    @Singleton
    fun provideLeaderboardRepo(
        leaderboardApi: LeaderboardApi,
        userRepo: UserRepo
    ): LeaderboardRepo = LeaderboardRepoImpl(leaderboardApi, userRepo)

    @Provides
    @Singleton
    fun provideRewardRepo(
        driveCoinsApi: DriveCoinsApi,
        userStatisticsApi: UserStatisticsApi,
        settingsRepo: SettingsRepo
    ): RewardRepo = RewardRepoImpl(driveCoinsApi, userStatisticsApi, settingsRepo)

    @Provides
    @Singleton
    fun provideCarServiceRepo(
        carServiceApi: CarServiceApi,
        userRepo: UserRepo
    ): CarServiceRepo = CarServiceRepoImpl(carServiceApi, userRepo)

    @Provides
    @Singleton
    fun provideAuthentication(
        authRepo: UserServicesRepo,
        sessionRepo: SessionRepo,
        userRepo: UserRepo,
        context: Context
    ): AuthenticationRepo {
        return Authentication(authRepo, sessionRepo, userRepo, context)
    }

    @Provides
    @Singleton
    fun provideDateFormatter(settingsRepo: SettingsRepo): MeasuresFormatter {
        return MeasuresFormatterImpl(settingsRepo)
    }

    @Provides
    @Singleton
    fun provideTripsMapper(
        measuresFormatter: MeasuresFormatter
    ): TripsMapper {
        return TripsMapper(measuresFormatter)
    }

    @Provides
    @Singleton
    fun provideTrackingRepo(
        tripsMapper: TripsMapper,
        tripEventTypeApi: TripEventTypeApi
    ): TrackingApiRepo {
        return TrackingApiImpl(tripsMapper, tripEventTypeApi)
    }

    @Provides
    @Singleton
    fun provideSettingsRepo(sharedPreferences: SharedPreferences): SettingsRepo {
        return SettingsRepoImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideImageLoader(): ImageLoader {
        return ImageLoader()
    }
}
