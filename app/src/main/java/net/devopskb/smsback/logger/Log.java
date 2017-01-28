package net.devopskb.smsback.logger;

/**
 * Created by chenchuk on 1/29/2016.
 */
public class Log {

    // catching all logs sent to Log.e() and process before sending
    // to the original Log.e
    public static void e(String tag, String message){
        Logger.getLogger().debug(message);

        // debugging on android studio console
        android.util.Log.e("", message);
    }
}
