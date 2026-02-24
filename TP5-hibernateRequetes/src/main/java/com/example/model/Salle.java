package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité minimum est de 1 personne")
    @Column(nullable = false)
    private Integer capacite;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;

    @Column(name = "batiment")
    private String batiment;

    @Column(name = "etage")
    private Integer etage;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "salle_equipement",
            joinColumns = @JoinColumn(name = "salle_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id")
    )
    private Set<Equipement> equipements = new HashSet<>();

    // Constructeurs
    public Salle() {
    }

    public Salle(String nom, Integer capacite) {
        this.nom = nom;
        this.capacite = capacite;
    }

    public Salle(String nom, Integer capacite, String description, String batiment, Integer etage) {
        this.nom = nom;
        this.capacite = capacite;
        this.description = description;
        this.batiment = batiment;
        this.etage = etage;
    }

    public Salle(String nom, Integer capacite, String description, String batiment, Integer etage,
                 List<Reservation> reservations, Set<Equipement> equipements) {
        this.nom = nom;
        this.capacite = capacite;
        this.description = description;
        this.batiment = batiment;
        this.etage = etage;
        this.reservations = reservations;
        this.equipements = equipements;
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

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBatiment() {
        return batiment;
    }

    public void setBatiment(String batiment) {
        this.batiment = batiment;
    }

    public Integer getEtage() {
        return etage;
    }

    public void setEtage(Integer etage) {
        this.etage = etage;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Set<Equipement> getEquipements() {
        return equipements;
    }

    public void setEquipements(Set<Equipement> equipements) {
        this.equipements = equipements;
    }

    // Méthodes utilitaires pour la relation avec Reservation
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setSalle(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setSalle(null);
    }

    // Méthodes utilitaires pour la relation avec Equipement
    public void addEquipement(Equipement equipement) {
        equipements.add(equipement);
        equipement.getSalles().add(this);
    }

    public void removeEquipement(Equipement equipement) {
        equipements.remove(equipement);
        equipement.getSalles().remove(this);
    }

    // Méthodes pratiques
    public String getLocalisationComplete() {
        StringBuilder localisation = new StringBuilder();
        if (batiment != null && !batiment.isEmpty()) {
            localisation.append("Bâtiment ").append(batiment);
        }
        if (etage != null) {
            if (localisation.length() > 0) {
                localisation.append(", ");
            }
            localisation.append("Étage ").append(etage);
        }
        return localisation.length() > 0 ? localisation.toString() : "Localisation non spécifiée";
    }

    public boolean hasReservations() {
        return reservations != null && !reservations.isEmpty();
    }

    public int getNombreReservations() {
        return reservations != null ? reservations.size() : 0;
    }

    public boolean hasEquipements() {
        return equipements != null && !equipements.isEmpty();
    }

    public int getNombreEquipements() {
        return equipements != null ? equipements.size() : 0;
    }

    public boolean isCapaciteSuffisante(int nombrePersonnes) {
        return capacite != null && capacite >= nombrePersonnes;
    }

    public String getDescriptionCourte() {
        if (description == null || description.length() <= 50) {
            return description;
        }
        return description.substring(0, 47) + "...";
    }

    // toString
    @Override
    public String toString() {
        return "Salle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", capacite=" + capacite +
                ", batiment='" + batiment + '\'' +
                ", etage=" + etage +
                ", reservations=" + getNombreReservations() +
                ", equipements=" + getNombreEquipements() +
                '}';
    }

    // equals et hashCode basés sur l'id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salle salle = (Salle) o;
        return id != null ? id.equals(salle.id) : super.equals(o);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}