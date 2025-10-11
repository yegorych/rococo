package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.container.Museums;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.impl.db.MuseumDbClient;
import guru.qa.rococo.utils.ImgBase64Utils;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.*;

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
        List<MuseumJson> createdMuseums = new ArrayList<>();

        AnnotationSupport.findAnnotation(testMethod, Museums.class).ifPresent(
                museumRepeatable -> {
                    if (museumRepeatable.count() > 0) {
                        for (int i = 0; i < museumRepeatable.count(); i++) {
                            MuseumJson museumJson = museumClient.createMuseum(MuseumJson.randomMuseum());
                            createdMuseums.add(museumJson);
                        }
                    }
                }
        );

        List<Museum> museumAnnotations = AnnotationSupport.findRepeatableAnnotations(testMethod, Museum.class);
        for (Museum museumAnno : museumAnnotations) {
            final String title = museumAnno.title().isEmpty()
                    ? RandomDataUtils.randomMuseumTitle()
                    : museumAnno.title();

            createdMuseums
                    .stream()
                    .filter(m -> title.equals(m.title()))
                    .findFirst()
                    .orElseGet(() -> {
                                final String description = museumAnno.description().isEmpty()
                                        ? RandomDataUtils.randomSentence(50)
                                        : museumAnno.description();

                                final String city = museumAnno.city().isEmpty()
                                        ? RandomDataUtils.randomCity()
                                        : museumAnno.city();

                                final String photo = museumAnno.photo().isEmpty()
                                        ? null
                                        : ImgBase64Utils.imageToBase64(museumAnno.photo());


                                final CountryEnum country = museumAnno.country();

                                MuseumJson mj = museumClient.createMuseum(
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
                                                photo
                                        )
                                );
                                createdMuseums.add(mj);
                                return mj;
                            }
                    );
        }
        setMuseums(createdMuseums);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson[].class);
    }

    @Override
    public MuseumJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdMuseums().toArray(MuseumJson[]::new);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static List<MuseumJson> createdMuseums() {
        final ExtensionContext context = context();
        return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
                .orElse(Collections.emptyList());
    }


    public static void setMuseums(List<MuseumJson> museums) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museums
        );
    }
}
