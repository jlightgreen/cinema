package cinema;

import cinema.exception.AuthenticationException;
import cinema.lib.Injector;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.ShoppingCart;
import cinema.model.User;
import cinema.security.AuthenticationService;
import cinema.service.CinemaHallService;
import cinema.service.MovieService;
import cinema.service.MovieSessionService;
import cinema.service.OrderService;
import cinema.service.ShoppingCartService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.log4j.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    private static Injector injector = Injector.getInstance("cinema");

    public static void main(String[] args) {
        MovieService movieService = (MovieService) injector.getInstance(MovieService.class);
        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        movie.setDescription("I never heard of the film");
        movieService.add(movie);
        movieService.getAll().forEach(logger::info);

        CinemaHall testHall = new CinemaHall();
        testHall.setCapacity(20);
        testHall.setDescription("little hall");
        CinemaHallService cinemaHallService =
                (CinemaHallService) injector.getInstance(CinemaHallService.class);
        cinemaHallService.add(testHall);
        cinemaHallService.getAll().forEach(logger::info);

        MovieSession movieSession = new MovieSession();
        movieSession.setCinemaHall(testHall);
        movieSession.setMovie(movie);
        movieSession.setShowTime(LocalDateTime.now());
        MovieSessionService movieSessionService =
                (MovieSessionService) injector.getInstance(MovieSessionService.class);
        movieSessionService.add(movieSession);
        movieSessionService.findAvailableSessions(movie.getId(),
                LocalDate.now()).forEach(logger::info);

        AuthenticationService authenticationService =
                (AuthenticationService) injector.getInstance(AuthenticationService.class);
        User testUser = new User();
        testUser.setEmail("shvabovichjulia@gmail.com");
        testUser.setPassword("12345");
        testUser = authenticationService.register(testUser.getEmail(), testUser.getPassword());
        try {
            testUser = authenticationService.login(testUser.getEmail(), testUser.getPassword());
            logger.info("Logged user: " + testUser);
        } catch (AuthenticationException e) {
            logger.error("AuthenticationException occured " + e);
        }

        ShoppingCartService shoppingCartService
                = (ShoppingCartService) injector.getInstance(ShoppingCartService.class);
        shoppingCartService.addSession(movieSession, testUser);
        ShoppingCart cart = shoppingCartService.getByUser(testUser);
        logger.info("Shopping cart of the user is " + cart);
        shoppingCartService.clear(cart);

        OrderService orderService
                = (OrderService) injector.getInstance(OrderService.class);
        cart = shoppingCartService.getByUser(testUser);
        orderService.completeOrder(cart.getTickets(), testUser);
        orderService.getOrderHistory(testUser).forEach(logger::info);
    }
}
