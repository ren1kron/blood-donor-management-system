package ifmo.se.coursach_back.nurse.api.dto;

public record VitalsPayload(
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        Double bodyTemperatureC,
        String wellbeing
) {
}
