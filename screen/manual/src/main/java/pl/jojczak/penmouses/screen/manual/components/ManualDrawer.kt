package pl.jojczak.penmouses.screen.manual.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.radius_l
import pl.jojczak.penmouses.screen.manual.ManualPageType
import pl.jojczak.penmouses.screen.manual.R


private val MAX_DRAWER_WIDTH = 281.dp

val sections = mapOf(
    R.string.manual_page_category_main to listOf(
        ManualPageType.AboutPenMouseS,
        ManualPageType.WhatsNewIn2,
        ManualPageType.HowToUse
    ),
    R.string.manual_page_category_modes to listOf(
        ManualPageType.MouseMode,
        ManualPageType.PointMode,
        ManualPageType.ScrollMode
    ),
    R.string.manual_page_category_preparation to listOf(
        ManualPageType.PreparationStep1,
        ManualPageType.PreparationStep2,
        ManualPageType.PreparationStep3
    )
)

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun ManualDrawer(
    manualHazeState: HazeState,
    currentPageType: ManualPageType,
    onPageClicked: (ManualPageType) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.Transparent,
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
            .widthIn(max = MAX_DRAWER_WIDTH)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = radius_l,
                    bottomEnd = radius_l,
                    bottomStart = 0.dp
                )
            )
            .hazeEffect(
                state = manualHazeState,
                style = HazeMaterials.thin(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
    ) {
        DrawerContent(
            currentPageType = currentPageType,
            onPageClicked = onPageClicked
        )
    }
}

@Composable
private fun DrawerContent(
    currentPageType: ManualPageType,
    onPageClicked: (ManualPageType) -> Unit
) {
    val insets = WindowInsets.safeDrawing.asPaddingValues()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = insets.calculateTopPadding() + pad_l,
            bottom = insets.calculateBottomPadding() + pad_l,
            start = insets.calculateStartPadding(LocalLayoutDirection.current) + pad_l,
            end = insets.calculateEndPadding(LocalLayoutDirection.current) + pad_l
        )
    ) {
        drawerTitle()
        sections.forEach { (titleRes, pages) ->
            drawerSectionTitle(titleId = titleRes)
            pages.forEach { page ->
                drawerItem(
                    pageType = page,
                    currentPageType = currentPageType,
                    onPageClicked = onPageClicked
                )
            }
        }
    }
}

private fun LazyListScope.drawerTitle() = item {
    Text(
        text = "Manual",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(
            start = pad_l,
            top = pad_s,
            bottom = pad_l
        ),
    )
}

private fun LazyListScope.drawerSectionTitle(
    @StringRes titleId: Int
) = item {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = pad_s)
    )
    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(pad_l),
    )
}

private fun LazyListScope.drawerItem(
    pageType: ManualPageType,
    currentPageType: ManualPageType,
    onPageClicked: (ManualPageType) -> Unit,
) = item {
    val selected = currentPageType == pageType

    NavigationDrawerItem(
        label = {
            Column {
                Text(text = stringResource(pageType.labelId))
                pageType.descId?.let {
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        icon = {
            val iconId = if (selected && pageType.filledIconId != null) {
                pageType.filledIconId
            } else {
                pageType.iconId
            }

            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
        },
        selected = selected,
        onClick = { onPageClicked(pageType) }
    )
}
