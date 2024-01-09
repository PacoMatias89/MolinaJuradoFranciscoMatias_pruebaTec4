package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.HotelDto;
import com.hackaboss.agenciaturismo.dto.UpdateHotelDto;
import com.hackaboss.agenciaturismo.model.Hotel;

import java.time.LocalDate;
import java.util.List;

public interface IHotelService {

    Hotel createHotel(HotelDto hotelDto);

    List<Hotel> getAllHotelsByPlaceDate(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String place, boolean isBooked);

    List<Hotel> getAllHotels();

    void deleteHotelById(Long id);

    Hotel editHotelById(Long id, UpdateHotelDto hotelDto);

    Hotel getHotelById(Long id);


}
