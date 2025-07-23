package org.example.model;

import lombok.Data;

@Data
public class Route {
    private Integer id;
    private String departurePoint;
    private String destinationPoint;
    private Integer carrierId;
    private int durationMinutes;
}