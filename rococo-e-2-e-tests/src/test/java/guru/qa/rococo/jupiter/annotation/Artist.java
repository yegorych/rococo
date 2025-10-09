package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.annotation.container.Artists;

import java.lang.annotation.*;

@Repeatable(Artists.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Artist {
    String name() default "";
    String biography() default "";
    String photo() default "";
    int paintings() default 0;
}
