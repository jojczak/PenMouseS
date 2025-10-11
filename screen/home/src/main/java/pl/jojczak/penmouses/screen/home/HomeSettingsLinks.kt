package pl.jojczak.penmouses.screen.home

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun openSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_SETTINGS))
}

fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}