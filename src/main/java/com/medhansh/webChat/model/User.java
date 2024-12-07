package com.medhansh.webChat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String profilePicture;

    @ElementCollection
    @CollectionTable(
            name = "user_contacts",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default  // This ensures that the default value of an empty list is used when building the object
    private List<Contact> contacts = new ArrayList<>();
}
