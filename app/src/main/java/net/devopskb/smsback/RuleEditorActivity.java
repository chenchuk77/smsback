package net.devopskb.smsback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import net.devopskb.smsback.core.RulesService;
import net.devopskb.smsback.logger.Log;
import net.devopskb.smsback.rules.Rule;

public class RuleEditorActivity extends AppCompatActivity {

    private Rule rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_editor);

        // get the rule as JSON from MainActivity
        String jsonRule = getIntent().getExtras().getString("SELECTED_JSON_RULE");
        rule = new Rule(jsonRule);
        updatePrivateFields(rule);
        updateDisplay(rule);
        addListeners();

        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rule rule = readDisplay();
                RulesService.getInstance().addRuleToSharedPrefs(rule);
                updateDisplay(rule);
                Toast.makeText(RuleEditorActivity.this, "Rule " + rule.getPriority() + " saved.", Toast.LENGTH_LONG).show();
                Log.e(this.getClass().getSimpleName(), "rule saved from ui fields.");
            }
        });
        Button btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Integer rulePriority = Integer.parseInt(((EditText) findViewById(R.id.etPriority)).getText().toString());
                                RulesService.getInstance().removeRuleFromSharedPrefs(rulePriority);

                                // back to main activity after deleting a rule
                                Intent mainActivityIntent = new Intent(RuleEditorActivity.this, MainActivity.class);
                                startActivity(mainActivityIntent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("rule will be removed permanently. Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }


    // update UI with a given rule
    private void updateDisplay(Rule rule) {
        EditText etPriority = (EditText)findViewById(R.id.etPriority);
        EditText etSender = (EditText)findViewById(R.id.etSender);
        EditText etContent = (EditText)findViewById(R.id.etContent);
        EditText etReplyTo = (EditText)findViewById(R.id.etReplyTo);
        // converting '-' / '+' / '++' to radiobutton checked property
        //EditText etAddTimestamp = (EditText)findViewById(R.id.etAddTimestamp);
        RadioGroup rbgrpTimestamp = (RadioGroup) findViewById(R.id.rbgrpTimestamp);
        RadioButton rbNoTimestamp = (RadioButton) findViewById(R.id.rbNoTimestamp);
        RadioButton rbEpochTimestamp = (RadioButton) findViewById(R.id.rbEpochTimestamp);
        RadioButton rbDateTimestamp = (RadioButton) findViewById(R.id.rbDateTimestamp);
        EditText etReplaceContent = (EditText)findViewById(R.id.etReplaceContent);
        EditText etDescription = (EditText)findViewById(R.id.etDescription);

        etPriority.setText(String.valueOf(rule.getPriority()));
        etSender.setText(rule.getMatchAddress());
        etContent.setText(rule.getMatchContent());
        etReplyTo.setText(rule.getReplyTo());

        String timestamp = rule.getAddTimestamp();
        if (timestamp.equals("+")){
            rbEpochTimestamp.setChecked(true);
        } else if (timestamp.equals("++")){
            rbDateTimestamp.setChecked(true);
        } else {
            rbNoTimestamp.setChecked(true);
        }
        etReplaceContent.setText(rule.getReplaceContent());
        etDescription.setText(rule.getRuleDescription());
        Log.e(this.getClass().getSimpleName(), "display updated with rule from editors context.");
    }

    // setup listeners to all editable fields. upon
    private void addListeners() {
        EditText etSender = (EditText)findViewById(R.id.etSender);
        EditText etContent = (EditText)findViewById(R.id.etContent);
        EditText etReplyTo = (EditText)findViewById(R.id.etReplyTo);
        EditText etReplaceContent = (EditText)findViewById(R.id.etReplaceContent);
        RadioGroup rbgrpTimestamp = (RadioGroup) findViewById(R.id.rbgrpTimestamp);

        // add listeners are the same for all editText.
        // if leaving a field or change rb : the rule in context get changed
        // and also the display
        EditText[] editTexts = {etSender, etContent, etReplyTo, etReplaceContent};
        for (EditText editText : editTexts) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        rule = readDisplay();
                        updatePrivateFields(rule);
                        updateDisplay(rule);
                    }
                }
            });
        }
        // special listener for rb-group
        rbgrpTimestamp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rule = readDisplay();
                updatePrivateFields(rule);
                updateDisplay(rule);
            }
        });


        Log.e(this.getClass().getSimpleName(), "display updated with rule from editors context.");
    }

    // update private fields
    public void updatePrivateFields(Rule rule) {
        this.rule.setPriority(rule.getPriority());
        this.rule.setMatchAddress(rule.getMatchAddress());
        this.rule.setMatchContent(rule.getMatchContent());
        this.rule.setReplyTo(rule.getReplyTo());
        this.rule.setAddTimestamp(rule.getAddTimestamp());
        this.rule.setReplaceContent(rule.getReplaceContent());
        Log.e(this.getClass().getSimpleName(), "rule updated in editors context.");

    }

    // make rule from UI fields
    private Rule readDisplay(){
        Rule rule = new Rule();
        Integer rulePriority = Integer.parseInt(((EditText) findViewById(R.id.etPriority)).getText().toString());
        rule.setPriority(rulePriority);
        rule.setMatchAddress(((EditText) findViewById(R.id.etSender)).getText().toString());
        rule.setMatchContent(((EditText) findViewById(R.id.etContent)).getText().toString());
        rule.setReplyTo(((EditText) findViewById(R.id.etReplyTo)).getText().toString());
        // converting radio button to '-' / '+' / '++'
        String timestamp = "";
        if (((RadioButton) findViewById(R.id.rbEpochTimestamp)).isChecked()) {
            timestamp += "+";
        } else if (((RadioButton) findViewById(R.id.rbDateTimestamp)).isChecked()) {
            timestamp += "++";
        } else { // if (((RadioButton) findViewById(R.id.rbNoTimestamp)).isChecked())
            timestamp += "-";
        }
        rule.setAddTimestamp(timestamp);
        rule.setReplaceContent(((EditText) findViewById(R.id.etReplaceContent)).getText().toString());
        return rule;
    }
}
