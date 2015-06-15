package com.training.android.mynotification;


        import android.app.Activity;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.app.TaskStackBuilder;
        import android.support.v4.app.NotificationCompat;

        import android.util.Log;
        import android.view.View;

        import java.util.Timer;
        import java.util.TimerTask;

public class NotificationActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    public void createNotification(View view) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationRecieverActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification notification = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .addAction(R.mipmap.ic_launcher, "Call", pIntent)
                .addAction(R.mipmap.ic_launcher, "More", pIntent)
                .addAction(R.mipmap.ic_launcher, "And more", pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

    }

    public void createNotificationExpanded(View view) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event tracker")
                .setContentText("Events received");

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];

        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        // Moves events into the expanded layout
        for (int i=0; i < events.length; i++) {

            inboxStyle.addLine("this appears when expanded");
        }
        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, mBuilder.build());


    }

    public void createBackStackableNotification(View view){
        Intent resultIntent = new Intent(this, NotificationRecieverActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(NotificationRecieverActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, builder.build());
    }

    public void createSpecialActivityNotification(View view){
        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(this, SpecialActivity.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Puts the PendingIntent into the notification builder
        builder.setContentIntent(notifyPendingIntent)
                .setContentTitle("Special Activity")
                .setContentText("Launch Special Activity")
                .setSmallIcon(R.mipmap.ic_launcher);
        // Notifications are issued by sending them to the NotificationManager system service.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds an anonymous Notification object from the builder, and passes it to the NotificationManager
        mNotificationManager.notify(7, builder.build());
    }

    public void createUpdatableNotification(View view){

        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        final int notifyID = 1;
        final NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher);

        // Start of a loop that processes data and then notifies the user
        final Timer timer;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int numMessages = 0;

            @Override
            public void run() {
                numMessages++;
                mNotifyBuilder.setContentText("I have done some " + numMessages + " work for you Sir!")
                        .setNumber(numMessages);
                // Because the ID remains unchanged, the existing notification is
                // updated.
                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());

                if (numMessages == 4)
                    timer.cancel();

            }
        }, 2000, 5000);
    }

    public void createProgressNotification(View view){
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher);
// Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(0, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(1*2000);
                            } catch (InterruptedException e) {
                                Log.d("Progress notification: ", "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(0, mBuilder.build());
                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();
    }


}
