package com.rest.server.services;

import com.rest.server.exception.ResourceNotFoundException;
import com.rest.server.models.Location;
import com.rest.server.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Location> allLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }
    // In LocationService.java
    // In LocationService.java
    public List<Location> getAllLocations() {
        try {
            // Even if the repository returns null, we convert to empty list
            List<Location> locations = locationRepository.findAll();
            return locations != null ? locations : Collections.emptyList();
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error retrieving locations: " + e.getMessage());
            // Return empty list instead of null
            return Collections.emptyList();
        }
    }
    public Optional<Location> singleLocation(String id){
        return Optional.ofNullable(locationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + id)));
    }

    public Location createLocation(Location Location) {
        return locationRepository.save(Location);
    }

    public Location updateLocation(String id, Location updatedLocation) {
        return locationRepository.findById(id)
                .map(location -> {
                    location.setLocationStreet(updatedLocation.getLocationStreet());
                    location.setLocationCity(updatedLocation.getLocationCity());
                    location.setLocationCountry(updatedLocation.getLocationCountry());
                    location.setLocationState(updatedLocation.getLocationState());
                    location.setLocationTimezone(updatedLocation.getLocationTimezone());
                    return locationRepository.save(location);
                }).orElseThrow(() -> new RuntimeException("Location not found"));
    }
    public void deleteLocation(String id) {
        locationRepository.deleteById(id);
    }
}
