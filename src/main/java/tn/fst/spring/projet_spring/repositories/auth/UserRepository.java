package tn.fst.spring.projet_spring.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.fst.spring.projet_spring.model.auth.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    long countByIsActive(boolean isActive);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = ?1")
    long countByRoleName(String roleName);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= ?1")
    long countByCreatedAtAfter(LocalDateTime date);

    default long countNewUsersLast7Days() {
        return countByCreatedAtAfter(LocalDateTime.now().minusDays(7));
    }

    default long countNewUsersLast30Days() {
        return countByCreatedAtAfter(LocalDateTime.now().minusDays(30));
    }

    default long countNewUsersToday() {
        return countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
    }

    @Query("SELECT COUNT(DISTINCT u) FROM User u WHERE u.updatedAt >= ?1")
    long countByUpdatedAtAfter(LocalDateTime date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.updatedAt >= :since AND u.id = :userId")
    long countByUpdatedAtAfterForUser(Long userId, LocalDateTime since);

    default long countActiveToday() {
        return countByUpdatedAtAfter(LocalDateTime.now().minusDays(1));
    }

    default long countActiveLast7Days() {
        return countByUpdatedAtAfter(LocalDateTime.now().minusDays(7));
    }

    default long countActiveLast30Days() {
        return countByUpdatedAtAfter(LocalDateTime.now().minusDays(30));
    }

    @Query("SELECT u FROM User u WHERE u.role.name = ?1")
    List<User> findByRoleName(String roleName);

    @Query("SELECT FUNCTION('DAYOFWEEK', u.createdAt) as dayOfWeek, COUNT(u) as count " +
            "FROM User u " +
            "WHERE u.createdAt >= ?1 " +
            "GROUP BY FUNCTION('DAYOFWEEK', u.createdAt)")
    List<Object[]> countUsersByDayOfWeek(LocalDateTime startDate);

    default Map<Integer, Long> countUsersByDayOfWeekLast30Days() {
        List<Object[]> results = countUsersByDayOfWeek(LocalDateTime.now().minusDays(30));
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((Integer) result[0], (Long) result[1]);
        }
        return map;
    }

    @Query("SELECT COUNT(u) FROM User u WHERE u.updatedAt >= ?1")
    long countActiveSessions(LocalDateTime since);

    default long countActiveSessions() {
        return countActiveSessions(LocalDateTime.now().minusHours(1));
    }

    // Statistiques basées sur les messages pour l'activité
    @Query("SELECT COUNT(m), SUM(CASE WHEN m.sender.id = ?1 THEN 1 ELSE 0 END) " +
            "FROM Message m WHERE m.timestamp >= ?2")
    Object[] getUserMessageStats(Long userId, LocalDateTime since);

    default Object[] getUserMessageStatsLast30Days(Long userId) {
        return getUserMessageStats(userId, LocalDateTime.now().minusDays(30));
    }

    // Utilisateur le plus actif basé sur les messages envoyés
    @Query("SELECT u FROM User u WHERE u.id = " +
            "(SELECT m.sender.id FROM Message m " +
            "WHERE m.timestamp >= ?1 " +
            "GROUP BY m.sender.id " +
            "ORDER BY COUNT(m) DESC " +
            "LIMIT 1)")
    Optional<User> findMostActiveUser(LocalDateTime since);

    default Optional<User> findMostActiveUser() {
        return findMostActiveUser(LocalDateTime.now().minusDays(30));
    }

    // Heure d'activité maximale basée sur les messages
    @Query("SELECT FUNCTION('HOUR', m.timestamp) as hour, COUNT(m) as count " +
            "FROM Message m " +
            "WHERE m.timestamp >= ?1 " +
            "GROUP BY FUNCTION('HOUR', m.timestamp)")
    List<Object[]> countMessagesByHour(LocalDateTime since);

    default Map<Integer, Long> countMessagesByHourLast30Days() {
        List<Object[]> results = countMessagesByHour(LocalDateTime.now().minusDays(30));
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((Integer) result[0], (Long) result[1]);
        }
        return map;
    }

    @Query("SELECT FUNCTION('HOUR', u.updatedAt) as hour, COUNT(u) as count " +
            "FROM User u " +
            "WHERE u.updatedAt >= ?1 " +
            "GROUP BY FUNCTION('HOUR', u.updatedAt)")
    List<Object[]> countActivitiesByHour(LocalDateTime since);

    default Map<Integer, Long> countActivitiesByHourLast30Days() {
        List<Object[]> results = countActivitiesByHour(LocalDateTime.now().minusDays(30));
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((Integer) result[0], (Long) result[1]);
        }
        return map;
    }

}