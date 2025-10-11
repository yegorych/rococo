package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.jupiter.annotation.container.Paintings;
import guru.qa.rococo.model.rest.*;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.impl.db.ArtistDbClient;
import guru.qa.rococo.service.impl.db.MuseumDbClient;
import guru.qa.rococo.service.impl.db.PaintingDbClient;
import guru.qa.rococo.utils.ImgBase64Utils;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.*;

import static guru.qa.rococo.jupiter.extension.TestsMethodContextExtension.context;
import static guru.qa.rococo.model.rest.ArtistJson.randomArtist;
import static guru.qa.rococo.model.rest.PaintingJson.randomPainting;
import static guru.qa.rococo.utils.RandomDataUtils.*;

@ParametersAreNonnullByDefault
public class PaintingExtension implements
        BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    private final PaintingClient paintingClient = new PaintingDbClient();
    private final ArtistClient artistClient = new ArtistDbClient();
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        List<PaintingJson> paintings = new ArrayList<>();

        AnnotationSupport.findAnnotation(testMethod, Paintings.class).ifPresent(
                paintingRepeatable -> {
                    if (paintingRepeatable.count() > 0) {
                        for (int i = 0; i < paintingRepeatable.count(); i++) {
                            ArtistJson artistJson = artistClient.createArtist(randomArtist());
                            paintings.add(paintingClient.createPainting(randomPainting().addArtist(artistJson)));
                        }
                    }
                }
        );

        List<Painting> paintingAnnotations = AnnotationSupport.findRepeatableAnnotations(testMethod, Painting.class);
        if (!paintingAnnotations.isEmpty()) {
            for (Painting paintingAnno : paintingAnnotations) {
                final String title = paintingAnno.title().isEmpty()
                        ? randomName()
                        : paintingAnno.title();

                PaintingJson paintingJson = Objects.requireNonNullElseGet(
                        paintingClient.findByTitle(title),
                        () -> {
                            final String description = paintingAnno.description().isEmpty()
                                    ? RandomDataUtils.randomSentence(50)
                                    : paintingAnno.description();

                            final String content = paintingAnno.photo().isEmpty()
                                    ? null
                                    : ImgBase64Utils.imageToBase64(paintingAnno.photo());

                            ArtistJson artistJson = paintingAnno.artist().name().isEmpty()
                                    ? artistClient.createArtist(
                                            new ArtistJson(
                                                    null,
                                                    randomName(),
                                                    paintingAnno.artist().biography().isEmpty() ? randomSentence(20) : paintingAnno.artist().biography(),
                                                    paintingAnno.artist().photo().isEmpty() ? null : ImgBase64Utils.imageToBase64(paintingAnno.artist().photo())
                                            )
                                    )
                                    : ArtistExtension.createdArtists()
                                    .stream().filter(a ->
                                            paintingAnno.artist().name().equals(a.name()))
                                    .findFirst().orElseThrow(()-> new IllegalArgumentException("Artist not found"));


                            MuseumJson museumJson = null;
                            if (paintingAnno.museum().createMuseum()){
                                final Museum museum = paintingAnno.museum();
                                final String desc = museum.description().isEmpty()
                                        ? randomSentence(10)
                                        : museum.description();

                                final String photo = museum.photo().isEmpty()
                                        ? null
                                        : ImgBase64Utils.imageToBase64(museum.photo());

                                museumJson = paintingAnno.museum().title().isEmpty()
                                        ? museumClient.createMuseum(
                                        new MuseumJson(
                                                null,
                                                RandomDataUtils.randomMuseumTitle(),
                                                desc,
                                                new GeoJson(
                                                        museum.city().isEmpty() ? randomCity() : museum.city(),
                                                        new CountryJson(
                                                                null,
                                                                paintingAnno.museum().country()
                                                        )
                                                ),
                                                photo
                                        ))
                                        : MuseumExtension.createdMuseums()
                                        .stream().filter(m ->
                                                paintingAnno.museum().title().equals(m.title()))
                                        .findFirst().orElseThrow(()-> new IllegalArgumentException("Museum not found"));

                            }


//                            final String museumTitle = paintingAnno.museum().title().isEmpty()
//                                    ? RandomDataUtils.randomMuseumTitle()
//                                    : paintingAnno.museum().title();
//
//                            MuseumJson museumJson = paintingAnno.museum().createMuseum()
//                                    ? MuseumExtension.createdMuseums()
//                                    .stream().filter(m -> museumTitle.equals(m.title()))
//                                    .findFirst()
//                                    .orElseGet(() -> {
//                                        final Museum museum = paintingAnno.museum();
//                                        final String desc = museum.description().isEmpty()
//                                                ? randomSentence(10)
//                                                : museum.description();
//
//                                        final String photo = museum.photo().isEmpty()
//                                                ? null
//                                                : ImgBase64Utils.imageToBase64(museum.photo());
//
//                                        MuseumJson m = museumClient.createMuseum(
//                                                new MuseumJson(
//                                                        null,
//                                                        museumTitle,
//                                                        desc,
//                                                        new GeoJson(
//                                                                museum.city().isEmpty() ? randomCity() : museum.city(),
//                                                                new CountryJson(
//                                                                        null,
//                                                                        paintingAnno.museum().country()
//                                                                )
//                                                        ),
//                                                        photo
//                                                ));
//                                        MuseumExtension.createdMuseums().add(m);
//                                        return m;
//                                    })
//                                    : null;

                            return paintingClient.createPainting(
                                    new PaintingJson(
                                            null,
                                            title,
                                            description,
                                            museumJson,
                                            artistJson,
                                            content
                                    )
                            ).addArtist(artistJson).addMuseum(museumJson);
                        }
                );
                paintings.add(paintingJson);
            }
            setPaintings(paintings);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson[].class);
    }

    @Override
    public PaintingJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdPaintings().toArray(PaintingJson[]::new);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static List<PaintingJson> createdPaintings() {
        final ExtensionContext context = context();
        return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
                .orElse(Collections.emptyList());
    }

    public static void setPaintings(List<PaintingJson> paintings) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                paintings
        );
    }
}
