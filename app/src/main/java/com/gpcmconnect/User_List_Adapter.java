package com.gpcmconnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class User_List_Adapter extends BaseAdapter {

    Context context;
    ArrayList<String> names, emails, designations;

    public User_List_Adapter(Context context, ArrayList<String> names, ArrayList<String> emails, ArrayList<String> designations) {
        this.context = context;
        this.names = names;
        this.emails = emails;
        this.designations = designations;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = layoutInflater.inflate(R.layout.users_view, null);

        TextView nameView = view.findViewById(R.id.name);
        TextView emailView = view.findViewById(R.id.email);
        TextView designationView = view.findViewById(R.id.designation);

        nameView.setText(names.get(position));
        emailView.setText(emails.get(position));
        designationView.setText(designations.get(position));

        return view;
    }
}
