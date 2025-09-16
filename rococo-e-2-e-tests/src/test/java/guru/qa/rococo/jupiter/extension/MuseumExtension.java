package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
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
public class MuseumExtension implements
    BeforeEachCallback,
    ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    private final MuseumClient museumClient = new MuseumDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
      Method testMethod = context.getRequiredTestMethod();
      AnnotationSupport.findAnnotation(testMethod, Museum.class)
              //.or(() -> AnnotationSupport.findAnnotation(testMethod, Painting.class).map(Painting::museum))
              .ifPresent(museumAnno -> {
                  final String title = museumAnno.title().isEmpty()
                          ? RandomDataUtils.randomMuseumTitle()
                          : museumAnno.title();

                  MuseumJson museumJson = Objects.requireNonNullElseGet(
                          museumClient.findMuseumByTitle(title),
                          () -> {
                              final String description = museumAnno.description().isEmpty()
                                      ? RandomDataUtils.randomSentence(50)
                                      : museumAnno.description();

                              final String city = museumAnno.city().isEmpty()
                                      ? RandomDataUtils.randomCity()
                                      : museumAnno.city();

                              final CountryEnum country = museumAnno.country();

                              return museumClient.createMuseum(
                                      new MuseumJson(
                                              null,
                                              title,
                                              description,
                                              new GeoJson(
                                                      city,
                                                      new CountryJson(
                                                              null,
                                                              country
                                                      )
                                              ),
                                              null
                                      )
                              );
                          }
                  );
                  setMuseum(museumJson);
              });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
  }

  @Override
  public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdMuseum();
  }

  @Nullable
  public static MuseumJson createdMuseum() {
    final ExtensionContext context = context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), MuseumJson.class);
  }


  public static void setMuseum(MuseumJson museum) {
    final ExtensionContext context = context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        museum
    );
  }
}
