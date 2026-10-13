package devSuivi.dto;

public record ProjectListDto(
        Long id,
        String name,
        String description,
        int quota,
        String status,
        ServerListDto server) {
}
