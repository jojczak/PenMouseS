package pl.jojczak.penmouses.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.jojczak.penmouses.utils.PreferencesManager

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}