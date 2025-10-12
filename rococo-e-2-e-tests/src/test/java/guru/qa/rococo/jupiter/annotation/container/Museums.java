package guru.qa.rococo.jupiter.annotation.container;

import guru.qa.rococo.jupiter.annotation.Museum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museums {
    Museum[] value() default {};
    int count() default 0;

}
