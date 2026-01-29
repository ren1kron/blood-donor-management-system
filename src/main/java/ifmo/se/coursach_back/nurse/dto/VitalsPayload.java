package ifmo.se.coursach_back.nurse.dto;

public record VitalsPayload(
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        Double bodyTemperatureC,
        String wellbeing
) {
}
