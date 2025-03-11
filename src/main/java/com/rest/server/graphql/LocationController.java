package com.rest.server.graphql;

import com.rest.server.models.Location;
import com.rest.server.models.LocationInput;
import com.rest.server.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

//    @QueryMapping
//    public List<Location> getAllLocations() {
//        return locationService.getAllLocations();
//    }
    @QueryMapping
    public List<Location> locations() {
        System.out.println("Locations query called");
        List<Location> result = locationService.getAllLocations();
        System.out.println("Result from service: " + (result == null ? "NULL" : "List with " + result.size() + " items"));
        return result != null ? result : Collections.emptyList();
    }
    @MutationMapping
    public Location createLocation(@Argument("input") LocationInput input) {
        return locationService.createLocation(convertInputToLocation(input));
    }

    @MutationMapping
    public Location updateLocation(@Argument("id") String id, @Argument("input") LocationInput input) {
        return locationService.updateLocation(id, convertInputToLocation(input));
    }

    @MutationMapping
    public String deleteLocation(@Argument("id") String id) {
        locationService.deleteLocation(id);
        return id;
    }

    private Location convertInputToLocation(LocationInput input) {
        Location location = new Location();
        location.setLocationStreet(input.getStreet());
        location.setLocationCity(input.getCity());
        location.setLocationState(input.getState());
        location.setLocationCountry(input.getCountry());
        location.setLocationTimezone(input.getTimezone());
        return location;
    }
}