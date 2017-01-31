package net.devopskb.smsback;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.devopskb.smsback.core.RulesService;
import net.devopskb.smsback.core.SimpleSms;
import net.devopskb.smsback.logger.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandConsoleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_console);



        // TODO: add to shared pref
        // TODO: improve shared pref for all vars
        // TODO: fix graphics
        // TODO: check command reply to 000 (local console)
        final Map<String, String> commands = new HashMap<>();
        commands.put("add or replace rule 2", "cmd:add:{\"priority\":2,\"addTimestamp\":\"+\",\"matchAddress\":\"1111\",\"matchContent\":\"-\",\"replyTo\":\"2222\",\"replaceContent\":\"-\"}");
        commands.put("list deployed rules", "cmd:get-deployed");
        commands.put("get rule 2", "cmd:get:2");
        commands.put("remove rule 3", "cmd:remove:3");
        commands.put("clear all !!!", "cmd:clear");
        String[] cmds = (commands.keySet()).toArray(new String[0]);

        // bind the listview with array of commands descriptions
        ListView lvCommands = (ListView) findViewById(R.id.lvCommands);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cmds);
        lvCommands.setAdapter(adapter);

        lvCommands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                String cmdValue = tv.getText().toString();
                EditText etCommand = (EditText) findViewById(R.id.etCommand);
                etCommand.setText(commands.get(cmdValue));
            }
        });

        Button btn_exec = (Button) findViewById(R.id.btnExec);
        btn_exec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(this.getClass().getSimpleName(), "btnExec() pressed.");
                RulesService service = RulesService.getInstance();
                String command = ((EditText) findViewById(R.id.etCommand)).getText().toString();

                // faking an sms to pass for execution
                SimpleSms simulatedSms = new SimpleSms();
                simulatedSms.setContent(command);
                simulatedSms.setSenderAddress("000");

                Log.e(this.getClass().getSimpleName(), simulatedSms.toString());
                // BUG : service.execCommand(new SimpleSms().setContent(command));
                service.execCommand(simulatedSms);
                //refreshRulesListView();
                Toast.makeText(v.getContext(), "Done.", Toast.LENGTH_LONG).show();
                Log.e(this.getClass().getSimpleName(), "rules deployd: " + service.getRules().toString());

            }
        });


    }

}
