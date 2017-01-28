package net.devopskb.smsback;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import net.devopskb.smsback.core.RulesService;
import net.devopskb.smsback.logger.Log;
import net.devopskb.smsback.logger.Logger;
import net.devopskb.smsback.logger.LoggerFactory;
import net.devopskb.smsback.rules.Rule;
import net.devopskb.smsback.threads.SmsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

//import android.util.Log;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import ch.qos.logback.classic.LoggerContext;

//import ch.qos.logback.core.util.StatusPrinter;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_FILENAME = "log.txt";
    public static final String LOG_DIRECTORY= "smsback";
    public static final String[] MANAGERS = {"chenchuk@gmail.com", "chen.alkabets@nova-lumos.com"};


    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    // logger.debug("Some log message. Details: {}", someObject.toString());

    private RulesArrayAdapter<String> adapter;
    // map of rules by priority
    public Map<Integer, Rule> rules;
    public RulesService service;

    public void init() {
        Log.e(this.getClass().getSimpleName(), "++++++++++++++++++++++++++++++++++++++++++++++++++.");
        new SmsListener();
        Log.e(this.getClass().getSimpleName(), "++++++++++++++++++++++++++++++++++++++++++++++++++.");
        service = RulesService.getInstance();
        service.setContext(getApplicationContext());
        if (!service.isEnabled()){
            Log.e(this.getClass().getSimpleName(), "init() called when service not enabled");
            service.createShredPrefs();
            service.printSharedPrefs();
            service.buildRulesMapFromSharedPrefs();
            service.startService();
            Log.e(this.getClass().getSimpleName(), "service started.");
        }else {
            Log.e(this.getClass().getSimpleName(), "init() called when service is running, noop.");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshRulesListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.e(this.getClass().getSimpleName(), "onCreate() called");
        setContentView(R.layout.activity_main);

        init();
        refreshRulesListView();
        // disable keyboard on startapp
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Button btn_status = (Button) findViewById(R.id.btnStatus);
        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(statusIntent);
            }
        });

//        Button btn_delete = (Button) findViewById(R.id.btnDelete);
//        btn_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Logger.deleteLogfile();
//                Toast.makeText(MainActivity.this, "Logfile deleted.",Toast.LENGTH_SHORT).show();
//                Log.e(this.getClass().getSimpleName(), "logfile deleted.");
//            }
//        });

        Button btn_log = (Button) findViewById(R.id.btnLog);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogMonitor = new Intent(MainActivity.this, LogMonitorActivity.class);
                startActivity(intentLogMonitor);
            }
        });

//        Button btn_email = (Button) findViewById(R.id.btnSendEmail);
//        btn_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // log file attachment
//                File logs_dir = new File(Environment.getExternalStorageDirectory(), LOG_DIRECTORY);
//                File file = new File(logs_dir, LOG_FILENAME);
//                ArrayList<Uri> uris = new ArrayList<Uri>();
//                uris.add(Uri.fromFile(file));
//
//                final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//                emailIntent.setType("plain/text");
//                //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"chenchuk@gmaqil.com@somewhere.nodomain"});
//                emailIntent.putExtra(Intent.EXTRA_EMAIL, MANAGERS);
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "That one works");
//                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//                startActivityForResult(Intent.createChooser(emailIntent, "Sending multiple attachment"), 12345);
//            }
//        });

        Button btn_console = (Button) findViewById(R.id.btnConsole);
        btn_console.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCmdConsole = new Intent(MainActivity.this, CommandConsoleActivity.class);
                startActivity(intentCmdConsole);
            }
        });

    }

    public void refreshRulesListView(){
        // Arrray adapter must take String[]
        Rule[] rulesArray = service.getRules().values().toArray(new Rule[0]);
        String[] deployedRulesAsStrings = new String[rulesArray.length];
        for(int i=0 ; i<rulesArray.length ; i++){
            deployedRulesAsStrings[i] = rulesArray[i].toString();
        }

        adapter = new RulesArrayAdapter<String>(this,deployedRulesAsStrings);
        // attach adapter to listview
        final ListView lvRules = (ListView) findViewById(R.id.lvRules);
        lvRules.setAdapter(adapter);
        lvRules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ruleEditorIntent = new Intent(MainActivity.this, RuleEditorActivity.class);
                String jsonRule = (String) lvRules.getItemAtPosition(position);
                Rule selectedRule = new Rule(jsonRule);
                ruleEditorIntent.putExtra("SELECTED_JSON_RULE", jsonRule);
                startActivity(ruleEditorIntent);
            }
        });
        //Toast.makeText(MainActivity.this, "Rules refreshed.",Toast.LENGTH_SHORT).show();

    }
}
/*
*
* 01-16 15:27:34.591 8649-8649/com.example.chenchuk.mysms D/_id: 38
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/thread_id: 1
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/address: 5554
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/person: null
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/date: 1452957384713
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/date_sent: 0
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/protocol: null
01-16 15:27:34.592 8649-8649/com.example.chenchuk.mysms D/read: 1
01-16 15:27:34.593 8649-8649/com.example.chenchuk.mysms D/status: -1
01-16 15:27:34.593 8649-8649/com.example.chenchuk.mysms D/type: 2
01-16 15:27:34.593 8649-8649/com.example.chenchuk.mysms D/reply_path_present: null
01-16 15:27:34.593 8649-8649/com.example.chenchuk.mysms D/subject: null
01-16 15:27:34.593 8649-8649/com.example.chenchuk.mysms D/body: message heghfffre...
01-16 15:27:34.593 8649-8649/com.example.chenchuk.mysms D/service_center: null
01-16 15:27:34.594 8649-8649/com.example.chenchuk.mysms D/locked: 0
01-16 15:27:34.594 8649-8649/com.example.chenchuk.mysms D/sub_id: 1
01-16 15:27:34.594 8649-8649/com.example.chenchuk.mysms D/error_code: 0
01-16 15:27:34.594 8649-8649/com.example.chenchuk.mysms D/creator: com.example.chenchuk.mysms
01-16 15:27:34.594 8649-8649/com.example.chenchuk.mysms D/seen: 1
01-16 15:27:34.594 8649-8649/com.example.chenchuk.mysms D/One row finished: **************************************************

*
*
* */
