package com.kbbukopin.webdash.setup;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kbbukopin.webdash.entity.User;
import com.kbbukopin.webdash.repository.UserRepository;
import com.kbbukopin.webdash.entity.Role;
import com.kbbukopin.webdash.repository.RoleRepository;  // Add this import

@Component
public class AdminAccountSetup implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;  // Inject RoleRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin account already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Create admin user
            User adminUser = new User();
            adminUser.setId("1");  // Set a suitable ID
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpassword"));
            adminUser.setEnabled(true);

            // Create "admin" role if not exists
            Role adminRole = roleRepository.findByName("admin");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("admin");
                roleRepository.save(adminRole);
            }

            // Assign roles to admin user
            adminUser.setRoles(Collections.singleton(adminRole));

            userRepository.save(adminUser);
        }
    }
}
