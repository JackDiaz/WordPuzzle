// DO NOT CHANGE THIS CLASS

package cmsc433.p3;
import java.util.List;

/**
 * The <code>Solution</code> interface represents a solution to a Grid. It
 * consists of a list of paths, which should correspond to words
 * in the grid that appear in a given dictionary.
 *
 */
public interface Solution {

	/**
	 * Returns the list of paths that comprise this solution.
	 * @return the solution's list of paths
	 */
	List<Path> getPaths();
	
	/**
	 * Adds a path to this solution
	 * @param p - the path to add
	 */
	void addPath(Path p);

}