package pl.jojczak.penmouses.ui.home

data class HomeScreenState(
    val areSPenFeaturesDisabled: Boolean = false,
    val isAccessibilityEnabled: Boolean = false,

    val showStep1Dialog: Boolean = false,
    val showStep2Dialog: Boolean = false,
)