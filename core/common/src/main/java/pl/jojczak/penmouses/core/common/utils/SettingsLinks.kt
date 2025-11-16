package pl.jojczak.penmouses.core.common.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun openSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_SETTINGS))
}

fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}