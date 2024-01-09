package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.HotelDto;
import com.hackaboss.agenciaturismo.dto.UpdateHotelDto;
import com.hackaboss.agenciaturismo.model.BookHotel;
import com.hackaboss.agenciaturismo.model.Hotel;
import com.hackaboss.agenciaturismo.model.Room;
import com.hackaboss.agenciaturismo.repositories.HotelRepository;
import com.hackaboss.agenciaturismo.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class HotelService implements IHotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Hotel createHotel(HotelDto hotelDto){
        Hotel hotel = new Hotel();

        hotel.setName(hotelDto.getName());
        hotel.setPlace(hotelDto.getPlace());
        hotel.setBooked(false);
        hotel.setStatus(false);


        // Crea una nueva habitación y establece la relación con el hotel
      
        Room room = new Room();

        room.setRoomType(hotelDto.getRoomType());
        room.setRoomPrice(hotelDto.getRoomPrice());
        room.setDisponibilityDateFrom(hotelDto.getDisponibilityDateFrom());
        room.setDisponibilityDateTo(hotelDto.getDisponibilityDateTo());

        // genero el código aleatorio para el hotel

        String[] partsName = hotel.getName().split(" ");
        String[] partsNameScript = hotel.getName().split("-");
        String[] singleWord = hotel.getName().split("[^\\p{L}]+");

        StringBuilder codHotel = new StringBuilder();

        if (partsNameScript.length >= 2) {
            for (String part : partsNameScript) {
                if (part.matches("[\\p{L}]+")) {
                    String lettersOnly = Normalizer.normalize(part, Normalizer.Form.NFD).replaceAll("[^\\p{L}]", "");
                    codHotel.append(lettersOnly.length() >= 1 ? lettersOnly.substring(0, 1) : "");
                }
            }
        } else if (singleWord.length == 1 && singleWord[0].matches("[\\p{L}]+")) {
            String lettersOnly = Normalizer.normalize(singleWord[0], Normalizer.Form.NFD).replaceAll("[^\\p{L}]", "");
            codHotel.append(lettersOnly.substring(0, Math.min(3, lettersOnly.length())));
        } else if (partsName.length >= 2) {
            for (String part : partsName) {
                if (part.matches("[\\p{L}]+")) {
                    String lettersOnly = Normalizer.normalize(part, Normalizer.Form.NFD).replaceAll("[^\\p{L}]", "");
                    codHotel.append(lettersOnly.length() >= 1 ? lettersOnly.substring(0, 1) : "");
                }
            }
        }


        //Genero los números del código de forma aleatorio
        Random random = new Random();

        codHotel.append("-")
                .append(random.nextInt(10))
                .append(random.nextInt(10))
                .append(random.nextInt(10))
                .append(random.nextInt(10));



        hotel.setHotelCode(codHotel.toString().toUpperCase());
        Hotel savedHotel = hotelRepository.save(hotel);

        room.setHotel(savedHotel);

        roomRepository.save(room);

        return savedHotel;

    }

    //Este método es el encarfado de obtener los hoteles un rango de fechas y por los lugares que estén en disponible
    @Override
    public List<Hotel> getAllHotelsByPlaceDate(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String place, boolean isBooked) {

        List<Hotel> hotels = hotelRepository.getHotels(disponibilityDateFrom, disponibilityDateTo, place, isBooked);
        if (hotels.isEmpty() || hotels.stream().anyMatch(Hotel::isStatus)) {
            throw new IllegalArgumentException("There are no hotels with these criteria in the database.");
        }
        return hotels;
    }

    @Override
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        List<Hotel> filteredHotels = new ArrayList<>();

        if (hotels.stream().anyMatch(hotel -> !hotel.isStatus())) {
            for (Hotel hotel : hotels) {
                if (!hotel.isStatus()) {
                    filteredHotels.add(hotel);
                }
            }
            return filteredHotels;
        } else {
            throw new IllegalArgumentException("There are no hotels in the database.");
        }
    }


    @Override
    public void deleteHotelById(Long id) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();
            //El status del hotel significa que el vuelo ha sido borrado
            if (hotel.isStatus()) {
                throw new IllegalArgumentException("The hotel cannot be deleted because is not found in the database."); // No se puede eliminar uno hotel que no existe
            }

            if (!hotel.getBookHotels().isEmpty() && hotel.getBookHotels().stream().anyMatch(BookHotel::isStatus)&& hotel.isBooked()) {
                // Si tiene una reserva con status marcado y está reservado, lo marcamos como eliminado
                hotel.setStatus(true);
                hotel.setUpdated(LocalDate.now());
                hotelRepository.save(hotel);

            }else if(hotel.isBooked()) {
                throw new IllegalArgumentException("The hotel has a reservation.");
            }
            // Si no está reservado, lo marcamos como eliminado
            hotel.setStatus(true);
            hotel.setUpdated(LocalDate.now());
            hotelRepository.save(hotel);


        } else {
            throw new IllegalArgumentException("The hotel is not found in the database.");
        }
    }


    @Override
    public Hotel editHotelById(Long id, UpdateHotelDto hotelDto){

        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();

            if(hotel.isBooked()){
                throw new IllegalArgumentException("The hotel cannot be updated because it has a reservation.");

            }

            if (hotel.isStatus()){
                throw new IllegalArgumentException("The hotel cannot be updated because is not found in the database."); // No se puede eliminar uno hotel que no existe
            }

            hotel.setName(hotelDto.getName());
            hotel.setPlace(hotelDto.getPlace());

            //Como en mi caso yo actualizo el nombre del
            String[] partsName = hotel.getName().split(" ");
            String[] partsNameScript = hotel.getName().split("-");
            String[] singleWord = hotel.getName().split("[^\\p{L}]+");

            StringBuilder codHotel = new StringBuilder();

            if (partsNameScript.length >= 2) {
                for (String part : partsNameScript) {
                    if (part.matches("[\\p{L}]+")) {
                        String lettersOnly = Normalizer.normalize(part, Normalizer.Form.NFD).replaceAll("[^\\p{L}]", "");
                        codHotel.append(lettersOnly.length() >= 1 ? lettersOnly.substring(0, 1) : "");
                    }
                }
            } else if (singleWord.length == 1 && singleWord[0].matches("[\\p{L}]+")) {
                String lettersOnly = Normalizer.normalize(singleWord[0], Normalizer.Form.NFD).replaceAll("[^\\p{L}]", "");
                codHotel.append(lettersOnly.substring(0, Math.min(3, lettersOnly.length())));
            } else if (partsName.length >= 2) {
                for (String part : partsName) {
                    if (part.matches("[\\p{L}]+")) {
                        String lettersOnly = Normalizer.normalize(part, Normalizer.Form.NFD).replaceAll("[^\\p{L}]", "");
                        codHotel.append(lettersOnly.length() >= 1 ? lettersOnly.substring(0, 1) : "");
                    }
                }
            }


            //Genero los números del código de forma aleatorio
            Random random = new Random();

            codHotel.append("-")
                    .append(random.nextInt(10))
                    .append(random.nextInt(10))
                    .append(random.nextInt(10))
                    .append(random.nextInt(10));


            hotel.setHotelCode(codHotel.toString().toUpperCase());

            return hotelRepository.save(hotel);
        }else {
            throw new IllegalArgumentException("The hotel is not found in the database.");
        }


    }

    @Override
    public Hotel getHotelById(Long id) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();

            if (hotel.isStatus()) {
                throw new IllegalArgumentException("The hotel is not found in the database.");
            }

            return hotelRepository.findById(id).orElse(null);
        }else {
            throw new IllegalArgumentException("The hotel is not found in the database.");
        }

    }




}
