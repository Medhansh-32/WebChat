package com.medhansh.webChat.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    // Getters and Setters

    @ElementCollection
    @CollectionTable(
            name = "user_contacts",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "contact_name")
    private List<String> contacts = new ArrayList<>();


}
