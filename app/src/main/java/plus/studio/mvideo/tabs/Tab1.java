package plus.studio.mvideo.tabs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import plus.studio.mvideo.R;
import plus.studio.mvideo.adapters.CustomListAdapter;
import plus.studio.mvideo.logging.L;
import plus.studio.mvideo.models.Movie;
import plus.studio.mvideo.utils.AppController;

/**
 * Created by yohananjr13 on 5/3/2015.
 */

public class Tab1 extends Fragment {

    // Movies json url
    private static final String url = "http://mvideo.herokuapp.com/movies";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);

        listView = (ListView) v.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), movieList);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest AppReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        L.d(response.toString());
                        hidePDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("name"));
                                movie.setThumbnailUrl(obj.getString("icon"));
                                movie.setRating(obj.getDouble("rating"));
                                movie.setDuration(obj.getString("duration"));
                                movie.setYear(obj.getInt("release"));
                                movieList.add(movie);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.d(error.toString() + "");
//                VolleyLog.d(L.TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });

        listView.setAdapter(adapter);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(AppReq);

        return v;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
