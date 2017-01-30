package net.devopskb.smsback;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogMonitorActivity extends AppCompatActivity {

    public static final String LOG_FILENAME = "log.txt";
    public static final String LOG_DIRECTORY= "smsback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_monitor);
        readLogFile();

    }
    @Override
    protected void onResume(){
        super.onResume();
        readLogFile();
    }



    public void readLogFile()
    {

        File logs_dir = new File(Environment.getExternalStorageDirectory(), LOG_DIRECTORY);

//        if (!logs_dir.exists()) {
//            logs_dir.mkdirs();
//        }
//
        File file = new File(logs_dir, LOG_FILENAME);
//        if (!file.exists()){
//
//        }

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        scrollToBottom();
        ((TextView) findViewById(R.id.tvOutput)).setText(sb.toString());
    }


    // adding thread to scroll down after content changed
    private void scrollToBottom() {
        final TextView mTextStatus = (TextView) findViewById(R.id.tvOutput);
        final ScrollView mScrollView = (ScrollView) findViewById(R.id.svOutput);

        mScrollView.post(new Runnable()
        {
            public void run()
            {
                mScrollView.smoothScrollTo(0, mTextStatus.getBottom());
            }
        });
    }
}
