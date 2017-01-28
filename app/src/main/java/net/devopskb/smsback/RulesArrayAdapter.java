package net.devopskb.smsback;

import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.devopskb.smsback.core.RulesService;
import net.devopskb.smsback.rules.Rule;
import net.devopskb.smsback.rules.RuleBuilder;

/**
 * Created by chenchuk on 2/4/2016.
 */
public class RulesArrayAdapter<T> extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public RulesArrayAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rule_list_row, parent, false);
        TextView tvUp = (TextView) rowView.findViewById(R.id.tvUp);
        TextView tvDown = (TextView) rowView.findViewById(R.id.tvDown);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        // stam test bgcolor of even/odds
        // rowView.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#eefebc") : Color.parseColor("#effce3"));
        // testing tvUp

        // get an object from the row
        Rule rule = new RuleBuilder().createFromJsonString(values[position]);

        //imageView.setImageResource(R.drawable.x);
        tvDown.setTextColor(Color.parseColor("#232343"));
        tvDown.setText("Rule Priority: " + rule.getPriority()); // TODO: + "Hitcount: ");

        tvUp.setText(rule.getRuleDescription());
        // change the icon for Windows and iPhone

        return rowView;
    }
}
