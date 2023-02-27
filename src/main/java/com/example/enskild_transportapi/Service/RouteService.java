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

    public List<Route> FavouredRoutesByTransportmode(Boolean isFavoured,String transport_mode)
    {
        return routeRepository.findRouteByIsFavouredAndTransportMode(isFavoured,transport_mode);
    }
    public List<Route> RoutesOriginDest(String origin,String destination)
    {
        return routeRepository.findAllRoutesByOriginContainsAndDestinationContains(origin,destination);
    }
    public Route save(Route route) {
        return routeRepository.save(route);
    }

    public Route get(long id) {
        return routeRepository.findById(id).get();
    }

    public void delete(long id) {
        routeRepository.deleteById(id);
    }
    public List<Route> getAllRoute() {
        return routeRepository.findAll();
    }
}
