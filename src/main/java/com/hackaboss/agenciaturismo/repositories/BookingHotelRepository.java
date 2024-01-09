package com.hackaboss.agenciaturismo.repositories;

import com.hackaboss.agenciaturismo.model.BookHotel;
import com.hackaboss.agenciaturismo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingHotelRepository extends JpaRepository<BookHotel, Long> {
    @Query("SELECT bh FROM BookHotel bh " +
            "INNER JOIN bh.users u " +
            "WHERE (bh.dateFrom BETWEEN :dateFrom AND :dateTo OR bh.dateTo BETWEEN :dateFrom AND :dateTo) " +
            "AND u IN :users " +
            "AND bh.status = false")
    List<BookHotel> getUserAndDateRange(@Param("users") List<User> users,
                                        @Param("dateFrom") LocalDate dateFrom,
                                        @Param("dateTo") LocalDate dateTo);





}
