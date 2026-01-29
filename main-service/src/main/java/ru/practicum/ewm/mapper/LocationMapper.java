package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.model.Location;

@Component
public class LocationMapper {
    public static LocationDto locationToLocationDto(Location location) {
        if (location == null) return null;
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location locationDtoToLocation(LocationDto locationDto) {
        if (locationDto == null) return null;
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
