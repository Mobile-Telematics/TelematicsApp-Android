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
import com.telematics.data.interceptor.TimeoutInterceptor
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.data.repository.*
import com.telematics.data.tracking.TrackingApiImpl
import com.telematics.data.utils.ImageLoader
import com.telematics.domain.repository.*
import dagger.Binds
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
    fun provideGsonConverterFactory(): Converter.Factory = GsonConverterFactory.create(Gson())


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
    fun provideInstanceValuesInterceptor(): InstanceValuesInterceptor = InstanceValuesInterceptor()

    @Singleton
    @Provides
    fun provideErrorInterceptor(): ErrorInterceptor = ErrorInterceptor()

    @Singleton
    @Provides
    fun providesTimeoutInterceptor(): TimeoutInterceptor = TimeoutInterceptor(
        connectTimeoutMillis = 30_000,
        readTimeoutMillis = 30_000,
        writeTimeoutMillis = 30_000
    )

    @Singleton
    @Provides
    fun provideOkHttpClient(
        mainInterceptor: MainInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        instanceValuesInterceptor: InstanceValuesInterceptor,
        errorInterceptor: ErrorInterceptor,
        timeoutInterceptor: TimeoutInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(timeoutInterceptor)
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
    fun provideImageLoader(): ImageLoader = ImageLoader()

}


@Module
@InstallIn(SingletonComponent::class)
interface RepoModuleBinds {
    @Binds
    @Singleton
    fun bindAuthRepo(repo: AuthRepoImpl): UserServicesRepo

    @Binds
    @Singleton
    fun bindSessionRepo(repo: SessionRepoImpl): SessionRepo

    @Binds
    @Singleton
    fun bindUserRepo(repo: UserRepoImpl): UserRepo

    @Binds
    @Singleton
    fun bindDashboardRepo(repo: StatisticRepoImpl): StatisticRepo

    @Binds
    @Singleton
    fun bindLeaderboardRepo(repo: LeaderboardRepoImpl): LeaderboardRepo

    @Binds
    @Singleton
    fun bindRewardRepo(repo: RewardRepoImpl): RewardRepo

    @Binds
    @Singleton
    fun bindCarServiceRepo(repo: CarServiceRepoImpl): CarServiceRepo

    @Binds
    @Singleton
    fun bindSettingsRepo(repo: SettingsRepoImpl): SettingsRepo

    @Binds
    @Singleton
    fun bindOnDemandRepo(repo: OnDemandRepoImpl): OnDemandRepo

    @Binds
    @Singleton
    fun bindTrackingRepo(repo: TrackingApiImpl): TrackingApiRepo

    @Binds
    @Singleton
    fun bindAuthentication(repo: Authentication): AuthenticationRepo

    @Binds
    @Singleton
    fun bindDateFormatter(fromatter: MeasuresFormatterImpl): MeasuresFormatter
}