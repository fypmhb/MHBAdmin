package com.example.mhbadmin.Filters;

import android.widget.Filter;

import com.example.mhbadmin.AdapterClasses.RequestBookingAdapter;
import com.example.mhbadmin.Classes.Models.FilterLists;

import java.util.ArrayList;
import java.util.List;

public class RequestBookingFilter extends Filter {

    private List<FilterLists> filterLists = null;

    private RequestBookingAdapter requestBookingAdapter = null;

    public RequestBookingFilter(List<FilterLists> filterLists, RequestBookingAdapter requestBookingAdapter) {
        this.filterLists = filterLists;
        this.requestBookingAdapter = requestBookingAdapter;
    }

    //Filtering Occurs
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toLowerCase();

            //STORE OUR FILTERED PLAYERS
            ArrayList<FilterLists> filteredPlayers = new ArrayList<>();
            for (int i = 0; i < filterLists.size(); i++) {
                //CHECK
                if (filterLists.get(i).getsCity().toLowerCase().contains(constraint) ||
                        filterLists.get(i).getsLocation().toLowerCase().contains(constraint) ||
                        filterLists.get(i).getsUserFirstName().toLowerCase().contains(constraint) ||
                        filterLists.get(i).getsUserLastName().toLowerCase().contains(constraint) ||
                        filterLists.get(i).getsFunctionDate().contains(constraint) ||
                        filterLists.get(i).getsFunctionTiming().toLowerCase().contains(constraint) ||
                        filterLists.get(i).getsEstimatedBudget().contains(constraint) ||
                        filterLists.get(i).getsNoOfGuests().contains(constraint) ||
                        filterLists.get(i).getsRequestTime().contains(constraint) ||
                        filterLists.get(i).getsOtherDetail().toLowerCase().contains(constraint)) {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterLists.get(i));
                }
            }
            results.count = filteredPlayers.size();
            results.values = filteredPlayers;
        } else {
            results.count = filterLists.size();
            results.values = filterLists;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        requestBookingAdapter.filterLists = (List<FilterLists>) results.values;
        //REFRESH
        requestBookingAdapter.notifyDataSetChanged();
    }
}