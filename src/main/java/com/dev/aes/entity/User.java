package com.dev.aes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "username", length = 255, unique = true)
    private String username;


    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "phone_no", length = 255, unique = true)
    private String phoneNo;


    @Column(name = "organization_name", length = 255, unique = true)
    private String organizationName;


    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updatead_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "business_name", length = 255)
    private String businessName;


    @Column(name = "business_email", length = 255)
    private String businessEmail;

    @Column(name = "business_mobile_number", length = 255)
    private String businessMobileNumber;


    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "enabled")
    private boolean enabled;


    @Column(name = "token", length = 255)
    private String token;


    @Column(name = "token_created_at")
    private LocalDateTime tokenCreatedAt;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}

