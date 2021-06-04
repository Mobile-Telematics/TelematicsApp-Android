package com.telematics.zenroad.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.telematics.authentication.data.Authentication
import com.telematics.data.BuildConfig
import com.telematics.data.api.DriveCoinsApi
import com.telematics.data.api.LoginApi
import com.telematics.data.api.RefreshApi
import com.telematics.data.api.UserStatisticsApi
import com.telematics.data.interceptor.AppIDInterceptor
import com.telematics.data.interceptor.ErrorInterceptor
import com.telematics.data.interceptor.InstanceValuesInterceptor
import com.telematics.data.interceptor.MainInterceptor
import com.telematics.data.repository.AuthRepoImpl
import com.telematics.data.repository.DashboardRepoImpl
import com.telematics.data.repository.SessionRepoImpl
import com.telematics.domain.repository.AuthenticationRepo
import com.telematics.domain.repository.DashboardRepo
import com.telematics.domain.repository.SessionRepo
import com.telematics.domain.repository.UserServicesRepo
import com.telematics.zenroad.App
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
@Module(
)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(application: App): Context {
        return application.applicationContext
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
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            "app_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
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
    fun provideAppIDInterceptor(): AppIDInterceptor = AppIDInterceptor()

    @Singleton
    @Provides
    fun provideInstanceValuesInterceptor(): InstanceValuesInterceptor = InstanceValuesInterceptor()

    @Singleton
    @Provides
    fun provideErrorInterceptor(): ErrorInterceptor = ErrorInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        mainInterceptor: MainInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        appIDInterceptor: AppIDInterceptor,
        instanceValuesInterceptor: InstanceValuesInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(mainInterceptor)
            addInterceptor(appIDInterceptor)
            addInterceptor(instanceValuesInterceptor)
            addInterceptor(errorInterceptor)
            authenticator(mainInterceptor)
            //if (BuildConfig.DEBUG)
            addInterceptor(loggingInterceptor)
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

    @Provides
    @Singleton
    fun provideAuthRepo(loginApi: LoginApi): UserServicesRepo = AuthRepoImpl(loginApi)

    @Provides
    @Singleton
    fun provideSessionRepo(sharedPreferences: SharedPreferences): SessionRepo =
        SessionRepoImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideDashboardRepo(
        driveCoinsApi: DriveCoinsApi,
        userStatisticsApi: UserStatisticsApi,
        sessionRepo: SessionRepo
    ): DashboardRepo = DashboardRepoImpl(driveCoinsApi, userStatisticsApi, sessionRepo)

    @Provides
    @Singleton
    fun provideAuthentication(
        authRepo: UserServicesRepo,
        sessionRepo: SessionRepo
    ): AuthenticationRepo {
        return Authentication(authRepo, sessionRepo)
    }
}
