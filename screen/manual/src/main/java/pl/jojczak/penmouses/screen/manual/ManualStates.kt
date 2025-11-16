package pl.jojczak.penmouses.screen.manual

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.halilibo.richtext.markdown.node.AstDocument
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstNodeLinks
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.R as coreR

sealed class ManualAction

sealed class ManualUserAction : ManualAction() {
    data class ChangePage(
        val page: ManualPageType,
        val isDarkMode: Boolean
    ) : ManualUserAction()
}

data class ManualViewState(
    val page: ManualPageType = ManualPageType.AboutPenMouseS,
    val markdownNode: AstNode = AstNode(type = AstDocument, links = AstNodeLinks())
)

internal data class ManualPageData(
    @param:RawRes val fileId: Int,
    @param:DrawableRes val iconId: Int,
    @param:DrawableRes val filledIconId: Int? = null,
    @param:StringRes val labelId: Int,
    @param:StringRes val descId: Int? = null,
    val featuresBefore: Set<ManualPageFeature> = emptySet(),
    val featuresAfter: Set<ManualPageFeature> = emptySet()
)

sealed class ManualPageFeature {
    data object AppInfo : ManualPageFeature()
    data object Banners : ManualPageFeature()
    data class Video(val uri: Uri) : ManualPageFeature()
}

internal val manualPageData = mapOf(
    ManualPageType.AboutPenMouseS to ManualPageData(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_spen,
        filledIconId = R.drawable.ic_spen_filled,
        labelId = R.string.manual_page_about_penmouse_s,
        featuresBefore = setOf(ManualPageFeature.AppInfo),
        featuresAfter = setOf(ManualPageFeature.Banners)
    ),
    ManualPageType.HowToUse to ManualPageData(
        fileId = R.raw.manual_page_how_to_use,
        iconId = R.drawable.ic_question_mark,
        labelId = R.string.manual_page_how_to_use
    ),
    ManualPageType.Troubleshooting to ManualPageData(
        fileId = R.raw.manual_page_troubleshooting,
        iconId = R.drawable.ic_error,
        filledIconId = R.drawable.ic_error_filled,
        labelId = R.string.manual_page_troubleshooting
    ),
    ManualPageType.PreparationStep1 to ManualPageData(
        fileId = R.raw.manual_page_preparation_step_1,
        iconId = R.drawable.ic_preparation_1,
        filledIconId = R.drawable.ic_preparation_1_filled,
        labelId = R.string.manual_page_step_1,
        descId = R.string.manual_page_step_1_desc,
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/step1.mp4".toUri())
        )
    ),
    ManualPageType.PreparationStep2 to ManualPageData(
        fileId = R.raw.manual_page_preparation_step_2,
        iconId = R.drawable.ic_preparation_2,
        filledIconId = R.drawable.ic_preparation_2_filled,
        labelId = R.string.manual_page_step_2,
        descId = R.string.manual_page_step_2_desc,
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/step2.mp4".toUri())
        )
    ),
    ManualPageType.PreparationStep3 to ManualPageData(
        fileId = R.raw.manual_page_preparation_step_3,
        iconId = R.drawable.ic_preparation_3,
        labelId = R.string.manual_page_step_3,
        descId = R.string.manual_page_step_3_desc,
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/step3.mp4".toUri())
        )
    ),
    ManualPageType.MouseMode to ManualPageData(
        fileId = R.raw.manual_page_mouse,
        iconId = coreR.drawable.ic_mouse_mode,
        filledIconId = coreR.drawable.ic_mouse_mode_filled,
        labelId = coreR.string.pen_mode_mouse,
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/mouse.mp4".toUri())
        )
    ),
    ManualPageType.PointMode to ManualPageData(
        fileId = R.raw.manual_page_point,
        iconId = coreR.drawable.ic_point_mode,
        labelId = coreR.string.pen_mode_point,
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/point.mp4".toUri())
        )
    ),
    ManualPageType.ScrollMode to ManualPageData(
        fileId = R.raw.manual_page_scroll,
        iconId = coreR.drawable.ic_scroll_mode,
        filledIconId = coreR.drawable.ic_scroll_mode_filled,
        labelId = coreR.string.pen_mode_scroll,
        featuresAfter = setOf(
            ManualPageFeature.Video("asset:///manual_assets/scroll.mp4".toUri())
        )
    )
)