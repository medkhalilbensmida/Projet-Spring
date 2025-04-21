package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.Data;
import java.util.Map;

@Data
public class UserStatsDTO {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private Map<String, Long> usersByRole;
    private long newUsersLast7Days;
    private long newUsersLast30Days;
    private double avgUserRegistrationPerDay;
    private UserActivityStats activityStats;
    private Map<String, Long> userRegistrationsByDay;

    @Data
    public static class UserActivityStats {
        private long recentlyUpdatedToday;  // Renommé pour refléter qu'on utilise updatedAt
        private long recentlyUpdatedLast7Days;
        private long recentlyUpdatedLast30Days;
        private String mostActiveTimeRange;
    }
}