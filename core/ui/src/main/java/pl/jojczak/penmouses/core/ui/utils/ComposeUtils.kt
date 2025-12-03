package pl.jojczak.penmouses.core.ui.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import pl.jojczak.penmouses.core.ui.R

fun openUrl(ctx: Context, @StringRes url: Int) {
    val intent = Intent(Intent.ACTION_VIEW, ctx.getString(url).toUri())

    try {
        ctx.startActivity(intent)
    } catch (_: Exception) {
        showLinkErrorToast(ctx)
    }
}

fun Modifier.openUrlClickable(ctx: Context, @StringRes url: Int) = this.clickable {
    openUrl(ctx, url)
}

private fun showLinkErrorToast(ctx: Context) = Toast.makeText(
    ctx,
    ctx.getString(R.string.common_link_error),
    Toast.LENGTH_SHORT
).show()