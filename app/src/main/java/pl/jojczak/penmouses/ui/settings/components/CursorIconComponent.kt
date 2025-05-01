package pl.jojczak.penmouses.ui.settings.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.elevation_2
import pl.jojczak.penmouses.ui.theme.pad_l
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.pad_xs
import pl.jojczak.penmouses.ui.theme.radius_m
import pl.jojczak.penmouses.utils.CursorType
import pl.jojczak.penmouses.utils.getCursorBitmap

@Composable
fun CursorIconComponent(
    cursorType: CursorType,
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {}
) {
    val radioButtonsHeight = remember { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(pad_l),
        modifier = Modifier
            .padding(pad_xl)
            .fillMaxWidth()
    ) {
        CursorPreview(
            cursorType = cursorType,
            radioButtonsHeight = radioButtonsHeight.intValue,
            onCustomCursorFileSelected = onCustomCursorFileSelected
        )
        CursorTypeSelector(
            cursorType = cursorType,
            onCursorTypeChange = onCursorTypeChange,
            radioButtonsHeight = radioButtonsHeight
        )
    }
}

@Composable
private fun RowScope.CursorPreview(
    cursorType: CursorType,
    radioButtonsHeight: Int,
    onCustomCursorFileSelected: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    var cursorBitmap by remember(cursorType) {
        mutableStateOf(getCursorBitmap(context, cursorType))
    }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            onCustomCursorFileSelected(uri)
            cursorBitmap = getCursorBitmap(context, cursorType)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    val previewCorner by animateDpAsState(
        targetValue = if (cursorType == CursorType.CUSTOM) 0.dp else radius_m,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Surface(
            tonalElevation = elevation_2,
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = radius_m,
                        topEnd = radius_m,
                        bottomStart = previewCorner,
                        bottomEnd = previewCorner,
                    )
                )
                .height(with(LocalDensity.current) { radioButtonsHeight.toDp() })

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(pad_l)
                    .fillMaxSize()
            ) {
                cursorBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.settings_cursor_preview),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        CustomCursorButton(
            cursorType = cursorType,
            pickImage = pickImage
        )
    }
}

@Composable
private fun CustomCursorButton(
    cursorType: CursorType,
    pickImage: ManagedActivityResultLauncher<String, Uri?>
) {
    AnimatedVisibility(
        visible = cursorType == CursorType.CUSTOM,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            FilledTonalButton(
                onClick = {
                    pickImage.launch("image/*")
                },
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = radius_m,
                    bottomEnd = radius_m
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(stringResource(R.string.settings_change_cursor_button))
            }
        }
    }
}

@Composable
private fun RowScope.CursorTypeSelector(
    cursorType: CursorType,
    onCursorTypeChange: (CursorType) -> Unit = {},
    radioButtonsHeight: MutableIntState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_xs),
        modifier = Modifier
            .selectableGroup()
            .weight(2f)
            .onGloballyPositioned { coordinates ->
                radioButtonsHeight.intValue = coordinates.size.height
            }
    ) {
        CursorType.entries.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .selectable(
                        selected = cursorType == it,
                        onClick = { onCursorTypeChange(it) },
                        role = Role.RadioButton
                    )
                    .padding(pad_xs)
            ) {
                RadioButton(
                    selected = cursorType == it,
                    onClick = null
                )
                Text(
                    text = stringResource(it.label),
                    modifier = Modifier.padding(start = pad_s)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCursorIconComponent() {
    PenMouseSTheme {
        Surface {
            CursorIconComponent(CursorType.LIGHT)
        }
    }
}