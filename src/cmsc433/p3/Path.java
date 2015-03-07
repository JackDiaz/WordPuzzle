// DO NOT CHANGE THIS CLASS
package cmsc433.p3;
import java.util.List;

/**
 * A <code>Path</code> interface represents a path through the grid, 
 * and consists of a list of points.
 *
 */
public interface Path {

	/**
	 * Returns the list of points that define this path.
	 * @return the list of points defining this path
	 */
	List<Point> getPoints();
	
	/**
	 * Adds a point to the end of the path
	 * @param p - the point to add
	 */
	void addPoint(Point p);

}