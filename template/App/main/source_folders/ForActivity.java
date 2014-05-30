
package {package_name};

import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Identifies {@link android.app.Activity} scoped dependencies. */
@Qualifier @Retention(RUNTIME)
public @interface ForActivity {
}