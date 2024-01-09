package com.hackaboss.agenciaturismo.repositories;

import com.hackaboss.agenciaturismo.model.BookFlight;
import com.hackaboss.agenciaturismo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingFlightRepository extends JpaRepository<BookFlight, Long> {


    boolean existsByFlightCode(String flightNumber);
    @Query("SELECT bf FROM BookFlight bf " +
            "INNER JOIN bf.passengers p " +
            "WHERE p IN :users " +
            "AND bf.updated IS NULL " +
            "AND bf.date = :date")
    List<BookFlight> getUserActiveBookings(@Param("users") List<User> users,
                                           @Param("date") LocalDate date);




}
