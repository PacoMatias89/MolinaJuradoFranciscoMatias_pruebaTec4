package com.hackaboss.agenciaturismo.services;


import com.hackaboss.agenciaturismo.model.Room;
import com.hackaboss.agenciaturismo.repositories.HotelRepository;
import com.hackaboss.agenciaturismo.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }


}
