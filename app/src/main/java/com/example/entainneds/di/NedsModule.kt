package com.example.entainneds.di

import com.example.entainneds.backend.NedsService
import com.example.entainneds.backend.RaceSummaryRepository
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@InstallIn(ViewModelComponent::class)
@Module
object NedsModule {
    private const val BASE_URL = "https://api.neds.com.au"

    @Provides
    fun provideNedsService(
    ): NedsService {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(NedsService::class.java)
    }

    @Provides
    fun provideRaceSummaryRepository(
        nedsService: NedsService
    ): RaceSummaryRepository {
        return RaceSummaryRepository(nedsService)
    }
}
