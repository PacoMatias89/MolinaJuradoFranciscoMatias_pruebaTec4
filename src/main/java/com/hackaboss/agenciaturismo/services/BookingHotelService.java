package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.BookingHotelDto;
import com.hackaboss.agenciaturismo.dto.UpdateBookingHotelDto;
import com.hackaboss.agenciaturismo.model.BookHotel;
import com.hackaboss.agenciaturismo.model.Hotel;
import com.hackaboss.agenciaturismo.model.Room;
import com.hackaboss.agenciaturismo.model.User;
import com.hackaboss.agenciaturismo.repositories.BookingHotelRepository;
import com.hackaboss.agenciaturismo.repositories.HotelRepository;
import com.hackaboss.agenciaturismo.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingHotelService implements IBookingHotelService {

    @Autowired
    private BookingHotelRepository bookHotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private IUserService userService;

    @Override
    public BookHotel createBookHotel(BookingHotelDto bookingHotelDto) {
        // Inicializar BookHotel
        BookHotel bookHotel = new BookHotel();

        // Obtener el hotel existente
        Hotel hotelExist = hotelRepository.findByHotelCode(bookingHotelDto.getHotelCode());

        if (hotelExist == null) {
            throw new IllegalArgumentException("The hotel does not exist in the database");
        }

        //Comprobamos que el hotel esté eliminado de la forma lógica

        if (hotelExist.isStatus()){
            throw new IllegalArgumentException("The hotel does not exist in the database");
        }
        //Comprobamos que el hotel tenga reserva
        if (!hotelExist.getPlace().equalsIgnoreCase(bookingHotelDto.getPlace())) {
            throw new IllegalArgumentException("the hotel does not correspond to the city");

        }


        // Obtener datos
        List<Room> rooms = roomRepository.findAll();

        // Verificar la fecha
        LocalDate dateFrom = bookingHotelDto.getDateFrom();
        LocalDate dateTo = bookingHotelDto.getDateTo();

        // Obtenemos las fechas de la habitación específica para el tipo y código de hotel
        Room roomDisponibility = rooms.stream()
                .filter(r -> r.getHotel().getHotelCode().equals(bookingHotelDto.getHotelCode()) &&
                        r.getRoomType().equalsIgnoreCase(bookingHotelDto.getRoomType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The specific room for the reservation was not found."));


        LocalDate startDate = roomDisponibility.getDisponibilityDateFrom();
        LocalDate endDate = roomDisponibility.getDisponibilityDateTo();

        // Verificamos las fechas de reserva contra las fechas de disponibilidad específicas de la habitación
        if (dateTo.isBefore(startDate) || dateFrom.isAfter(endDate)) {
            throw new IllegalArgumentException("The reservation dates are not within the room's availability range or are invalid.");
        } else {
            // Verificar si el hotel no está reservado
            if (!hotelExist.isBooked()) {
                hotelRepository.save(hotelExist);

                bookHotel.setHotelCode(hotelExist.getHotelCode());
                bookHotel.setDateFrom(dateFrom);
                bookHotel.setDateTo(dateTo);

                // Calcular las noches con las fechas que ingresamos
                int nights = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);
                bookHotel.setNights(nights);

                // Crear la lista de usuarios
                List<User> users = new ArrayList<>();

                for (User userDto : bookingHotelDto.getHosts()) {
                    User user = userService.findByEmail(userDto.getEmail());
                    if (user == null) {
                        // Si el usuario no existe, crea uno nuevo
                        user = new User();
                    }
                    // Actualizar datos del usuario
                    user.setName(userDto.getName());
                    user.setLastName(userDto.getLastName());
                    user.setEmail(userDto.getEmail());
                    user.setPassPort(userDto.getPassPort());
                    user.setAge(userDto.getAge());

                    // Guardar o actualizar el usuario
                    userService.createUser(user);

                    users.add(user);
                }

                // Asociar el hotel y usuarios con la reserva
                bookHotel.setRoomType(bookingHotelDto.getRoomType());
                bookHotel.setHotel(hotelExist);
                bookHotel.setUsers(users);
                bookHotel.setPeopleQ(users.size());

                // Calcular el precio por noche y total
                double totalPrice = rooms.stream()
                        .filter(room -> room.getHotel().getHotelCode().equals(bookingHotelDto.getHotelCode()) &&
                                room.getRoomType().equalsIgnoreCase(bookingHotelDto.getRoomType()))
                        .mapToDouble(room -> nights * room.getRoomPrice())
                        .findFirst()
                        .orElse(0.0);
                bookHotel.setPrice(totalPrice);

                //Comprobamos que el usuario no pueda reservar dos habitaciones distintas con la misma fecha
                List<BookHotel> existingBookings = bookHotelRepository.getUserAndDateRange(users, dateFrom, dateTo);

                // Comprobamos si alguna de las reservas existentes coincide con las fechas de la nueva reserva
                if (existingBookings.isEmpty() && existingBookings.stream().noneMatch(BookHotel::isStatus)) {
                    hotelExist.setBooked(true);
                    return bookHotelRepository.save(bookHotel);
                } else {
                    throw new IllegalArgumentException("The user already has a reservation for the specified dates.");
                }



            } else {
                throw new IllegalArgumentException("The hotel is already booked");
            }
        }

    }

    @Override
    public List<BookHotel> getAllBookHotels() {
        List<BookHotel> bookHotels = bookHotelRepository.findAll();
        List<BookHotel> filterBookHotels = new ArrayList<>(); // Me creo una lista para agregar los que no están eliminados

        if (bookHotels.stream().anyMatch(bookHotel -> !bookHotel.isStatus())) {
            for (BookHotel bookHotel : bookHotels) {
                if (!bookHotel.isStatus()) {
                    filterBookHotels.add(bookHotel);
                }
            }
            return filterBookHotels;
        }else {
            throw new IllegalArgumentException("There are no available booked hotels in the database.");
        }


    }

    @Override
    public BookHotel getBookHotelById(Long id) {
        Optional<BookHotel> optionalBookHotel = bookHotelRepository.findById(id);
        //Comprobamos que existe la reserva
        if (optionalBookHotel.isPresent()){
            BookHotel bookHotel = optionalBookHotel.get();
            if (bookHotel.isStatus()) {
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }
            return bookHotel;
        }else {
            throw new IllegalArgumentException("The hotel reservation does not exist");
        }

    }

    @Override
    public BookHotel deleteBookHotelById(Long id) {
        //Verificamos que la reserva exista
        Optional<BookHotel> optionalBookHotel = bookHotelRepository.findById(id);

        //Verificamos que exista, y si existe que lo elimine
        //siempre y cuando la reservar no esté eliminada
        if (optionalBookHotel.isPresent()){
            BookHotel bookHotel = optionalBookHotel.get();

            if (bookHotel.isStatus()) {
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }

            bookHotel.setStatus(true);
            bookHotel.setUpdated(LocalDate.now());
            bookHotel.getHotel().setBooked(false);//cambiamos la columna de reservado a disponible
            return bookHotelRepository.save(bookHotel);


        }else {
            throw new IllegalArgumentException("The hotel reservation does not exist");
        }



    }

    @Override
    public BookHotel editBookHotelById(Long id, UpdateBookingHotelDto bookingHotelDto) {

        Optional<BookHotel> optionalBookHotel = bookHotelRepository.findById(id);

        //Hacermos lo mismo que el eliminado pero en vez de eliminarlo para actualizarlo
        if (optionalBookHotel.isPresent()) {
            BookHotel bookHotel = getBookHotel(bookingHotelDto, optionalBookHotel);

            bookHotel.setDateFrom(bookingHotelDto.getDateFrom());
            bookHotel.setDateTo(bookingHotelDto.getDateTo());


            //Al cambiar las fechas entonces tendremos que cambiar tanto las noches y
            // evidentemente también el precio

            int nights = (int) ChronoUnit.DAYS.between(bookingHotelDto.getDateFrom(), bookingHotelDto.getDateTo());
            bookHotel.setNights(nights);

            //Buscamos la reserva
            List<Room> rooms = roomRepository.findAll();

            //Aquí obtenemos el precio total de la habitación
            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getHotelCode().equals(bookHotel.getHotelCode()) &&
                            room.getRoomType().equalsIgnoreCase(bookHotel.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .findFirst()
                    .orElse(0.0);
            bookHotel.setPrice(totalPrice);


            return bookHotelRepository.save(bookHotel);
        }else {
            throw new IllegalArgumentException("The hotel reservation does not exist");
        }

    }
    //Método para validar la fecha de la reserva y devolver un detalle de la actualización de la reserva del hotel
    private static BookHotel getBookHotel(UpdateBookingHotelDto bookingHotelDto, Optional<BookHotel> optionalBookHotel) {
        BookHotel bookHotel = optionalBookHotel.get();

        //verificamos las fechas si están dentro del rango de la habitación
        if (bookingHotelDto.getDateFrom().isBefore(bookHotel.getHotel().getRoom().getDisponibilityDateFrom()) ||
                bookingHotelDto.getDateTo().isAfter(bookHotel.getHotel().getRoom().getDisponibilityDateTo()) ||
                bookingHotelDto.getDateTo().isBefore(bookHotel.getHotel().getRoom().getDisponibilityDateFrom())||
        bookingHotelDto.getDateTo().isBefore(bookingHotelDto.getDateFrom())) {
            throw new IllegalArgumentException("The reservation dates are not within the room's availability range or are not valid." +
                    " The dates are:\n" +
                    //Obtenemos las fechas de la habitación para mostrarle al usuario el rango de fechas que tiene la habitación
                    "- From: " + bookingHotelDto.getDateFrom() +'\n' +
                    "- To: " + bookingHotelDto.getDateTo()  + '\n' +
                    "The dates you want to change are:\n" +
                    "- From: " + bookHotel.getDateFrom() + '\n' +
                    "- To: " + bookHotel.getDateTo() + '\n' );
        }


        if (bookHotel.isStatus()) {
            throw new IllegalArgumentException("The reservation is not found in the database.");
        }
        return bookHotel;
    }
}
