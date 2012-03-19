package kelsos.mbremote.Messaging;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.nio.channels.AsynchronousCloseException;

public class AppNotificationManager {
    private static AppNotificationManager ourInstance = new AppNotificationManager();

    private Context context;
    
    public void setContext(Context context)
    {
        this.context = context;
    }

    public static AppNotificationManager getInstance() {
        return ourInstance;
    }

    private AppNotificationManager() {

    }
    
    private class ToastMessageTask extends AsyncTask<String,String,String>
    {
        String toastMessage;
        @Override
        protected String doInBackground(String... strings) {
            toastMessage = strings[0];
            return toastMessage;
        }

        @Override
        protected void onPostExecute(String string)
        {
            showToast(string);
        }
    }

    /**
     * Given a string that contains a message the function will display the message
     * in a toast window.
     * @param message The message that will be displayed.
     */
    private void showToast(final String message) {
                if(context!=null)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    /**
     *  Using an id of the string stored in the strings XML this function
     *  displays a toast window.
     */
    public void showToastMessage(final int id) {
        if(context==null) return;
        String data = context.getString(id);
        new ToastMessageTask().execute(data);
    }
    
    public void showToastMessage(final String message)
    {
        if(context==null) return;
        new ToastMessageTask().execute(message);
    }

}
