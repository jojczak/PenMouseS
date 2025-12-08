package pl.jojczak.penmouses.screen.settings.tab.general

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.screen.settings.mvi.SettingsTabViewModel
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction
import javax.inject.Inject

@HiltViewModel
internal class SettingsGeneralViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : SettingsTabViewModel<SettingsGeneralState>(
    preferencesManager = preferencesManager,
    startState = SettingsGeneralState()
) {
    override fun onViewAction(action: SettingsViewAction) = when (action) {
        is SettingsViewAction.UpdatePreference<*> -> updateGeneralPreference(action)
        is SettingsViewAction.RefreshData -> refreshSettingsData()
        else -> Unit
    }

    fun <Key> updateGeneralPreference(action: SettingsViewAction.UpdatePreference<Key>) {
        when (action.key) {
            PrefKeys.ANALYTICS_ENABLED -> {
                (action.value as? Boolean)?.let { enabled ->
                    preferencesManager.put(action.key, action.value)
                    Firebase.analytics.setAnalyticsCollectionEnabled(enabled)
                    Firebase.crashlytics.isCrashlyticsCollectionEnabled = enabled
                    updateState { it.copy(analyticsEnabled = enabled) }
                }
            }
        }
    }

    override fun refreshSettingsData() = updateState {
        SettingsGeneralState(
            analyticsEnabled = preferencesManager.get(PrefKeys.ANALYTICS_ENABLED)
        )
    }
}