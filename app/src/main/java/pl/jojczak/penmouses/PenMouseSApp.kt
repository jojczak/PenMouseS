package pl.jojczak.penmouses

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pl.jojczak.penmouses.di.ActivityProvider
import javax.inject.Inject

@HiltAndroidApp
class PenMouseSApp : Application() {
    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityProvider)
    }
}