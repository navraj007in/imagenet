package com.infodart.instaproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.infodart.instaproject.R;
import com.infodart.instaproject.adapters.PostAdapter;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.interfaces.OnLoadMoreListener;
import com.infodart.instaproject.model.Post;
import com.infodart.instaproject.ui.BaseActivity;
import com.infodart.instaproject.ui.InstaHome;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.NPLayoutManager;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int pageNum  = 1;
    SwipeRefreshLayout swipeRefreshLayout;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FeedsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedsFragment newInstance(String param1, String param2) {
        FeedsFragment fragment = new FeedsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        initFragment(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void loadProfile(String _id) {
        String url = String.format(Constants.GetServerURL() + Constants.URL_USERS +
                Constants.URL_USER_PROFILE,_id);
        InstaHome.followersMap = new HashMap<>();

        Logger.v("URL-"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        Utils.WriteToFile(Constants.FILE_PROFILE,response,getActivity());
                        Logger.d("Got Response - "+response+ "-Wrote To File");

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonFollowing = new JSONArray(jsonObject.getString("following"));
                            for(int i=0;i<jsonFollowing.length();i++) {
                                InstaHome.followersMap.put(jsonFollowing.getString(i),1);
                            }
                            Utils.WriteObjectToFile("followers",InstaHome.followersMap,getActivity());
                            //String followers = jsonObject.getString("");
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = VolleySingleton.getInstance(getActivity().getApplicationContext()).
                getRequestQueue();

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        //queue.add(stringRequest);

    }
    String mUserId ;
    protected Handler handler;

    private void initFragment(View view) {
        mUserId = ((BaseActivity)(getActivity())).mUserId ;

        init(view);
        readSavedFeeds();
        loadProfile(mUserId);

        loadProfile(mUserId);
        adapter = new PostAdapter(getActivity(),posts,recyclerView,likesMap);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                posts.add(null);
                adapter.notifyItemInserted(posts.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   remove progress item
                        posts.remove(posts.size() - 1);
                        adapter.notifyItemRemoved(posts.size());
                        //add items one by one
                        int start = posts.size();
                        int end = start + 20;


                        adapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        if(followersMap == null)
            followersMap = (HashMap<String, Integer>) Utils.ReadObjectFromFile("followers",getActivity());

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    ArrayList<Post> posts ;
    PostAdapter adapter;
    RecyclerView recyclerView;
    private void readSavedFeeds() {
        String feeds = Utils.ReadFromFile(getActivity(), Constants.FILE_FEEDS);
        try {

            posts = Post.ParsePostList(feeds);

            adapter = new PostAdapter(getActivity(), posts,recyclerView,likesMap);
            LinearLayoutManager mLayoutManager = new NPLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            recyclerView.setAdapter(adapter);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadPosts() {
        long timestamp = System.currentTimeMillis();
        String params = "/?timestamp=%d&page=%d&userid=%s";
        String url = String.format(Constants.GetServerURL() + Constants.URL_POSTS+ params,timestamp,pageNum,mUserId);

        Logger.v("URL-"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        //Log.e("SexApp",response);
                        swipeRefreshLayout.setRefreshing(false);
                        try {

                            posts = Post.ParsePostList(response);
                            adapter = new PostAdapter(getActivity(), posts,recyclerView,likesMap);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setAdapter(adapter);

                            Utils.WriteToFile(Constants.FILE_FEEDS,response,getActivity());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        swipeRefreshLayout.setRefreshing(false);
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = VolleySingleton.getInstance(getActivity().getApplicationContext()).
                getRequestQueue();


        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        queue.add(stringRequest);
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        // mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        loadPosts();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts();
            }
        });

    }
    public static HashMap<String, Integer> likesMap;
    public static HashMap<String, Integer> followersMap;
    private void loadLikesMap() {

        SharedPreferences pref = getActivity().getSharedPreferences("likes", Context.MODE_PRIVATE);
        likesMap = (HashMap<String, Integer>) pref.getAll();
        for (String s : likesMap.keySet()) {
            Integer value=likesMap.get(s);
        }
    }


}
