import java.util.*;

enum SeatStatus { AVAILABLE, BOOKED }

class Movie {
    int id;
    String name;

    Movie(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Seat {
    int seatNo;
    SeatStatus status = SeatStatus.AVAILABLE;

    Seat(int seatNo) {
        this.seatNo = seatNo;
    }

    synchronized boolean bookSeat() {
        if (status == SeatStatus.BOOKED)
            return false;
        status = SeatStatus.BOOKED;
        return true;
    }
}

class Screen {
    int id;
    List<Seat> seats = new ArrayList<>();

    Screen(int id, int totalSeats) {
        this.id = id;
        for (int i = 1; i <= totalSeats; i++)
            seats.add(new Seat(i));
    }
}

class Show {
    int id;
    Movie movie;
    Screen screen;

    Show(int id, Movie movie, Screen screen) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
    }
}

class Theater {
    int id;
    String name;
    String city;
    List<Show> shows = new ArrayList<>();

    Theater(int id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    void addShow(Show show) {
        shows.add(show);
    }
}

class User {
    int id;
    String name;

    User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Booking {
    int bookingId;
    User user;
    Show show;
    List<Seat> seats;

    Booking(int bookingId, User user, Show show, List<Seat> seats) {
        this.bookingId = bookingId;
        this.user = user;
        this.show = show;
        this.seats = seats;
    }
}

class BookingService{

    Booking bookTickets(User user, Show show, List<Integer> seatNumbers) {

        List<Seat> bookedSeats = new ArrayList<>();

        for (int seatNo : seatNumbers) {
            Seat seat = show.screen.seats.get(seatNo - 1);

            if (!seat.bookSeat()) {
                System.out.println("Seat " + seatNo + " already booked!");
                return null;
            }

            bookedSeats.add(seat);
        }

        return new Booking(
                new Random().nextInt(10000),
                user,
                show,
                bookedSeats
        );
    }
}

class BookMyShow {

    List<Movie> movies = new ArrayList<>();
    List<Theater> theaters = new ArrayList<>();

    void addMovie(Movie movie) {
        movies.add(movie);
    }

    void addTheater(Theater theater) {
        theaters.add(theater);
    }

    List<Show> searchMovie(String city, String movieName) {

        List<Show> result = new ArrayList<>();

        for (Theater theater : theaters) {
            if (!theater.city.equals(city))
                continue;

            for (Show show : theater.shows) {
                if (show.movie.name.equals(movieName))
                    result.add(show);
            }
        }

        return result;
    }
}

public class BookingServic{

    public static void main(String[] args) {

        // Movie
        Movie movie = new Movie(1, "Pushpa 2");

        // Screen (10 seats)
        Screen screen = new Screen(1, 10);

        // Show
        Show show = new Show(1, movie, screen);

        // Theater
        Theater theater = new Theater(1, "PVR", "Delhi");
        theater.addShow(show);

        // BookMyShow
        BookMyShow bms = new BookMyShow();
        bms.addMovie(movie);
        bms.addTheater(theater);

        // Search
        List<Show> shows = bms.searchMovie("Delhi", "Pushpa 2");
        System.out.println("Shows Found: " + shows.size());

        // User
        User user = new User(1, "Vishal");

        // Booking
        BookingService bookingService = new BookingService();
        Booking booking = bookingService.bookTickets(
                user,
                shows.get(0),
                Arrays.asList(1, 2, 3)
        );

        if (booking != null) {
            System.out.println("Booking Successful!");
            System.out.println("Booking ID: " + booking.bookingId);
        }

        // Try booking same seat again
        bookingService.bookTickets(
                user,
                shows.get(0),
                Arrays.asList(2)
        );
    }
}