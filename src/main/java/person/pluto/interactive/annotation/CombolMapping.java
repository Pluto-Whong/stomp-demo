package person.pluto.interactive.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 组合映射
 * </p>
 *
 * @since 2020-03-30 13:55:13
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@RequestMapping
@MessageMapping
public @interface CombolMapping {

    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] request() default {};

    @AliasFor(annotation = MessageMapping.class, attribute = "value")
    String[] message() default {};

}
