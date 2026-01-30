package ifmo.se.coursach_back.admin.api.dto;

public record AdminUpdateAccountRequest(
        Boolean isActive,
        String newPassword
) {
}
