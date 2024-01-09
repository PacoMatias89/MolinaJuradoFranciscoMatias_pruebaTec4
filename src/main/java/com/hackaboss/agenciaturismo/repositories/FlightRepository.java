package com.hackaboss.agenciaturismo.repositories;


import com.hackaboss.agenciaturismo.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT DISTINCT fli" +
            " from  Flight fli" +
            " where fli.date between :date1 and  :date2 and fli.origin = :origin and fli.destination = :destination")
    List<Flight> getAllFlightDate(LocalDate date1, LocalDate date2, String origin, String destination);

    Flight findByFlightNumber(String flightCode);



}
