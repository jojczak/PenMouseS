package pl.jojczak.penmouses.ui.home

import pl.jojczak.penmouses.service.AppToServiceEvent

data class HomeScreenState(
    val areSPenFeaturesDisabled: Boolean = false,
    val isAccessibilityEnabled: Boolean = false,

    val showStep1Dialog: Boolean = false,
    val showStep2Dialog: Boolean = false,

    val serviceStatus: AppToServiceEvent.ServiceStatus = AppToServiceEvent.ServiceStatus.OFF
)