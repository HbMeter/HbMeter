package hbmeter.hbmeter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 24/07/2017.
 */

public class CommonBaseAdapter extends BaseAdapter {
    ArrayList<String> labeldata_array = new ArrayList<String>();
    ArrayList<String> labelorario_array = new ArrayList<String>();
    ArrayList<String> labelastar_array = new ArrayList<String>();
    ArrayList<String> labelhb_array = new ArrayList<String>();
    ArrayList<String> labelhblab_array = new ArrayList<String>();
    ArrayList<String> labelnumero_array = new ArrayList<String>();



    Context context;
    LayoutInflater inflater;


    public CommonBaseAdapter(Context c, ArrayList<String> label_data, ArrayList<String> label_orario, ArrayList<String> label_astar, ArrayList<String> label_hb, ArrayList<String> label_hblab, ArrayList<String> label_numero)
    {
        context = c;
        labeldata_array = label_data;
        labelorario_array = label_orario;
        labelastar_array = label_astar;
        labelhb_array = label_hb;
        labelhblab_array = label_hblab;
        labelnumero_array = label_numero;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return labeldata_array.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        Holder holder;
        // TODO Auto-generated method stub
        if (v == null) {
            v = inflater.inflate(R.layout.row, null);
            holder = new Holder();
            holder.data = (TextView)v.findViewById(R.id.data);
            holder.orario = (TextView) v.findViewById(R.id.orario);
            holder.astar = (TextView) v.findViewById(R.id.astar);
            holder.hb = (TextView) v.findViewById(R.id.hb);
            holder.hblab = (TextView) v.findViewById(R.id.hblab);
            holder.numero = (TextView) v.findViewById(R.id.numero);


            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }


        holder.data.setText(labeldata_array.get(position));
        holder.orario.setText(labelorario_array.get(position));
        holder.astar.setText(labelastar_array.get(position));
        holder.hb.setText(labelhb_array.get(position));
        holder.hblab.setText(labelhblab_array.get(position));
        holder.numero.setText(labelnumero_array.get(position));


        return v;
    }


    public class Holder
    {
        TextView data, orario, astar, hb, hblab, numero;
    }
}
