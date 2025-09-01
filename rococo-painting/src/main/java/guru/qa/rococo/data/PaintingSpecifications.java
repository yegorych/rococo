package guru.qa.rococo.data;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class PaintingSpecifications {

    public static Specification<PaintingEntity> withFilter(String title, String artistId) {
        return Specification.where(titleContains(title))
                .and(artistIdEquals(artistId));
    }

    private static Specification<PaintingEntity> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return cb.conjunction();
            String normalized = "%" + title.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), normalized);
        };
    }

    private static Specification<PaintingEntity> artistIdEquals(String artistId) {
        return (root, query, cb) ->
                artistId == null || artistId.isEmpty() ? cb.conjunction() : cb.equal(root.get("artistId"), UUID.fromString(artistId));
    }
}
