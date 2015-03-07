// DO NOT CHANGE THIS CLASS

package cmsc433.p3;

/**
 * A <code>Point</code> is a coordinate on the character Grid.
 * All points must be non-negative.
 * 
 * This class is immutable, and therefore thread-safe.
 *
 */
public class Point {
	public final int row;
	public final int col;
	
	/**
	 * Constructs a point. 
	 * @param row - must be non-negative
	 * @param col - must be non-negative
	 * @throws IllegalArgumentException on negative inputs
	 */
	public Point(int row, int col) throws IllegalArgumentException {
		if (row < 0 || col < 0) throw new IllegalArgumentException("illegal value");
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Determines whether the given point is adjacent to
	 * this point.
	 * @param p - the point to check adjacency against
	 * @return whether this point is adjacent to p
	 */
	public boolean isAdjacent(Point p) {
		int deltarow = p.row - this.row;
		int deltacol = p.col - this.col;
		return ((deltarow >= -1 && deltarow <= 1) &&
				(deltacol >= -1 && deltacol <= 1));
	}

	public boolean equals(Object o) {
    	if (o instanceof Point) {
    		Point p = (Point) o;
    		return (p.row == row && p.col == col);
    	}
    	return false;
    }
	
	public int hashCode() {
		return (row<<16 + col);
	}
	
	public String toString()
	{
		return "("+row+","+col+")";
	}
}
