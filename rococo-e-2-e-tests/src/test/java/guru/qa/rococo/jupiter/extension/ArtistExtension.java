package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.container.Artists;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.impl.db.ArtistDbClient;
import guru.qa.rococo.service.impl.db.PaintingDbClient;
import guru.qa.rococo.utils.ImgBase64Utils;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static guru.qa.rococo.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class ArtistExtension implements
        BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistClient artistClient = new ArtistDbClient();
    private final PaintingClient paintingClient = new PaintingDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        List<ArtistJson> artists = new ArrayList<>();

        AnnotationSupport.findAnnotation(testMethod, Artists.class).ifPresent(
                artistRepeatable -> {
                    if (artistRepeatable.count() > 0) {
                        for (int i = 0; i < artistRepeatable.count(); i++) {
                            ArtistJson artistJson = artistClient.createArtist(ArtistJson.randomArtist());
                            artists.add(artistJson);
                        }
                    }
                }
        );

        List<Artist> artistAnnotations = AnnotationSupport.findRepeatableAnnotations(testMethod, Artist.class);
        for (Artist artistAnno : artistAnnotations) {
            final String name = artistAnno.name().isEmpty()
                    ? RandomDataUtils.randomName()
                    : artistAnno.name();

            boolean exists = artists.stream().anyMatch(a -> name.equals(a.name())) ;
            if (!exists) {
                final String biography = artistAnno.biography().isEmpty()
                        ? RandomDataUtils.randomSentence(50)
                        : artistAnno.biography();

                final String photo = artistAnno.photo().isEmpty()
                        ? null
                        : ImgBase64Utils.imageToBase64(artistAnno.photo());

                ArtistJson createdArtist = artistClient.createArtist(
                        new ArtistJson(
                                null,
                                name,
                                biography,
                                photo,
                                new ArrayList<>()
                        )
                );
                createdArtist.paintings().addAll(createPaintings(createdArtist, artistAnno.paintings()));
                artists.add(createdArtist);
            }
        }
        setArtists(artists);
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson[].class);
    }

    @Override
    public ArtistJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdArtists().toArray(ArtistJson[]::new);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static List<ArtistJson> createdArtists() {
        final ExtensionContext context = context();
        return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
                .orElse(Collections.emptyList());
    }


    public static void setArtists(List<ArtistJson> artist) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                artist
        );
    }


    private List<PaintingJson> createPaintings(ArtistJson artistJson, int count) {
        List<PaintingJson> paintings = new ArrayList<>();
        if (count == 0) {
            return Collections.emptyList();
        }

        for (int i = 0; i < count; i++) {
            paintings.add(paintingClient.createPainting(PaintingJson.randomPainting().addArtist(artistJson)));
        }
        return paintings;
    }
}
