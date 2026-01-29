package ifmo.se.coursach_back.admin.dto;

public record AdminUpdateAccountRequest(
        Boolean isActive,
        String newPassword
) {
}
