package com.example.enskild_transportapi.Repository;

import com.example.enskild_transportapi.Model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    //SELECT * FROM route where  isFavoured=true  ;
    List<Route> findRouteByIsFavoured(Boolean isFavoured);

    //SELECT * FROM route where  isFavoured=true and transport_mode = ;
    List<Route> findRouteByIsFavouredAndTransportMode(Boolean isFavoured,String transportmode);


    //SELECT * FROM route where origin like '%%' and destination like '%%';
    List<Route> findAllRoutesByOriginContainsAndDestinationContains(String Origin,String Destination);



}
