package net.hova_it.barared.brio.apis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.DLCorreosClientes;

import java.util.ArrayList;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public class CustomerEmailAdapter extends ArrayAdapter<DLCorreosClientes> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<DLCorreosClientes> items;
    private ArrayList<DLCorreosClientes> itemsAll;
    private ArrayList<DLCorreosClientes> suggestions;
    private int viewResourceId;
    
    public CustomerEmailAdapter (Context context, int viewResourceId, ArrayList<DLCorreosClientes> items) {
        super (context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<DLCorreosClientes>) items.clone ();
        this.suggestions = new ArrayList<DLCorreosClientes> ();
        this.viewResourceId = viewResourceId;
    }
    
    public View getView (int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext ().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate (viewResourceId, null);
        }
        DLCorreosClientes customer = items.get (position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById (R.id.customerNameLabel);
            if (customerNameLabel != null) {
                customerNameLabel.setText (customer.getEmail ());
            }
        }
        return v;
    }
    
    @Override
    public Filter getFilter () {
        return nameFilter;
    }
    
    Filter nameFilter = new Filter () {
        @Override
        public String convertResultToString (Object resultValue) {
            String str = ((DLCorreosClientes) (resultValue)).getEmail ();
            return str;
        }
        
        @Override
        protected FilterResults performFiltering (CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear ();
                for (DLCorreosClientes customer : itemsAll) {
                    if (customer.getEmail ().toLowerCase ().startsWith (constraint.toString ().toLowerCase ())) {
                        suggestions.add (customer);
                    }
                }
                FilterResults filterResults = new FilterResults ();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size ();
                return filterResults;
            } else {
                return new FilterResults ();
            }
        }
        
        @Override
        protected void publishResults (CharSequence constraint, FilterResults results) {
            ArrayList<DLCorreosClientes> filteredList = (ArrayList<DLCorreosClientes>) results.values;
            if (results != null && results.count > 0) {
                clear ();
                for (DLCorreosClientes c : filteredList) {
                    add (c);
                }
                notifyDataSetChanged ();
            }
        }
    };
    
}