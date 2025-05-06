package pl.jojczak.penmouses.notifications

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.main.MainActivity

object NotificationsManager {
    fun showIdleNotification(context: Context) {
        createNotificationChannels(context)
        val notification = NotificationCompat.Builder(context, NotificationChannels.STATUS.channelId)
            .setSmallIcon(Notifications.IDLE.smallIcon)
            .setContentTitle(context.getString(Notifications.IDLE.title, context.getString(R.string.app_name)))
            .setContentText(context.getString(Notifications.IDLE.content))
            .setContentIntent(getAppPendingIntent(context))
            .addAction(getStopActionIntent(context))
            .build()

        showNotification(
            context = context,
            id = NotificationChannels.STATUS.notificationId,
            notification = notification
        )
    }

    fun showMouseHiddenNotification(context: Context) {
        createNotificationChannels(context)

        val notification = NotificationCompat.Builder(context, NotificationChannels.STATUS.channelId)
            .setSmallIcon(Notifications.HIDDEN.smallIcon)
            .setContentTitle(context.getString(Notifications.HIDDEN.title))
            .setContentText(context.getString(Notifications.HIDDEN.content))
            .setContentIntent(getAppPendingIntent(context))
            .addAction(getStopActionIntent(context))
            .build()

        showNotification(
            context = context,
            id = NotificationChannels.STATUS.notificationId,
            notification = notification
        )
    }

    fun showMouseSleepNotification(context: Context) {
        createNotificationChannels(context)

        val notification = NotificationCompat.Builder(context, NotificationChannels.STATUS.channelId)
            .setSmallIcon(Notifications.SLEEP.smallIcon)
            .setContentTitle(context.getString(Notifications.SLEEP.title))
            .setContentText(context.getString(Notifications.SLEEP.content))
            .setContentIntent(getAppPendingIntent(context))
            .addAction(getStopActionIntent(context))
            .build()

        showNotification(
            context = context,
            id = NotificationChannels.STATUS.notificationId,
            notification = notification
        )
    }

    fun showErrorNotification(context: Context) {
        createNotificationChannels(context)

        val notification = NotificationCompat.Builder(context, NotificationChannels.STATUS.channelId)
            .setSmallIcon(Notifications.ERROR.smallIcon)
            .setContentTitle(context.getString(Notifications.ERROR.title))
            .setContentText(context.getString(Notifications.ERROR.content, context.getString(R.string.app_name)))
            .setContentIntent(getAppPendingIntent(context))
            .build()

        showNotification(
            context = context,
            id = NotificationChannels.STATUS.notificationId,
            notification = notification
        )
    }

    fun cancelStatusNotifications(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            cancel(NotificationChannels.STATUS.notificationId)
        }
    }

    fun createNotificationChannels(context: Context) {
        if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            NotificationChannels.entries.forEach {
                val channel = NotificationChannel(
                    it.channelId,
                    context.getString(it.channelName),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                    enableLights(false)
                }

                context.getSystemService(NotificationManager::class.java).apply {
                    createNotificationChannel(channel)
                }
            }
        }
    }

    private fun showNotification(context: Context, id: Int, notification: Notification) {
        if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(id, notification)
            }
        }
    }

    private fun getAppPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getStopActionIntent(context: Context): NotificationCompat.Action {
        val actionIntent = Intent(context, NotificationsActionReceiver::class.java).apply {
            putExtra(NotificationsActionReceiver.ACTION, NotificationsActionReceiver.ACTION_TYPE_STOP)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_notification_stop,
            context.getString(R.string.notification_action_stop),
            pendingIntent
        ).build()

        return action
    }

    private enum class NotificationChannels(
        val notificationId: Int,
        val channelId: String,
        @StringRes val channelName: Int
    ) {
        STATUS(
            notificationId = 1001,
            channelId = "nt_status",
            channelName = R.string.notification_channel_status
        )
    }

    private enum class Notifications(
        @DrawableRes val smallIcon: Int,
        @StringRes val title: Int,
        @StringRes val content: Int
    ) {
        IDLE(
            smallIcon = R.drawable.ic_notification_stylus_default,
            title = R.string.notification_idle_title,
            content = R.string.notification_idle_content
        ),
        HIDDEN (
            smallIcon = R.drawable.ic_notification_stylus_hide,
            title = R.string.notification_hidden_title,
            content = R.string.notification_hidden_content
        ),
        SLEEP (
            smallIcon = R.drawable.ic_notification_stylus_sleep,
            title = R.string.notification_sleep_title,
            content = R.string.notification_sleep_content
        ),
        ERROR (
            smallIcon = R.drawable.ic_notification_stop,
            title = R.string.notification_interrupted_title,
            content = R.string.notification_interrupted_content
        )
    }
}