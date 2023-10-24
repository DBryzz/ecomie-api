package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;
    private String firstname;
    private String lastname;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    /** PROFILE INFO */
    @Column(nullable = true)
    private String phoneNumber;
    @Column(nullable = true)
    private String country;
    @Column(nullable = true)
    private String region;
    @Column(nullable = true)
    private String city;
    @Column(nullable = true)
    private String language;
    @Column(nullable = true)
    private String profilePictureFileName;
    /** END */

    private String roles;
    private boolean accountEnabled;
    private String emailVerificationToken;
    private boolean accountBlocked;
    private boolean accountSoftDeleted;
    @Column(nullable = true)
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;
//    @Column(nullable = true)
//    @OneToMany(mappedBy = "ecomiest")
//    private List<ChallengeReport> challengeReports;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));

//        ================================================================
        ArrayList<String> roles = new ArrayList<>(Arrays.asList(this.roles.split("-")));
//        user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }



    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {return this.accountEnabled;}

    public boolean toggleEnable() {return !this.accountEnabled;}
    public boolean toggleBlock() {return !this.accountBlocked;}
    public boolean toggleSoftDelete() {return !this.accountSoftDeleted;}


    public void addSubscription(Subscription subscription) {
        this.subscriptions.add(subscription);
        subscription.setUser(this);
    }

    public void removeSubscription(Subscription subscription) {
        this.subscriptions.remove(subscription);
        subscription.setUser(null);
    }

//    public void addChallengeReport(ChallengeReport report) {
//        this.challengeReports.add(report);
//        report.setEcomiest(this);
//    }
//
//    public void removeChallengeReport(ChallengeReport report) {
//        this.challengeReports.remove(report);
//        report.setEcomiest(null);
//    }

}
