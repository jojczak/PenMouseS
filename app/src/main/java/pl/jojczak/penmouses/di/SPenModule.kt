package pl.jojczak.penmouses.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.jojczak.penmouses.service.SPenManager
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
    fun provideSPenManager2(activityProvider: ActivityProvider): SPenManager {
        return SPenManager(activityProvider)
    }
}