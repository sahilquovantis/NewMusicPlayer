package com.quovantis.music.helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.quovantis.music.R;
import com.quovantis.music.constants.AppConstants;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.models.NotificationModel;
import com.quovantis.music.module.home.HomeActivity;
import com.quovantis.music.music.MusicService;

/**
 * Notification helper class generates notifications for current song.
 */

public class NotificationHelper {
    private Context mContext;

    public NotificationHelper(Context mContext) {
        this.mContext = mContext;
    }

    public Notification createNotification(NotificationModel model, MediaSessionCompat.Token token,
                                           int iconForPlayPause, String playPause, String action) {
        if (model != null && token != null) {
            String title = model.getTitle();
            String artist = model.getArtist();
            Bitmap image = model.getBitmap();

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification_view);
            remoteViews.setTextViewText(R.id.tv_song_name, title);
            remoteViews.setTextViewText(R.id.tv_song_artist, artist);
            remoteViews.setImageViewBitmap(R.id.iv_song_thumbnail, image);
            remoteViews.setImageViewResource(R.id.iv_play_pause, iconForPlayPause);
            remoteViews.setOnClickPendingIntent(R.id.iv_next_song, getIntent(IntentKeys.INTENT_ACTION_NEXT));
            remoteViews.setOnClickPendingIntent(R.id.iv_previous_song, getIntent(IntentKeys.INTENT_ACTION_PREVIOUS));
            remoteViews.setOnClickPendingIntent(R.id.iv_close_noti, getIntent(IntentKeys.INTENT_ACTION_STOP));
            remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, getIntent(action));

            RemoteViews smallView = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification_compat);
            smallView.setTextViewText(R.id.tv_song_name, title);
            smallView.setTextViewText(R.id.tv_song_artist, artist);
            smallView.setImageViewBitmap(R.id.iv_song_thumbnail, image);
            smallView.setImageViewResource(R.id.iv_play_pause, iconForPlayPause);
            smallView.setOnClickPendingIntent(R.id.iv_next_song, getIntent(IntentKeys.INTENT_ACTION_NEXT));
            smallView.setOnClickPendingIntent(R.id.iv_previous_song, getIntent(IntentKeys.INTENT_ACTION_PREVIOUS));
            smallView.setOnClickPendingIntent(R.id.iv_close_noti, getIntent(IntentKeys.INTENT_ACTION_STOP));
            smallView.setOnClickPendingIntent(R.id.iv_play_pause, getIntent(action));

            Intent showActivityIntent = new Intent(mContext, HomeActivity.class);
            showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent showActivityPendingIntent = PendingIntent.getActivity(mContext, 1, showActivityIntent, 0);

            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext)
                    .setSmallIcon(iconForPlayPause)
                    .setCustomBigContentView(remoteViews)
                    .setCustomContentView(smallView)
                    .setContentIntent(showActivityPendingIntent)
                    .setStyle(new NotificationCompat.MediaStyle().setMediaSession(token));

            if (action.equalsIgnoreCase(IntentKeys.INTENT_ACTION_PLAY)) {
                Intent intent = new Intent(mContext, MusicService.class);
                intent.setAction(IntentKeys.INTENT_ACTION_STOP);
                PendingIntent pendingIntent = PendingIntent.getService(mContext, 1, intent, 0);
                builder.setDeleteIntent(pendingIntent);
            }
            return builder.build();
        }
        return null;
    }

    /*public Notification createNotification(NotificationModel model, MediaSessionCompat.Token token,
                                           int iconForPlayPause, String playPause, String action) {
        if (model != null && token != null) {
            String title = model.getTitle();
            String artist = model.getArtist();
            Bitmap image = model.getBitmap();

            NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
            style.setMediaSession(token);

            Intent showActivityIntent = new Intent(mContext, HomeActivity.class);
            showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent showActivityPendingIntent = PendingIntent.getActivity(mContext, 1, showActivityIntent, 0);

            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext)
                    .setSmallIcon(iconForPlayPause)
                    .setContentTitle(title)
                    .setContentText(artist)
                    .setContentIntent(showActivityPendingIntent)
                    .setLargeIcon(image)
                    .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2).setMediaSession(token))
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            builder.addAction(createAction(R.drawable.ic_action_previous, "Previous", IntentKeys.INTENT_ACTION_PREVIOUS));
            builder.addAction(createAction(iconForPlayPause, playPause, action));
            builder.addAction(createAction(R.drawable.ic_action_next, "Next", IntentKeys.INTENT_ACTION_NEXT));
            builder.addAction(createAction(R.drawable.ic_action_remove, "Close", IntentKeys.INTENT_ACTION_STOP));
            style.setShowActionsInCompactView(0, 1, 2);
            if (action.equalsIgnoreCase(IntentKeys.INTENT_ACTION_PLAY)) {
                Intent intent = new Intent(mContext, MusicService.class);
                intent.setAction(IntentKeys.INTENT_ACTION_STOP);
                PendingIntent pendingIntent = PendingIntent.getService(mContext, 1, intent, 0);
                builder.setDeleteIntent(pendingIntent);
            }
            return builder.build();
        }
        return null;
    }*/
    private NotificationCompat.Action createAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    private PendingIntent getIntent(String intentAction) {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.setAction(intentAction);
        return PendingIntent.getService(mContext, 1, intent, 0);
    }
}