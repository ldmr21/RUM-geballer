package pp.npc;

/**
 * This class implements the behavior of a non-player character.
 */
public class NonPlayerCharacterBehavior {

    /**
     * The npc whose behavior is implemented.
     */
    private final NonPlayerCharacter npc;

    /**
     * Creates a new npc-behavior
     *
     * @param npc the npc to use the behavior
     */
    public NonPlayerCharacterBehavior(NonPlayerCharacter npc) {
        this.npc = npc;
    }

    /**
     * Specifies the actual behavior.
     *
     * @param delta time in seconds since the last update
     */
    public void update(float delta) {
        // missing implementation
    }
}
