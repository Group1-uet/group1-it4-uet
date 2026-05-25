package auction.model;

import java.util.Objects;

public class Admin extends User {
    private String department;
    private String adminLevel; // SUPER_ADMIN, MODERATOR, SUPPORT

    public Admin(String id, String username, String password, String email, String department, String adminLevel) {
        super(id, username, password, email, "ADMIN");
        this.department = department;
        this.adminLevel = adminLevel;
    }

    public Admin(String id, String username, String password, String email) {
        this(id, username, password, email, "General", "SUPPORT");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        if (!adminLevel.matches("SUPER_ADMIN|MODERATOR|SUPPORT")) {
            throw new IllegalArgumentException("Invalid admin level");
        }
        this.adminLevel = adminLevel;
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(adminLevel);
    }

    public boolean isModerator() {
        return "MODERATOR".equals(adminLevel);
    }

    public boolean isSupport() {
        return "SUPPORT".equals(adminLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return Objects.equals(adminLevel, admin.adminLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adminLevel);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                '}';
    }
}

