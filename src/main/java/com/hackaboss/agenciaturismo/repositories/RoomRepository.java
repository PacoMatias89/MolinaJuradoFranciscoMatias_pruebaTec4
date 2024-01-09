package com.hackaboss.agenciaturismo.repositories;

import com.hackaboss.agenciaturismo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
