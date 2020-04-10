package person.pluto.interactive.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 当前用户信息注解
 * </p>
 *
 * @author Pluto
 * @since 2019-03-28 11:01:30
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUserId {
}
