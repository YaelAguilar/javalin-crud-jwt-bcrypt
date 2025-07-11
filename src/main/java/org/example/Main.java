package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.example.configs.AppConfig;
import org.example.configs.DbConfig;
import org.example.controllers.AuthController;
import org.example.controllers.CartController;
import org.example.controllers.ProductController;
import org.example.controllers.UserController;
import org.example.daos.*;
import org.example.daos.impl.CartDAO;
import org.example.daos.impl.CartItemDAO;
import org.example.daos.impl.ProductDAO;
import org.example.daos.impl.UserDAO;
import org.example.middlewares.AuthMiddleware;
import org.example.routes.AuthRoutes;
import org.example.routes.CartRoutes;
import org.example.routes.ProductRoutes;
import org.example.routes.UserRoutes;
import org.example.services.AuthService;
import org.example.services.CartService;
import org.example.services.ProductService;
import org.example.services.UserService;

import java.util.Map;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        DbConfig.init();

        // --- Inyección de Dependencias Manual ---
        // DAOs
        IUserDAO userDAO = new UserDAO();
        IProductDAO productDAO = new ProductDAO();
        ICartDAO cartDAO = new CartDAO();
        ICartItemDAO cartItemDAO = new CartItemDAO();
        
        // Services
        AuthService authService = new AuthService(userDAO);
        UserService userService = new UserService(userDAO);
        ProductService productService = new ProductService(productDAO);
        CartService cartService = new CartService(cartDAO, cartItemDAO, productDAO);
        
        // Controllers
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        ProductController productController = new ProductController(productService);
        CartController cartController = new CartController(cartService);

        // Routes
        AuthRoutes authRoutes = new AuthRoutes(authController);
        UserRoutes userRoutes = new UserRoutes(userController);
        ProductRoutes productRoutes = new ProductRoutes(productController);
        CartRoutes cartRoutes = new CartRoutes(cartController);

        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(jacksonMapper));
            config.plugins.enableCors(cors -> cors.add(it -> {
                it.anyHost();
                it.exposeHeader("Authorization");
            }));
            if (AppConfig.isDevelopment()) {
                config.plugins.enableDevLogging();
            }
        });

        // --- Manejadores de Excepciones ---
        app.exception(AuthMiddleware.AuthException.class, (e, ctx) -> {
            ctx.status(401).json(Map.of("success", false, "message", e.getMessage()));
        });
        app.exception(AuthMiddleware.AdminAccessOnlyException.class, (e, ctx) -> {
            ctx.status(403).json(Map.of("success", false, "message", e.getMessage()));
        });
        app.exception(NoSuchElementException.class, (e, ctx) -> {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        });
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        });

        // --- Registrar las rutas ---
        authRoutes.register(app);
        userRoutes.register(app);
        productRoutes.register(app);
        cartRoutes.register(app);

        // Endpoint de prueba
        app.get("/", ctx -> ctx.json(Map.of(
            "status", "Ok",
            "message", "¡El servidor del e-commerce está funcionando!",
            "environment", AppConfig.getEnvironment()
        )));

        setupShutdownHook(app);
        app.start(AppConfig.getServerHost(), AppConfig.getServerPort());
        
        System.out.println("Servidor iniciado en http://" + AppConfig.getServerHost() + ":" + AppConfig.getServerPort());
    }

    private static void setupShutdownHook(Javalin app) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando la aplicación...");
            DbConfig.close();
            app.stop();
        }));
    }
}