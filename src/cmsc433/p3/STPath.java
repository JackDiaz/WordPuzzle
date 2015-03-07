package cmsc433.p3;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>STPath</code> class is a non-thread-safe implementation of the 
 * <code>Path</code> class.
 *
 */
public class STPath implements Path {
	private final ArrayList<Point> points;
	public STPath() { points = new ArrayList<Point>(); }
	public List<Point> getPoints() { 
		return points;
	}
	/**
	 * Adds a point to the end of this path.
	 */
	public void addPoint(Point p) {
		points.add(p);
	}
}