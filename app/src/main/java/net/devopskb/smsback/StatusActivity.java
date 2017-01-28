package net.devopskb.smsback;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.devopskb.smsback.core.RulesService;
import net.devopskb.smsback.logger.Log;
import net.devopskb.smsback.logger.Logger;

import java.io.File;
import java.util.ArrayList;

public class StatusActivity extends AppCompatActivity {

    public static final String LOG_FILENAME = "log.txt";
    public static final String LOG_DIRECTORY= "smsback";
    public static final String[] MANAGERS = {"chenchuk@gmail.com", "chen.alkabets@nova-lumos.com"};

    RulesService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        service = RulesService.getInstance();
        updateDisplay();

        ImageButton ibStatus = (ImageButton)findViewById(R.id.ibStatus);
        ibStatus.setOnClickListener(new View.OnClickListener() {
            @Override

            // flip status
            public void onClick(View v) {
                if (service.isEnabled()) {
                    Log.e(this.getClass().getSimpleName(), "going to stop service...");
                    service.stopService();
                    updateDisplay();
                } else {
                    Log.e(this.getClass().getSimpleName(), "going to start service...");
                    service.startService();
                    updateDisplay();
                }
            }
        });

        Button btn_delete = (Button) findViewById(R.id.btnDelete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.deleteLogfile();
                Toast.makeText(StatusActivity.this, "Logfile deleted.", Toast.LENGTH_SHORT).show();
                Log.e(this.getClass().getSimpleName(), "logfile deleted.");
            }
        });

        Button btn_email = (Button) findViewById(R.id.btnSendEmail);
        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // log file attachment
                File logs_dir = new File(Environment.getExternalStorageDirectory(), LOG_DIRECTORY);
                File file = new File(logs_dir, LOG_FILENAME);
                ArrayList<Uri> uris = new ArrayList<Uri>();
                uris.add(Uri.fromFile(file));

                final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailIntent.setType("plain/text");
                //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"chenchuk@gmaqil.com@somewhere.nodomain"});
                emailIntent.putExtra(Intent.EXTRA_EMAIL, MANAGERS);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Logfile from SmsBack app");
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivityForResult(Intent.createChooser(emailIntent, "Sending multiple attachment"), 12345);
            }
        });

//        Button btn_start = (Button)findViewById(R.id.btnStart);
//        btn_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RulesService.getInstance().enableService();
//                updateDisplay();
//            }
//        });
//
//        Button btn_stop = (Button)findViewById(R.id.btnStop);
//        btn_stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RulesService.getInstance().disableService();
//                updateDisplay();
//            }
//        });
    }

    private void updateDisplay() {
        TextView tvMatchedSms = (TextView) findViewById(R.id.tvMatchedSms);
        TextView tvUnMatchedSms = (TextView) findViewById(R.id.tvUnMatchedSms);
        TextView tvCmdSms = (TextView) findViewById(R.id.tvCmdSms);
        TextView tvBinarySms = (TextView) findViewById(R.id.tvBinarySms);
        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
        ImageButton ibStatus = (ImageButton)findViewById(R.id.ibStatus);

        tvMatchedSms.setText(String.valueOf(service.getCounterMatchedSms()));
        tvUnMatchedSms.setText(String.valueOf(service.getCounterUnMatchedSms()));
        tvCmdSms.setText(String.valueOf(service.getCounterCmdSms()));
        tvBinarySms.setText(String.valueOf(service.getCounterBinarySms()));

        if (service.isEnabled()){
            ibStatus.setImageResource(R.drawable.blue_icon_service_up);
            tvStatus.setText("Service is running... (press to stop)");
        } else {
            ibStatus.setImageResource(R.drawable.red_icon_service_down);
            tvStatus.setText("Service stopped. (press to start)");

        }

    }
}
