package pl.jojczak.penmouses.utils

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri

fun Modifier.openUrl(ctx: Context, @StringRes url: Int) = this.clickable {
    ctx.startActivity(
        Intent(
            Intent.ACTION_VIEW, ctx.getString(
                url
            ).toUri()
        )
    )
}