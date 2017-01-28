package net.devopskb.smsback.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
//import android.util.Log;

import com.google.gson.Gson;
//import android.util.Log;

import net.devopskb.smsback.logger.Log;
import net.devopskb.smsback.rules.Rule;
import net.devopskb.smsback.rules.RuleBuilder;
import net.devopskb.smsback.threads.SmsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *      Created by chenchuk on 1/22/2016.
 *
 *      This is the main service, its a singleton that has ref to a rules<Integer, Rule>
 *      it manages the sharedPref, saving/replacing rules and load them (deploy)
 *      once a list is deployed, its ready to serve by an SMS handler.
 *
 */

public class RulesService {

    private static int counterMatchedSms = 0;
    private static int counterUnMatchedSms = 0;
    private static int counterCmdSms= 0;
    private static int counterBinarySms= 0;

    private Boolean enabled;
    private SmsListener listener;

    // need to access shared-prefs
    private Context context;

    private static final int MAX_RULES = 99;
    public static final RulesService instance = new RulesService();

    // deployed rules
    private Map<Integer, Rule> rules;

    public Map<Integer, Rule> getRules() {return rules;}
    public void setRules(Map<Integer, Rule> rules) {this.rules = rules;}

    public int getCounterMatchedSms() {return counterMatchedSms;}
    public void increaseCounterMatchedSms(){counterMatchedSms++;}
    public int getCounterUnMatchedSms() {return counterUnMatchedSms;}
    public void increaseCounterUnMatchedSms(){counterUnMatchedSms++;}
    public int getCounterCmdSms() {return counterCmdSms;}
    public void increaseCounterCmdSms(){counterCmdSms++;}
    public int getCounterBinarySms() {return counterBinarySms;}
    public void increaseCounterBinarySms(){counterBinarySms++;}


    // creates SmsListener with that Map of rules
    //Log.e(getClass().getSimpleName(), "starting sms listener with : " + rules.size() + " deployed rules.");


    // should kill the listener !
    public void startService(){
        this.enabled = true;
        if(rules!=null) {
            this.listener = new SmsListener();
            this.listener.setRules(rules);
            this.listener.setContext(context);
            Log.e(getClass().getSimpleName(), "service status changed to enabled.");
        } else  {
            Log.e(getClass().getSimpleName(), "cannot start service, no rules..");
        }

    }
    public void stopService(){
        this.enabled = false;
        Log.e(getClass().getSimpleName(), "service status changed to disabled.");
    }
    public Boolean isEnabled(){
        return this.enabled;
    }

    //public List<Operator> getOperators() { return operators;}
    //public void setOperators(List<Operator> operators) {this.operators = operators;}

    // return this singleton service
    public static RulesService getInstance(){
        return instance;
    }

    // singleton, private c'tor
    private RulesService(){
        this.enabled = false;
        Log.e(getClass().getSimpleName(), "rules service initialized.");
        //operators = new ArrayList<Operator>();
    }

    public void setContext(Context applicationContext) {
        // a context will be set by the caller to access shared prefs
        this.context = applicationContext;
        Log.e(getClass().getSimpleName(), "context set for this singleton.");
    }

