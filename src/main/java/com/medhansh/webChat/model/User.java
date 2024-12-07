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
            name = "contact",
            joinColumns = @JoinColumn(name = "user_id")
    )@Builder.Default
    private List<Contact> contacts = new ArrayList<>();
}
