package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.User;
import com.portfolio.LOC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;



    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializationService - Starting user initialization");
        initializeUsers();
    }

    private void initializeUsers() {
        // Check if users already exist
        long userCount = userRepository.count();
        System.out.println("DataInitializationService - Current user count: " + userCount);
        if (userCount > 0) {
            System.out.println("DataInitializationService - Users already exist, skipping initialization");
            return;
        }

        // Create demo users
        createUser("Admin User", "admin@smartloan.com", "password", User.Role.ADMIN);
        createUser("Sales Executive", "sales@smartloan.com", "password", User.Role.SALES_EXECUTIVE);
        createUser("RCPU Officer", "rcpu@smartloan.com", "password", User.Role.UNDERWRITER);
        createUser("L1 Manager", "l1@smartloan.com", "password", User.Role.MANAGER_L1);
        createUser("L2 Manager", "l2@smartloan.com", "password", User.Role.MANAGER_L2);

        System.out.println("Demo users created successfully!");
        System.out.println("Login credentials:");
        System.out.println("Admin: admin@smartloan.com / password");
        System.out.println("Sales: sales@smartloan.com / password");
        System.out.println("RCPU: rcpu@smartloan.com / password");
        System.out.println("L1: l1@smartloan.com / password");
        System.out.println("L2: l2@smartloan.com / password");
    }

    private void createUser(String name, String email, String password, User.Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // Plain text password for demo
        user.setRole(role);
        user.setIsActive(true);
        userRepository.save(user);
    }
}
