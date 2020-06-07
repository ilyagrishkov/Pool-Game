package nl.tudelft.cse.sem.server.http.endpoint;

import io.javalin.http.HandlerType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark service endpoints. User for automatic adding of endpoints.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
    /**
     * HTTP method to use for this service.
     *
     * @return The HTTP method
     */
    HandlerType method() default HandlerType.GET;

    /**
     * The path of the service.
     *
     * @return The path
     */
    String path();
}
