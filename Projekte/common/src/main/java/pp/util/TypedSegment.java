package pp.util;

/**
 * A directed straight line segment between two positions in the plane and with a String type.
 * This type may be used for any purpose.
 *
 * @param from the start position of the segment
 * @param to   the end position of the segment
 * @param cat  the segment type
 */
public record TypedSegment(Position from, Position to, String cat) implements SegmentLike {}
