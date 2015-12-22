package com.cabanasrd.ui.google.maps;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author zeeshan0026 and Fernando Valle
 * http://stackoverflow.com/users/1547539/zeeshan0026
 * http://stackoverflow.com/users/1826257/fernando-valle
 */



public class Route {
    GoogleMap mMap;
    Context context;
    String lang;
    ArrayList<Polyline> polylines = null;

    public Route() {
        polylines = new ArrayList<Polyline>();
    }


    public boolean drawRoute(GoogleMap map, Context c, ArrayList<LatLng> points, String language, boolean optimize)
    {
        mMap = map;
        context = c;
        lang = language;
        if(points.size() == 2)
        {
            String url = makeURL(points.get(0).latitude,points.get(0).longitude,points.get(1).latitude,points.get(1).longitude,"driving");
            new connectAsyncTask(url,false).execute();
            return true;
        }
        else if(points.size() > 2)
        {
            String url = makeURL(points,"driving",optimize);
            new connectAsyncTask(url,false).execute();
            return true;
        }

        return false;

    }

    private String makeURL (ArrayList<LatLng> points, String mode, boolean optimize){
        StringBuilder urlString = new StringBuilder();

        if(mode == null)
            mode = "driving";

        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append( points.get(0).latitude);
        urlString.append(',');
        urlString.append(points.get(0).longitude);
        urlString.append("&destination=");
        urlString.append(points.get(points.size()-1).latitude);
        urlString.append(',');
        urlString.append(points.get(points.size()-1).longitude);

        urlString.append("&waypoints=");
        if(optimize)
            urlString.append("optimize:true|");
        urlString.append( points.get(1).latitude);
        urlString.append(',');
        urlString.append(points.get(1).longitude);

        for(int i=2;i<points.size()-1;i++)
        {
            urlString.append('|');
            urlString.append( points.get(i).latitude);
            urlString.append(',');
            urlString.append(points.get(i).longitude);
        }


        urlString.append("&sensor=true&mode="+mode);


        return urlString.toString();
    }

    private String makeURL (double sourcelat, double sourcelog, double destlat, double destlog,String mode){
        StringBuilder urlString = new StringBuilder();

        if(mode == null)
            mode = "driving";

        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode="+mode+"&alternatives=true&language="+lang);
        return urlString.toString();
    }




    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }




    private class connectAsyncTask extends AsyncTask<Void, Void, String>{
        String url;

        connectAsyncTask(String urlPass, boolean withSteps){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {

            JSONParser jParser = new JSONParser();
            String json = "";
            try {
                json = jParser.getJSONFromUrl(url);
            } catch (Exception e) {
            }

            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result!=null){
                try {
                    drawPath(result);
                } catch (Exception e) {
                }

            }
        }
    }

    private void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            if(!polylines.isEmpty()){
                for(Polyline line :polylines){
                    line.remove();
                }
                polylines.clear();
            }
            for(int z = 0; z<list.size()-1;z++){

                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                        .width(4)
                        .color(Color.BLUE).geodesic(true));
                polylines.add(line);                            }





        }
        catch (JSONException e) {

        }
    }



}