package com.hackaboss.agenciaturismo.repositories;

import com.hackaboss.agenciaturismo.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;



@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Hotel findByHotelCode(String hotelCode);


    @Query("SELECT  h " +
            "FROM Hotel h " +
            "JOIN h.room r " +
            "WHERE h.place = :place " +
            "  AND h.isBooked = :isBooked " +
            "  AND r.disponibilityDateFrom BETWEEN :disponibilityDateFrom AND :disponibilityDateTo")
    List<Hotel> getHotels(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String place, boolean isBooked);



}
