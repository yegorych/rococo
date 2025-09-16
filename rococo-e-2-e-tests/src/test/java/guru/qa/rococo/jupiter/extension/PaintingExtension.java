package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.rest.*;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.service.impl.MuseumDbClient;
import guru.qa.rococo.service.impl.PaintingDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.Objects;

import static guru.qa.rococo.jupiter.extension.TestsMethodContextExtension.context;
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
      AnnotationSupport.findAnnotation(testMethod, Painting.class)
              .ifPresent(paintingAnno -> {
                  final String title = paintingAnno.title().isEmpty()
                          ? randomName()
                          : paintingAnno.title();

                  PaintingJson paintingJson = Objects.requireNonNullElseGet(
                          paintingClient.findByTitle(title),
                          () -> {
                              final String description = paintingAnno.description().isEmpty()
                                      ? RandomDataUtils.randomSentence(50)
                                      : paintingAnno.description();

                              ArtistJson artistJson;
                              if (paintingAnno.artist().name().isEmpty()){
                                  artistJson = new ArtistJson(
                                          null,
                                          randomName(),
                                          randomSentence(50),
                                          null
                                  );
                              } else {
                                  ArtistJson searchedArtist = artistClient.findArtistByName(
                                          paintingAnno.artist().name()
                                  );
                                  artistJson = searchedArtist == null
                                          ? new ArtistJson(
                                                  null,
                                                  paintingAnno.artist().name(),
                                                  paintingAnno.artist().biography(),
                                                  null
                                          )
                                          : searchedArtist;
                              }


                              MuseumJson museumJson = null;
                              if (paintingAnno.museum() != null) {
                                  if (paintingAnno.museum().title().isEmpty()){
                                      museumJson = new MuseumJson(
                                              null,
                                              randomMuseumTitle(),
                                              randomSentence(50),
                                              new GeoJson(
                                                      randomCity(),
                                                      new CountryJson(
                                                              null,
                                                              CountryEnum.fromName(randomCountry())
                                                      )
                                              ),
                                              null);
                                  } else {
                                      MuseumJson searchedMuseum = museumClient.findMuseumByTitle(
                                              paintingAnno.museum().title()
                                      );
                                      museumJson = searchedMuseum == null
                                              ? new MuseumJson(
                                                      null,
                                                      paintingAnno.artist().name(),
                                                      paintingAnno.artist().biography(),
                                                      new GeoJson(
                                                              paintingAnno.museum().city(),
                                                              new CountryJson(
                                                                      null,
                                                                      paintingAnno.museum().country()
                                                              )
                                                      ),
                                                      null
                                              )
                                              : searchedMuseum;
                                  }
                              }

                              return paintingClient.createPainting(
                                      new PaintingJson(
                                              null,
                                              title,
                                              description,
                                              museumJson,
                                              artistJson,
                                              null
                                      )
                              );
                          }
                  );
                  setPainting(paintingJson);
              });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
  }

  @Override
  public PaintingJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdPainting();
  }

  @Nullable
  public static PaintingJson createdPainting() {
    final ExtensionContext context = context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), PaintingJson.class);
  }


  public static void setPainting(PaintingJson painting) {
    final ExtensionContext context = context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        painting
    );
  }
}
