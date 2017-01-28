package net.devopskb.smsback.threads;

import android.telephony.SmsManager;

import net.devopskb.smsback.core.RulesService;
import net.devopskb.smsback.core.SimpleSms;
import net.devopskb.smsback.logger.Log;
import net.devopskb.smsback.rules.Rule;

import java.util.Map;

// bypassing android logging
//
//import android.util.Log;

/**
 * Created by chenchuk on 1/15/2016.
 */
public class SmsHandler implements  Runnable{

    private SimpleSms simpleSms;
    private Map<Integer, Rule> rules;

    public SmsHandler(SimpleSms simpleSms, Map<Integer, Rule> rules){

        //Log.e(this.getClass().getSimpleName(), "rules is null ? " + (rules == null ? "Y" : "N"));

        Log.e(this.getClass().getSimpleName(), "sms handler created.");
        this.simpleSms = simpleSms;
        this.rules = rules;
        Log.e(this.getClass().getSimpleName(), rules.size() + " rules deployed.");
    }

    @Override
    public void run() {

        // looping all rules in sorted TreeMap to search for a match
        for (Integer i : rules.keySet()) {
            Rule rule = rules.get(i);
            Log.e(this.getClass().getSimpleName(), "checking sms agains rule priority:" + rule.getPriority());
            if (rule.isMatch(simpleSms)){
                Log.e(this.getClass().getSimpleName(), "rule match !");
                RulesService.getInstance().increaseCounterMatchedSms();
                // allow rule to generate reply
                SimpleSms simpleSmsReply = rule.createSmsReply(simpleSms);
                Log.e(this.getClass().getSimpleName(), "reply message created.");
                sendSMS(simpleSmsReply);
                Log.e(this.getClass().getSimpleName(), "sms sent, handler task finished.");
                return;
            }else {
                Log.e(this.getClass().getSimpleName(), "rule doesnt match, continue to next rule by priority.");
                RulesService.getInstance().increaseCounterUnMatchedSms();
            }
        }
        Log.e(this.getClass().getSimpleName(), "all " + rules.size() + " rules didnt match, nothing todo, handler done.");
    }

    public  void  sendSMS(SimpleSms simpleSms){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(simpleSms.getRecipientAddress(), null, simpleSms.getContent(), null, null);
        Log.e(this.getClass().getSimpleName(), "sms sent: " + simpleSms.toString());
    }
}
