package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.service.impl.MuseumDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.Objects;

import static guru.qa.rococo.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class ArtistExtension implements
    BeforeEachCallback,
    ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistClient artistClient = new ArtistDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
      Method testMethod = context.getRequiredTestMethod();
      AnnotationSupport.findAnnotation(testMethod, Artist.class)
              //.or(() -> AnnotationSupport.findAnnotation(testMethod, Painting.class).map(Painting::artist))
              .ifPresent(artistAnno -> {
                  final String name = artistAnno.name().isEmpty()
                          ? RandomDataUtils.randomArtistName()
                          : artistAnno.name();

                  ArtistJson artistJson = Objects.requireNonNullElseGet(
                          artistClient.findArtistByName(name),
                          () -> {
                              final String biography = artistAnno.biography().isEmpty()
                                      ? RandomDataUtils.randomSentence(50)
                                      : artistAnno.biography();

                              return artistClient.createArtist(
                                      new ArtistJson(
                                              null,
                                              name,
                                              biography,
                                              null
                                      )
                              );
                          }
                  );
                  setArtist(artistJson);
              });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
  }

  @Override
  public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdArtist();
  }

  @Nullable
  public static ArtistJson createdArtist() {
    final ExtensionContext context = context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), ArtistJson.class);
  }


  public static void setArtist(ArtistJson artist) {
    final ExtensionContext context = context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        artist
    );
  }
}
