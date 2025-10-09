package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.model.CountryEnum;


import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.StringUtils.truncate;

public class RandomDataUtils {
    private static final Faker faker = new Faker();
    private static final Faker fakerRu = new Faker(Locale.of("ru", "RU"));

    @Nonnull
    public static String randomUsername() {
        return faker.name().username() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    @Nonnull
    public static String randomPassword(){
        return faker.internet().password();
    }
    @Nonnull
    public static String randomName(){
        return faker.name().name() + UUID.randomUUID();
    }
    @Nonnull
    public static String randomSurname(){
        return faker.name().lastName();
    }
    @Nonnull
    public static String randomMuseumTitle(){
        return faker.address().cityName() + " Museum of " + faker.book().genre();
    }

    @Nonnull
    public static String randomCity(){
        return fakerRu.address().cityName();
    }

    @Nonnull
    public static String randomCountry(){
        return CountryEnum.randomCountry().getCountryName();
    }

    @Nonnull
    public static String randomSentence(int wordsCount){
        return faker.lorem().sentence(wordsCount);
    }

    @Nonnull
    public static String randomWord(int lettersCount){
        return faker.lorem().characters(lettersCount);
    }


}
