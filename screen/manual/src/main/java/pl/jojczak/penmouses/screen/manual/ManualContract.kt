package pl.jojczak.penmouses.screen.manual

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

sealed class ManualAction

sealed class ManualUserAction : ManualAction() {
    data class ChangeScreen(val page: ManualPageType) : ManualUserAction()
}

data class ManualViewState(
    val page: ManualPageType = ManualPageType.AboutPenMouseS,
    val markdownContent: String = ""
)

enum class ManualPageType(
    @param:RawRes val fileId: Int,
    @param:DrawableRes val iconId: Int,
    @param:DrawableRes val filledIconId: Int? = null,
    @param:StringRes val labelId: Int,
    @param:StringRes val descId: Int? = null,
) {
    AboutPenMouseS(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_spen,
        filledIconId = R.drawable.ic_spen_filled,
        labelId = R.string.manual_page_about_penmouse_s,
    ),
    WhatsNewIn2(
        fileId = R.raw.manual_page_whats_new,
        iconId = R.drawable.ic_star,
        filledIconId = R.drawable.ic_star_filled,
        labelId = R.string.manual_page_whats_new
    ),
    HowToUse(
        fileId = R.raw.manual_page_how_to_use,
        iconId = R.drawable.ic_question_mark,
        labelId = R.string.manual_page_how_to_use
    ),
    MouseMode(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_mouse_mode,
        filledIconId = R.drawable.ic_mouse_mode_filled,
        labelId = R.string.manual_page_mouse_mode
    ),
    PointMode(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_point_mode,
        labelId = R.string.manual_page_point_mode
    ),
    ScrollMode(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_scroll_mode,
        filledIconId = R.drawable.ic_scroll_mode_filled,
        labelId = R.string.manual_page_scroll_mode
    ),
    PreparationStep1(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_preparation_1,
        filledIconId = R.drawable.ic_preparation_1_filled,
        labelId = R.string.manual_page_step_1,
        descId = R.string.manual_page_step_1_desc
    ),
    PreparationStep2(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_preparation_2,
        filledIconId = R.drawable.ic_preparation_2_filled,
        labelId = R.string.manual_page_step_2,
        descId = R.string.manual_page_step_2_desc
    ),
    PreparationStep3(
        fileId = R.raw.manual_page_about_pen_mouse_s,
        iconId = R.drawable.ic_preparation_3,
        labelId = R.string.manual_page_step_3,
        descId = R.string.manual_page_step_3_desc
    )
}