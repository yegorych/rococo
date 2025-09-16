package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.model.CountryEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museum {
    String title() default "";
    CountryEnum country() default CountryEnum.BELARUS;
    String city() default "";
    String description() default "";
}
