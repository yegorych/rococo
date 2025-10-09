package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.annotation.container.Paintings;

import java.lang.annotation.*;

@Repeatable(Paintings.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Painting {
    String title() default "";
    String description() default "";
    String photo() default "";
    Artist artist() default @Artist;
    Museum museum() default @Museum(createMuseum = false);
}
