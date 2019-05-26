package roles;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@With(RestrictAnnotationAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * The interface containing the string value of the role we want to restrict on.
 */
public @interface RestrictAnnotation {

    String value() default "admin";
}