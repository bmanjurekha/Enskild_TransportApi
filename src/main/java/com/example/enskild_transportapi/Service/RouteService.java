package com.example.enskild_transportapi.Service;

import java.util.List;


import com.example.enskild_transportapi.Model.Route;
import com.example.enskild_transportapi.Repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> FavouredRoutes(Boolean isFavoured){
        return  routeRepository.findRouteByIsFavoured(isFavoured);
    }
    public List<Route> RoutesOriginDest(String origin,String destination)
    {
        return routeRepository.findAllRoutesByOriginContainsAndDestinationContains(origin,destination);
    }
    public Route save(Route route) {
        return routeRepository.save(route);
    }
}