    public void printSharedPrefs(){
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Map<String,?> keys = prefs.getAll();
        Log.e(getClass().getSimpleName(),"printing sharedPrefs");

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.e(getClass().getSimpleName(), entry.getKey() + ": " + entry.getValue().toString());
        }
    }


    // tester : remove all sharedPref keys
    public void clearSharedPrefs(){
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().commit();
        Log.e(getClass().getSimpleName(), "sharedPref cleared.");
    }

    // tester : static creation of rules
    public void createShredPrefs(){
        Log.e(getClass().getSimpleName(), "createSp() called. setting initial sharedPref.");
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // init sharedPref only once
        if (prefs.contains("INITIALIZED")){
            Log.e(getClass().getSimpleName(), "shared pref already initialized ... exiting.");
            return;
        }
        Log.e(getClass().getSimpleName(), "shared pref initializing ...");
        editor.putBoolean("INITIALIZED", true);
        editor.commit();

        // create rules using a builder pattern
        Rule rule2 = new RuleBuilder()
                            .setPriority(2)
                            .matchAddress("+972545211197")
                            .matchContent("ABC")
                            .addTimestamp("+")
                            .replyTo("0545211197")
                            .create();

        Rule rule3 = new RuleBuilder()
                            .setPriority(3)
                            .matchAddress("+972545211197")
                            .matchContent("-")
                            .addTimestamp("+")
                            .replyTo("0545211197")
                            .create();

        Rule rule5 = new RuleBuilder()
                            .setPriority(5)
                            .matchContent("AAA")
                            .addTimestamp("+")
                            .replyTo("054521197")
                            .create();

        // add rules. after each addition the sharedPref will be scanned again
        // to deploy the new rules

        addRuleToSharedPrefs(rule2);
        addRuleToSharedPrefs(rule3);
        addRuleToSharedPrefs(rule5);
    }

    // load rule if exists
    public Rule getRule(Integer id){
        String ruleName = id.toString();
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        String ruleString = prefs.getString(ruleName, "");
        Gson gson = new Gson();
                String json = prefs.getString(ruleName, "");
        Rule rule = gson.fromJson(json, Rule.class);
        return rule;
    }

    public void execCommand (SimpleSms sms){
        String smsContent = sms.getContent();
        if (smsContent.toLowerCase().startsWith("cmd:")){
            String cmdString = smsContent.substring(4);
            // cmd:add:{........}
            // command to add/replace a rule
            if (cmdString.toLowerCase().startsWith("add:")){
                Log.e(this.getClass().getSimpleName(), "exec command to add/replace rule.");
                String jsonRule = cmdString.substring(4);
                // create a rule objbect from json sms command, and add to rules
                Rule rule = new Rule(jsonRule);
                addRuleToSharedPrefs(rule);
                SimpleSms addRuleReply = new SimpleSms();
                addRuleReply.setRecipientAddress(sms.getSenderAddress());
                addRuleReply.setContent("done. total rules: " + rules.size());
                sendSMS(addRuleReply);

            }

            // cmd:get:43
            // command to get rule 43
            if (cmdString.toLowerCase().startsWith("get:")){
                Integer ruleId = Integer.parseInt(cmdString.substring(4));
                Log.e(this.getClass().getSimpleName(), "exec command get rule id: "+ruleId + ".");
                SimpleSms ruleReply = new SimpleSms();
                ruleReply.setRecipientAddress(sms.getSenderAddress());
                if (RulesService.getInstance().getRules().containsKey(ruleId)){
                    Rule rule = rules.get(ruleId);
                    String jsonRule = (rules.get(ruleId)).toString();
                    ruleReply.setContent(jsonRule);
                }else{
                    ruleReply.setContent("rule with id " + ruleId + " doesnt exists!");
                }
                sendSMS(ruleReply);

            }

            // cmd:remove:43
            // command to remove rule 43
            if (cmdString.toLowerCase().startsWith("remove:")){
                Integer ruleId = Integer.parseInt(cmdString.substring(7));
                Log.e(this.getClass().getSimpleName(), "exec command to remove rule id: " + ruleId + ".");
                SimpleSms ruleRemoveReply = new SimpleSms();
                ruleRemoveReply.setRecipientAddress(sms.getSenderAddress());
                if (RulesService.getInstance().getRules().containsKey(ruleId)){
                    removeRuleFromSharedPrefs(ruleId);
                    ruleRemoveReply.setContent("rule id: " + ruleId + " removed.");
                } else {
                    ruleRemoveReply.setContent("rule id: " + ruleId + " doesnt exists!.");
                }
                sendSMS(ruleRemoveReply);
            }

            // cmd:get-deployed
            // command to get all id's of deployed rules
            if (cmdString.equalsIgnoreCase("get-deployed")){
                Log.e(this.getClass().getSimpleName(), "exec command get all id's of deployed rules.");
                String allIds = "{";
                for(Integer ruleId : rules.keySet()){
                    allIds += ruleId + ", ";
                }
                allIds+="}";
                SimpleSms rulesListReply = new SimpleSms();
                rulesListReply.setRecipientAddress(sms.getSenderAddress());
                rulesListReply.setContent("deployed rules: " + allIds);
                sendSMS(rulesListReply);
            }
            // cmd:clear
            // command to clear all deployed rules
            if (cmdString.equalsIgnoreCase("clear")){
                Log.e(this.getClass().getSimpleName(), "exec command clear, to remove all rules.");
                clearSharedPrefs();
                buildRulesMapFromSharedPrefs();

                SimpleSms replyCmdClear = new SimpleSms();
                replyCmdClear.setRecipientAddress(sms.getSenderAddress());
                replyCmdClear.setContent("rules cleared.");
                Log.e(this.getClass().getSimpleName(), "total rules in sharedPrefs: " + rules.size());
                sendSMS(replyCmdClear);
            }



        } else {
            Log.e(this.getClass().getSimpleName(), "unrecognized command: " + sms.getContent());
        }
    }

    // remove rule to sharedPref
    public void removeRuleFromSharedPrefs(Integer id) {
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(String.valueOf(id));
        editor.commit();
        Log.e(getClass().getSimpleName(), "rule id: " + id + " removed.");
        buildRulesMapFromSharedPrefs();
    }

    // add rule to sharedPref
    public void addRuleToSharedPrefs(Rule rule){
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(rule, Rule.class);
        editor.putString(String.valueOf(rule.getPriority()), json);
        editor.commit();
        Log.e(getClass().getSimpleName(), "adding rule id: " + rule.getPriority() + " : " + json);
        //Log.e(getClass().getSimpleName(), "about to refresh rules list");
        // also deploy the new list
        buildRulesMapFromSharedPrefs();
    }

    // deploying the new list
    public void buildRulesMapFromSharedPrefs(){
        rules = new TreeMap<Integer, Rule>();
        SharedPreferences prefs = context.getSharedPreferences("RULES", Context.MODE_PRIVATE);
        Rule rule;
        Log.e(getClass().getSimpleName(), "redeploy existing rules from shared prefs.");
        for(int i=1 ; i<MAX_RULES ; i++){
            String jsonRule = prefs.getString(String.valueOf(i), "");
            if (!jsonRule.isEmpty()){
                rule = getRule(i);
                rules.put(rule.getPriority(), rule);
                Log.e(getClass().getSimpleName(), "rule added + " + rule.toString());
            }
        }
        Log.e(getClass().getSimpleName(), "rules map deployed. total : " + rules.size() + " rules.");
    }
    public void sendSMS(SimpleSms simpleSms){

        // if source of the sms is "000" it means that the command arrived from console
        // there is no need to reply, logging is enough.
        if (simpleSms.getRecipientAddress().equals("000")){
            Log.e(this.getClass().getSimpleName(), "reply to console command : " + simpleSms.toString());
            return;
        }
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(simpleSms.getRecipientAddress(), null, simpleSms.getContent(), null, null);
        Log.e(this.getClass().getSimpleName(), "sms sent: " + simpleSms.toString());
    }


}

