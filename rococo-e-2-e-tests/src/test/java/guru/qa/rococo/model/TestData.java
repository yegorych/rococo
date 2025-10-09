package guru.qa.rococo.model;

import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record TestData(
        List<MuseumJson> museums,
        List<ArtistJson> artists,
        List<PaintingJson> paintings
) {

    @Nonnull
    public MuseumJson museumByTitle(@Nonnull String title) {
        if (museums.isEmpty()) {
            throw new IllegalArgumentException("Museum list is empty");
        }
        return museums
                .stream()
                .filter(m -> title.equals(m.title()))
                .findFirst()
                .orElseThrow();
    }

    @Nonnull
    public ArtistJson artistByName(@Nonnull String name) {
        if (artists.isEmpty()) {
            throw new IllegalArgumentException("Artist list is empty");
        }
        return artists
                .stream()
                .filter(a -> name.equals(a.name()))
                .findFirst()
                .orElseThrow();
    }

    @Nonnull
    public MuseumJson randomMuseum() {
        if (museums.isEmpty()) {
            throw new IllegalArgumentException("Museum list is empty");
        }
        return museums.get(ThreadLocalRandom.current().nextInt(museums.size()));
    }
}
