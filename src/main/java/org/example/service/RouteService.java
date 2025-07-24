package org.example.service;

import org.example.dto.route.RouteCreateDto;
import org.example.exception.NotFoundException;
import org.example.model.Route;
import org.example.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public Route createRoute(RouteCreateDto dto) {
        Route route = new Route();
        route.setDeparturePoint(dto.getDeparturePoint());
        route.setDestinationPoint(dto.getDestinationPoint());
        route.setCarrierId(dto.getCarrierId());
        route.setDurationMinutes(dto.getDurationMinutes());

        routeRepository.save(route);
        return route;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route updateRoute(Long id, RouteCreateDto dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        route.setDeparturePoint(dto.getDeparturePoint());
        route.setDestinationPoint(dto.getDestinationPoint());
        route.setCarrierId(dto.getCarrierId());
        route.setDurationMinutes(dto.getDurationMinutes());

        routeRepository.save(route);
        return route;
    }

    public void deleteRoute(Long id) {
        if (!routeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Route not found");
        }
        routeRepository.delete(id);
    }
}