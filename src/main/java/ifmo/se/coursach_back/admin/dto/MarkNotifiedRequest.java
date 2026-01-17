package ifmo.se.coursach_back.admin.dto;

public record MarkNotifiedRequest(
        String channel,
        String body
) {
}
