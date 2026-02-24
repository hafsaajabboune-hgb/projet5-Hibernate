package com.example;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.service.SalleService;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations");
        EntityManager em = emf.createEntityManager();

        try {
            SalleRepository salleRepository = new SalleRepositoryImpl(em);
            SalleService salleService = new SalleServiceImpl(em, salleRepository);

            initializeTestData(em);

            System.out.println("\n=== Test 1: Recherche de salles disponibles par créneau ===");
            testAvailableRooms(salleService);

            System.out.println("\n=== Test 2: Recherche multi-critères ===");
            testMultiCriteriaSearch(salleService);

            System.out.println("\n=== Test 3: Pagination ===");
            testPagination(salleService);

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void initializeTestData(EntityManager em) {
        em.getTransaction().begin();

        Equipement imprimante = new Equipement("Imprimante", "Imprimante laser couleur");
        Equipement camera = new Equipement("Caméra", "Caméra HD pour visioconférence");
        Equipement wifi = new Equipement("Routeur WiFi", "Connexion internet haut débit");

        em.persist(imprimante);
        em.persist(camera);
        em.persist(wifi);

        Utilisateur user1 = new Utilisateur("El Idrissi", "Amine", "amine.elidrissi@example.com");
        Utilisateur user2 = new Utilisateur("Tahiri", "Salma", "salma.tahiri@example.com");

        em.persist(user1);
        em.persist(user2);

        Salle salle1 = new Salle("Salle X101", 28);
        salle1.setDescription("Salle de travail");
        salle1.setBatiment("Bloc X");
        salle1.setEtage(1);
        salle1.addEquipement(imprimante);

        Salle salle2 = new Salle("Salle Y202", 14);
        salle2.setDescription("Salle de réunion");
        salle2.setBatiment("Bloc Y");
        salle2.setEtage(2);
        salle2.addEquipement(camera);

        Salle salle3 = new Salle("Salle Z303", 55);
        salle3.setDescription("Salle de présentation");
        salle3.setBatiment("Bloc Z");
        salle3.setEtage(3);
        salle3.addEquipement(imprimante);
        salle3.addEquipement(wifi);

        Salle salle4 = new Salle("Salle X202", 22);
        salle4.setDescription("Salle informatique");
        salle4.setBatiment("Bloc X");
        salle4.setEtage(2);
        salle4.addEquipement(imprimante);
        salle4.addEquipement(camera);

        Salle salle5 = new Salle("Salle Y303", 38);
        salle5.setDescription("Salle polyvalente");
        salle5.setBatiment("Bloc Y");
        salle5.setEtage(3);
        salle5.addEquipement(wifi);

        em.persist(salle1);
        em.persist(salle2);
        em.persist(salle3);
        em.persist(salle4);
        em.persist(salle5);

        LocalDateTime now = LocalDateTime.now();

        Reservation res1 = new Reservation(
                now.plusDays(1).withHour(8).withMinute(0),
                now.plusDays(1).withHour(10).withMinute(0),
                "Séance de travail"
        );
        res1.setUtilisateur(user1);
        res1.setSalle(salle1);

        Reservation res2 = new Reservation(
                now.plusDays(2).withHour(15).withMinute(0),
                now.plusDays(2).withHour(17).withMinute(0),
                "Réunion de projet"
        );
        res2.setUtilisateur(user2);
        res2.setSalle(salle2);

        Reservation res3 = new Reservation(
                now.plusDays(3).withHour(9).withMinute(0),
                now.plusDays(3).withHour(11).withMinute(0),
                "Présentation interne"
        );
        res3.setUtilisateur(user1);
        res3.setSalle(salle3);

        em.persist(res1);
        em.persist(res2);
        em.persist(res3);

        em.getTransaction().commit();
        System.out.println("Données de test initialisées avec succès !");
    }

    private static void testAvailableRooms(SalleService salleService) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start1 = now.plusDays(1).withHour(9).withMinute(0);
        LocalDateTime end1 = now.plusDays(1).withHour(11).withMinute(0);

        System.out.println("Salles disponibles pour le créneau: " + start1 + " à " + end1);
        List<Salle> availableRooms1 = salleService.findAvailableRooms(start1, end1);

        if (availableRooms1.isEmpty()) {
            System.out.println("Aucune salle disponible pour ce créneau.");
        } else {
            for (Salle salle : availableRooms1) {
                System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() + ")");
            }
        }

        LocalDateTime start2 = now.plusDays(5).withHour(14).withMinute(0);
        LocalDateTime end2 = now.plusDays(5).withHour(16).withMinute(0);

        System.out.println("\nSalles disponibles pour le créneau: " + start2 + " à " + end2);
        List<Salle> availableRooms2 = salleService.findAvailableRooms(start2, end2);

        if (availableRooms2.isEmpty()) {
            System.out.println("Aucune salle disponible pour ce créneau.");
        } else {
            for (Salle salle : availableRooms2) {
                System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() + ")");
            }
        }
    }

    private static void testMultiCriteriaSearch(SalleService salleService) {
        Map<String, Object> criteria1 = new HashMap<>();
        criteria1.put("capaciteMin", 30);

        System.out.println("Recherche des salles avec capacité >= 30:");
        List<Salle> result1 = salleService.searchRooms(criteria1);

        for (Salle salle : result1) {
            System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() + ")");
        }

        Map<String, Object> criteria2 = new HashMap<>();
        criteria2.put("batiment", "Bloc X");

        System.out.println("\nRecherche des salles dans le Bloc X:");
        List<Salle> result2 = salleService.searchRooms(criteria2);

        for (Salle salle : result2) {
            System.out.println("- " + salle.getNom() + " (bâtiment: " + salle.getBatiment() + ")");
        }

        Map<String, Object> criteria3 = new HashMap<>();
        criteria3.put("capaciteMin", 20);
        criteria3.put("capaciteMax", 40);
        criteria3.put("etage", 2);

        System.out.println("\nRecherche des salles avec capacité entre 20 et 40, à l'étage 2:");
        List<Salle> result3 = salleService.searchRooms(criteria3);

        for (Salle salle : result3) {
            System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() +
                    ", étage: " + salle.getEtage() + ")");
        }
    }

    private static void testPagination(SalleService salleService) {
        int pageSize = 2;

        int totalPages = salleService.getTotalPages(pageSize);
        System.out.println("Nombre total de pages: " + totalPages);

        for (int page = 1; page <= totalPages; page++) {
            System.out.println("\nPage " + page + ":");

            List<Salle> sallesPage = salleService.getPaginatedRooms(page, pageSize);

            for (Salle salle : sallesPage) {
                System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() +
                        ", bâtiment: " + salle.getBatiment() + ")");
            }
        }

        long totalItems = salleService.getAllRooms().size();
        List<Salle> firstPageItems = salleService.getPaginatedRooms(1, pageSize);

        PaginationResult<Salle> paginationResult = new PaginationResult<>(
                firstPageItems, 1, pageSize, totalItems
        );

        System.out.println("\nInformations de pagination:");
        System.out.println("Page courante: " + paginationResult.getCurrentPage());
        System.out.println("Taille de la page: " + paginationResult.getPageSize());
        System.out.println("Nombre total de pages: " + paginationResult.getTotalPages());
        System.out.println("Nombre total d'éléments: " + paginationResult.getTotalItems());
        System.out.println("Page suivante disponible: " + paginationResult.hasNext());
        System.out.println("Page précédente disponible: " + paginationResult.hasPrevious());
    }
}