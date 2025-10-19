package pl.jojczak.penmouses.screen.manual.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import pl.jojczak.penmouses.screen.manual.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManualTopAppBar(
    modifier: Modifier = Modifier,
    onMenuIconClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.manual_title))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    onMenuIconClicked()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(R.string.manual_menu_content_description)
                )
            }
        },
        modifier = modifier
    )
}