package com.kbbukopin.webdash.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;  // Changed to String

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private boolean enabled;

    @OneToMany(mappedBy = "user")
    private List<PinnedProject> pinnedProjects = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Constructors, getters, setters, etc. can be added as needed.
}
