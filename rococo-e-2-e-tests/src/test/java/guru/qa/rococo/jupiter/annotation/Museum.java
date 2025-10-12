package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.annotation.container.Museums;
import guru.qa.rococo.model.CountryEnum;

import java.lang.annotation.*;


@Repeatable(Museums.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museum {
    String title() default "";
    CountryEnum country() default CountryEnum.BELARUS;
    String city() default "";
    String description() default "";
    String photo() default "";
    boolean createMuseum() default true;
}
