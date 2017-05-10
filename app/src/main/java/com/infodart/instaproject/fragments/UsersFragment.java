package com.infodart.instaproject.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.infodart.instaproject.R;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.fragments.dummy.DummyContent;
import com.infodart.instaproject.fragments.dummy.DummyContent.DummyItem;
import com.infodart.instaproject.model.Users;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.VolleySingleton;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UsersFragment extends Fragment {
    private EndlessRecyclerViewScrollListener scrollListener;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    int pageNum = 1;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsersFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UsersFragment newInstance(int columnCount) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }
    SuperRecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        // Set the adapter
        if (view instanceof SuperRecyclerView) {
            Context context = view.getContext();
            recyclerView = (SuperRecyclerView) view;
            if (mColumnCount <= 1) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        //loadNextDataFromApi(page);
                        pageNum++;
                        loadUsers(page);
                    }
                };
                // Adds the scroll listener to RecyclerView
               // recyclerView.addOnScrollListener(scrollListener);
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            loadUsers(1);
            recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadUsers(pageNum=1);
                }
            });
            /*recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    loadUsers(++pageNum);
                    Logger.e("Values-"+ overallItemsCount+"-"+itemsBeforeMore+"-"+maxLastVisiblePosition);

                }
            },10);*/
        }
        return view;
    }
    Toast mToast;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Users item);
    }

    private void loadUsers(int pageNum) {
        String url = String.format(Constants.GetServerURL() + Constants.URL_USERS +"/?page=%s",pageNum);
        Logger.d(url);
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String> headers = new HashMap<>();
        //progressDialog = new ProgressDialog(getActivity());
        //progressDialog.show();
        Request request = VolleySingleton.getInstance(getActivity()).getStringRequest(Request.Method.GET,
                url,userListener,errorListener,headers,params);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }
    UsersRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Users> users = new ArrayList<>();
    Response.Listener<String> userListener= new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            ArrayList<Users> newUsers = new ArrayList<>();
            if(pageNum==1) {
                users = Users.ParseJSONArray(response);
                recyclerViewAdapter = new UsersRecyclerViewAdapter(users, mListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
            else{
                users.addAll(newUsers);
                recyclerViewAdapter.notifyDataSetChanged();
            }


        }
    };
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Logger.e("Could not load user");
        }
    };
}
