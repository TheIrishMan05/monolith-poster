package ifmo.poster.monolith.dto.request.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketInventoryRequest {

    @NotNull(message = "ID события обязательно")
    private Long eventId;

    @NotNull(message = "ID типа билета обязательно")
    private Long ticketTypeId;

    /**
     * Без мест: сколько AVAILABLE билетов создать.
     * С местами можно не передавать — берётся seats.size().
     */
    @Min(value = 1, message = "Минимум 1 билет")
    @Max(value = 200, message = "За один запрос не больше 200 билетов")
    private Integer quantity;

    /**
     * Явные места. Если список не пустой — создаётся билет на каждое место.
     */
    @Valid
    @Size(max = 200, message = "За один запрос не больше 200 мест")
    private List<SeatSpec> seats;

    @Getter
    @Setter
    public static class SeatSpec {

        @NotBlank(message = "Ряд обязателен")
        @Size(max = 10, message = "Ряд не длиннее 10 символов")
        private String row;

        @Min(value = 1, message = "Номер места минимум 1")
        private int number;

        @Size(max = 50)
        private String sectorName;

        private Double xCoordinate;

        private Double yCoordinate;
    }
}