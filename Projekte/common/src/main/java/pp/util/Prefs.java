package pp.util;

import java.util.prefs.Preferences;

/**
 * A class with convenience methods for preferences.
 */
public class Prefs {
    private Prefs() {
        // don't instantiate
    }

    /**
     * Returns a preferences node for the specified class object. The path of the
     * preference node corresponds to the fully qualified name of the class.
     *
     * @param clazz a class object
     * @return a preference node for the calling user
     */
    public static Preferences getPreferences(Class<?> clazz) {
        return Preferences.userNodeForPackage(clazz).node(clazz.getSimpleName());
    }
}
