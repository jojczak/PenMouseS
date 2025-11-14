package pl.jojczak.penmouses.core.common.types

import android.net.Uri
import androidx.core.net.toUri

enum class ManualPageType(
    val featuresBefore: Set<ManualPageFeature> = emptySet(),
    val featuresAfter: Set<ManualPageFeature> = emptySet()
) {
    AboutPenMouseS(
        featuresBefore = setOf(ManualPageFeature.AppInfo),
        featuresAfter = setOf(ManualPageFeature.Banners)
    ),
    HowToUse,
    PreparationStep1(
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/step1.mp4".toUri())
        )
    ),
    PreparationStep2(
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/step2.mp4".toUri())
        )
    ),
    PreparationStep3(
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/step3.mp4".toUri())
        )
    ),
    MouseMode,
    PointMode,
    ScrollMode
}

sealed class ManualPageFeature {
    data object AppInfo : ManualPageFeature()
    data object Banners : ManualPageFeature()
    data class Video(val uri: Uri) : ManualPageFeature()
}