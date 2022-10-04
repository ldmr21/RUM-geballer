package jme3test.helloworld;

class OS {
    /**
     * Checks whether this program is running on a Mac.
     */
    static boolean isMac() {
        return System.getProperty("os.name").toUpperCase().contains("MAC");
    }
}
