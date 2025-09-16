package guru.qa.rococo.utils;

import com.github.javafaker.Faker;


import javax.annotation.Nonnull;
import java.util.Locale;

public class RandomDataUtils {
    private static final Faker faker = new Faker();
    private static final Faker fakerRu = new Faker(Locale.of("ru", "RU"));

    @Nonnull
    public static String randomUsername(){
        return faker.name().username();
    }
    @Nonnull
    public static String randomPassword(){
        return faker.internet().password();
    }
    @Nonnull
    public static String randomName(){
        return faker.name().firstName();
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
        return fakerRu.address().country();
    }

    @Nonnull
    public static String randomArtistName(){
        return fakerRu.artist().name();
    }


    @Nonnull
    public static String randomSentence(int wordsCount){
        return faker.lorem().sentence(wordsCount);
    }
}
