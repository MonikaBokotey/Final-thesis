package com.licenta.healthapp.controller;

import com.licenta.healthapp.Requests.LoginRequest;
import com.licenta.healthapp.Requests.SignUpRequest;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.repository.UserRepository;
import com.licenta.healthapp.service.DailyLogService;
import com.licenta.healthapp.service.UserService;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import py4j.GatewayServer;

import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.PrivateKey;
import java.security.PublicKey;

@RestController
@CrossOrigin(origins = "*")

public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private DailyLogService dailyLogService;

    @Autowired
    private UserRepository userRepository;

    private final PublicKey publicKey;
    private final PrivateKey privateKey;



    @Autowired
    public LoginController(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

// THIS WORKS----------
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        String username = loginRequest.getUsername();
//        String password = loginRequest.getPassword();
//
//        // Authenticate user
//        Optional<User> authenticatedUser = userService.authenticateUser(username, password);
//        if (authenticatedUser.isPresent()) {
//            return ResponseEntity.ok(authenticatedUser.get());
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }

    //-------------
    @PostMapping("/signup")
    public ResponseEntity<User> signUpUser(@RequestBody SignUpRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String gender = request.getGender();
        double weight = request.getWeight();
        double height = request.getHeight();
        int targetWeight=request.getTargetWeight();



        return userService.signUpUser(username, password, email, gender, weight, height,targetWeight)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());


    }
//
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Optional<User> authenticatedUser = userService.authenticateUser(username, password);
        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            dailyLogService.checkAndCreateDailyLog(user.getUser_id());

            try {
                String encryptedUserId = RSAUtil.encrypt(String.valueOf(user.getUser_id()), publicKey);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.csv"))) {
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID"));
                    csvPrinter.printRecord(encryptedUserId);
                    csvPrinter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error encrypting user ID");
            }

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/publicKey")
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    @PostMapping(value = "/decryptUserId")
    public ResponseEntity<String> decryptUserId(@RequestBody String encryptedUserId) {
        try {
            String decryptedUserId = RSAUtil.decrypt(encryptedUserId, privateKey);
            return ResponseEntity.ok(decryptedUserId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error decrypting user ID");
        }
    }



    @GetMapping("/userCheck/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/csv")
    public String getCSVContent() {
        String filePath = "user_data.csv";
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while reading the file.";
        }
    }

}
