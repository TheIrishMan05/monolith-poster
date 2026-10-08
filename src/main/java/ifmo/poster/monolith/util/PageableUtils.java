package ifmo.poster.monolith.util;

import org.springframework.data.domain.Pageable;

public final class PageableUtils {

    public static final int MAX_PAGE_SIZE = 50;

    private PageableUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static void ensureMaxPageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be <= " + MAX_PAGE_SIZE);
        }
    }
}