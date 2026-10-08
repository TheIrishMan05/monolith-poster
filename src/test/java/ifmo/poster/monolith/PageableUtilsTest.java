package ifmo.poster.monolith;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ifmo.poster.monolith.util.PageableUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

/** Юнит-проверка жёсткого лимита page size = 50. */
class PageableUtilsTest {

    /** size == 50 допустим. */
    @Test
    void acceptsPageSizeAtLimit() {
        assertDoesNotThrow(() -> PageableUtils.ensureMaxPageSize(PageRequest.of(0, 50)));
    }

    /** size == 51 бросает IllegalArgumentException с упоминанием 50. */
    @Test
    void rejectsPageSizeAboveLimit() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> PageableUtils.ensureMaxPageSize(PageRequest.of(0, 51))
        );
        assertTrue(ex.getMessage().contains("50"));
    }
}
