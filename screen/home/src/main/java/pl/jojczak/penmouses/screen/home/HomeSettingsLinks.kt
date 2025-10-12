package pl.jojczak.penmouses.screen.home

import android.content.Context
import android.content.Intent
import android.provider.Settings

internal fun openSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_SETTINGS))
}

internal fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}