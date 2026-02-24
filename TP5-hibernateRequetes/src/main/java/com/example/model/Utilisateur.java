package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    // Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public Utilisateur(String nom, String prenom, String email, List<Reservation> reservations) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.reservations = reservations;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Méthodes utilitaires pour gérer la relation bidirectionnelle
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setUtilisateur(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setUtilisateur(null);
    }

    // Méthodes pratiques
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean hasReservations() {
        return reservations != null && !reservations.isEmpty();
    }

    public int getNombreReservations() {
        return reservations != null ? reservations.size() : 0;
    }

    // toString
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", nombreReservations=" + getNombreReservations() +
                '}';
    }

    // equals et hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;

        // Si les deux ont des IDs non null, comparer par ID
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        // Sinon, comparer par email (qui est unique)
        return email != null ? email.equals(that.email) : super.equals(o);
    }

    @Override
    public int hashCode() {
        // Utiliser l'ID si disponible, sinon l'email
        return id != null ? id.hashCode() : (email != null ? email.hashCode() : super.hashCode());
    }
}