package com.example.mhbadmin.Filters;

import android.widget.Filter;

import com.example.mhbadmin.AdapterClasses.RequestBookingHistoryAdapter;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Classes.Models.FilterLists;

import java.util.ArrayList;
import java.util.List;

public class RequestBookingFilter extends Filter {

    private List<FilterLists> filterLists = null;

    private RequestBookingHistoryAdapter requestBookingHistoryAdapter = null;

    public RequestBookingFilter(List<FilterLists> filterLists, RequestBookingHistoryAdapter requestBookingHistoryAdapter) {
        this.filterLists = filterLists;
        this.requestBookingHistoryAdapter = requestBookingHistoryAdapter;
    }

    //Filtering Occurs
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        FilterLists filterLists1=null;
        CRequestBookingData cRequestBookingData=null;
        CUserData cUserData=null;

        //CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toLowerCase();

            //STORE OUR FILTERED PLAYERS
            ArrayList<FilterLists> filteredPlayers = new ArrayList<>();
            for (int i = 0; i < filterLists.size(); i++) {

                filterLists1=filterLists.get(i);

                cRequestBookingData=filterLists1.getcRequestBookingDataList();
                cUserData=filterLists1.getcUserDataList();

                //CHECK
                if (cUserData.getsCity().toLowerCase().contains(constraint) ||
                        cUserData.getsLocation().toLowerCase().contains(constraint) ||
                        cUserData.getsUserFirstName().toLowerCase().contains(constraint) ||
                        cUserData.getsUserLastName().toLowerCase().contains(constraint) ||
                        cRequestBookingData.getsFunctionDate().contains(constraint) ||
                        cRequestBookingData.getsFunctionTiming().toLowerCase().contains(constraint) ||
                        cRequestBookingData.getsEstimatedBudget().contains(constraint) ||
                        cRequestBookingData.getsNoOfGuests().contains(constraint) ||
                        cRequestBookingData.getsRequestTime().contains(constraint) ||
                        cRequestBookingData.getsOtherDetail().toLowerCase().contains(constraint)) {
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
        requestBookingHistoryAdapter.filterLists = (List<FilterLists>) results.values;
        //REFRESH
        requestBookingHistoryAdapter.notifyDataSetChanged();
    }
}