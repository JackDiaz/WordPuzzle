package cmsc433.p3;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>STSolution</code> class is a non-thread-safe implementation
 * of the Solution interface.
 *
 */
public class STSolution implements Solution {
	private final ArrayList<Path> paths;
	public STSolution() { paths = new ArrayList<Path>(); }
	public List<Path> getPaths() {
		return paths;
	}
	public void addPath(Path p) {
		paths.add(p);
	}
}