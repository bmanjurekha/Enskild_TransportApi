package com.example.enskild_transportapi.Controller;

import com.example.enskild_transportapi.Model.GoogleRoute;
import com.example.enskild_transportapi.Model.OpenWeather;
import com.example.enskild_transportapi.Model.Route;
import com.example.enskild_transportapi.Model.Step;
import com.example.enskild_transportapi.Service.RouteService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/route/*")

public class RouteController {
    @Autowired
    private RouteService routeService;
    @GetMapping("getroute/{origin}/{destination}/{transportmode}")
    public ResponseEntity<List<Route>> getRoute(@PathVariable String origin, @PathVariable String destination, @PathVariable String transportmode) throws IOException {
        double destLat = 0;
        double destLng = 0;
        String weather;
        // Get Route Details from Google
        GoogleRoute.Root gdata = getGoogleRoute(origin, destination, transportmode);

        //Get Destination Lat & Lon for Weather
        if(gdata!=null) {
            if (gdata.status.equals("OK")) {
                if (!(gdata.routes.isEmpty())) {
                    if (!(gdata.routes.get(0).legs.isEmpty())) {
                        destLat = gdata.routes.get(0).legs.get(0).end_location.lat;
                        destLng = gdata.routes.get(0).legs.get(0).end_location.lng;
                    }
                }
            }
        }
        //Get Weather Info
        weather = getWeather(destLat,destLng);

        //Map Google Route & Weather details to Route
        List<Route> lRoute= getRoute(gdata,weather,transportmode,origin,destination);
        for(Route r : lRoute){
            routeService.save(r);
        }
        List<Route> lstRoute = routeService.RoutesOriginDest(origin,destination);
        return ResponseEntity.status(201).body(lstRoute);
    }

    public GoogleRoute.Root getGoogleRoute(String origin,String destination,String transportmode) throws IOException {
        String gapi_key = "AIzaSyBIPxVXETgCnzPpbmsRMHYWiOeYEuN0Bdw";
        String gUrl = "https://maps.googleapis.com/maps/api/directions/json?destination=" +
                URLEncoder.encode(destination, StandardCharsets.UTF_8.toString()) +
                "&origin=" +
                URLEncoder.encode(origin, StandardCharsets.UTF_8.toString()) +
                "&mode=" +
                URLEncoder.encode(transportmode, StandardCharsets.UTF_8.toString()) +
                "&key=" +
                URLEncoder.encode(gapi_key, StandardCharsets.UTF_8.toString());
        if(transportmode.toUpperCase().equals("DRIVING"))
            gUrl=gUrl+"&traffic_model=best_guess&departure_time=now&alternatives=true";

        okhttp3.OkHttpClient gclient = new okhttp3.OkHttpClient().newBuilder()
                .build();

        okhttp3.Request grequest = new okhttp3.Request.Builder()
                .url(gUrl)
                .method("GET", null)
                .build();

        okhttp3.Call gcall = gclient.newCall(grequest);
        try (okhttp3.Response gresponse = gcall.execute()) {
            if (gresponse.isSuccessful()) {
                Gson ggson = new Gson();
                GoogleRoute.Root gdata = ggson.fromJson(gresponse.body().string(), GoogleRoute.Root.class);

                if (gdata.status.equals("OK")) {
                    return gdata;
                }
            }
        }
        return null;
    }

    public String getWeather(double lat,double lan) throws IOException {

        String wapi_key = "0f6c1de1ef8510d3a48b470d96c6bfa6";
        String wUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                lat +
                "&lon=" +
                lan +
                "&appid=" +
                URLEncoder.encode(wapi_key, StandardCharsets.UTF_8.toString());

        okhttp3.OkHttpClient wclient = new okhttp3.OkHttpClient().newBuilder()
                .build();

        okhttp3.Request wrequest = new okhttp3.Request.Builder()
                .url(wUrl)
                .method("GET", null)
                .build();

        okhttp3.Call wcall = wclient.newCall(wrequest);
        try (okhttp3.Response wresponse = wcall.execute()) {
            if (wresponse.isSuccessful()) {
                Gson wgson = new Gson();
                OpenWeather.Root wdata = wgson.fromJson(wresponse.body().string(), OpenWeather.Root.class);
                return wdata.weather.get(0).main + " - " + wdata.weather.get(0).description;
            }
        }
        return "";
    }

    public List<Route> getRoute(GoogleRoute.Root gdata, String weather,String transportmode,String origin,String destination) {
        List<Route> lstRoute = new ArrayList<>();
        if (gdata != null) {
            if (gdata.status.equals("OK")) {
                if (!(gdata.routes.isEmpty())) {

                    for (GoogleRoute.Route route : gdata.routes ) {
                        Route r = new Route();
                        r.setOrigin(origin);
                        r.setDestination(destination);
                        if(!(transportmode.toUpperCase().equals("DRIVING"))) {
                            List<Step> lstep = new ArrayList<>();
                            for (GoogleRoute.Step step : route.legs.get(0).steps) {
                                Step st = new Step();
                                st.setRoute(r);
                                st.setDescription(step.html_instructions);
                                st.setDistance(step.distance.text);
                                lstep.add(st);
                            }
                            r.setSteps(lstep);
                        }
                        r.setEstimatedTime(route.legs.get(0).duration.text);
                        r.setWeather(weather);
                        if(route.legs.get(0).duration_in_traffic!=null)
                            r.setTrafficDelay(route.legs.get(0).duration_in_traffic.text);
                        r.setTransportMode(transportmode);
                        lstRoute.add(r);

                    }

                    return lstRoute;
                }
            }
        }
        return  null;
    }

    @GetMapping("getFavouredRoutes/{transportmode}")
    public ResponseEntity<List<Route>>getFavouredRoutes(@PathVariable String transportmode)
    {
        List<Route> routes = routeService.FavouredRoutesByTransportmode(true,transportmode);
        return ResponseEntity.status(201).body(routes);
    }
    @PostMapping("isFavouredroute/{id}")
    public ResponseEntity<Route> isFavouredroute(@PathVariable long id) {
       Route route = routeService.get(id);
       if(route!=null)
       {
           route.setIsFavoured(true);
           routeService.save(route);
           return ResponseEntity.ok(routeService.get(id));
       }
       else
           return ResponseEntity
                   .status(204)
                   .header("x-information", "Route did not exist")
                   .body(new Route());
    }

    @DeleteMapping("deleteFavouredroute/{id}")
    public ResponseEntity<Route> deleteFavouredroute(@PathVariable long id) {

        Route route = routeService.get(id);
        if(route!=null)
        {
            route.setIsFavoured(false);
            routeService.save(route);
            return ResponseEntity.ok(routeService.get(id));
        }
        else
            return ResponseEntity
                    .status(204)
                    .header("x-information", "Route did not exist")
                    .body(new Route());
    }
}



