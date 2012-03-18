package kelsos.mbremote.Messaging;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class AppNotificationManager {
    private static AppNotificationManager ourInstance = new AppNotificationManager();
    private Handler _nmHandler;

    public static AppNotificationManager getInstance() {
        return ourInstance;
    }

    private AppNotificationManager() {
        _nmHandler = new Handler();
    }
    /**
     * Given a string that contains a message the function will display the message
     * in a toast window.
     * @param message The message that will be displayed.
     */
    public void showToastMessage(final Context context, final String message) {
        _nmHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  Using an id of the string stored in the strings XML this function
     *  displays a toast window.
     */
    public void showToastMessage(final Context context, final int id) {
        _nmHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
