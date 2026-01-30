package ifmo.se.coursach_back.admin.api.dto;

public record MarkNotifiedRequest(
        String channel,
        String body
) {
}
