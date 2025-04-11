package pl.jojczak.penmouses.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.jojczak.penmouses.utils.SPenManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SPenModule {
    @Provides
    @Singleton
    fun provideActivityProvider(): ActivityProvider {
        return ActivityProvider()
    }

    @Provides
    @Singleton
    fun provideSPenManager(
        activityProvider: ActivityProvider,
        sharedPreferences: SharedPreferences
    ): SPenManager {
        return SPenManager(activityProvider, sharedPreferences)
    }
}