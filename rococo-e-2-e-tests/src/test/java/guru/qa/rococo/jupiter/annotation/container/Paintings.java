package guru.qa.rococo.jupiter.annotation.container;

import guru.qa.rococo.jupiter.annotation.Painting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Paintings {
    int count() default 0;
    Painting[] value() default {};

}
