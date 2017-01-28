package net.devopskb.smsback.threads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

import net.devopskb.smsback.core.RulesService;
//import net.kukinet.smsback.core.SimpleSms;
import net.devopskb.smsback.core.SimpleSms;
import net.devopskb.smsback.logger.Log;
import net.devopskb.smsback.rules.Rule;

import java.util.Map;

//
// bypass android Log
 //import android.util.Log;

public class SmsListener extends BroadcastReceiver{

    public static final String BINARY_KEYWORD = "BINARY";
    private static int counter = 0;


    private Context context;
    private Map<Integer, Rule> rules;
    private SharedPreferences preferences;
    private boolean binarySms = false;

    public SmsListener(){
        // exception if not available
        Log.e(this.getClass().getSimpleName(), "SmsListener-" + ++counter + " created.");
    }

    public void setRules(Map<Integer, Rule> rules){
        this.rules = rules;
        Log.e(this.getClass().getSimpleName(),rules.size() + " rules deployed.");
    }
    public void setContext(Context context){
        Log.e(this.getClass().getSimpleName(), "setting context for this listener ??? maybe not necessery.");
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(this.getClass().getSimpleName(), "onReceive called - new incoming sms.");
        if (RulesService.getInstance().isEnabled()) {
            SmsMessage[] msgs = null;
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                if (bundle != null){
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage incomingSms = SmsMessage.createFromPdu((byte[]) pdus[0]);
                    // if binary 140 bytes
                    if (incomingSms.getDisplayMessageBody() == null && incomingSms.getUserData().length == 140) {
                        Log.e(this.getClass().getSimpleName(), "incoming binary sms.");
                        binarySms = true;
                        RulesService.getInstance().increaseCounterBinarySms();

                    } else if(incomingSms.getMessageBody().toLowerCase().startsWith("cmd:")){
                        // must make sure its not binary sms, before parsing content command sms
                        Log.e(this.getClass().getSimpleName(), "incoming sms command, sending for execution.");
                        RulesService.getInstance().increaseCounterCmdSms();
                        // converting command sms to simpleSms
                        SimpleSms simpleSms = new SimpleSms();;
                        simpleSms.setSenderAddress(incomingSms.getOriginatingAddress());
                        simpleSms.setContent(incomingSms.getMessageBody());
                        RulesService.getInstance().execCommand(simpleSms);
                        return;
                    }

                    // create structured simple sms and populate from incoming sms
                    SimpleSms simpleSms = new SimpleSms();;
                    simpleSms.setSenderAddress(incomingSms.getOriginatingAddress());
                    simpleSms.setTimestamp(incomingSms.getTimestampMillis());
                    if(binarySms){
                        simpleSms.setContent(BINARY_KEYWORD);
                    }else {
                        simpleSms.setContent(incomingSms.getMessageBody());
                    }
                    Log.e(this.getClass().getSimpleName(), (binarySms ? "binary ": "" ) + "sms received from: " + incomingSms.getOriginatingAddress());
                    Log.e(this.getClass().getSimpleName(), (binarySms ? "binary content received." : "content: " + incomingSms.getDisplayMessageBody()));
                    rules = RulesService.getInstance().getRules();
                    Log.e(this.getClass().getSimpleName(), "starting sms handler to apply policy of " + rules.size() + " deployed rules.");
                    // send simple sms to a handler thread to check against policy of rules
                    (new Thread(new SmsHandler(simpleSms, rules))).start();
                }
            }
        }else{
            Log.e(this.getClass().getSimpleName(), "service disabled, ignoring message.");
        }
    }
}