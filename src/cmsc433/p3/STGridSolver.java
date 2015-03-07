package cmsc433.p3;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.HashMap;

public class STGridSolver {
	static boolean timeUp(long startTime, long timeMS) {
		return (System.currentTimeMillis() - startTime > timeMS);
	}
	
	static Solution solve(Grid board, Dictionary dict, long timeMS) {
		long startTime = System.currentTimeMillis();
		Solution solution = new STSolution();
		ArrayList<Point> filled = new ArrayList<Point>();
		for (String word : dict) {
			if (timeUp(startTime, timeMS))
				return solution;
			
			boolean foundWord = false;
			for (int row = 0; row < board.numRows(); ++row) {
				for (int col = 0; col < board.numCols(); ++col) {
					if (board.get(row, col) == word.charAt(0) && !(filled.contains(new Point(row, col)))) {
						ArrayDeque<Point> currentPath = new ArrayDeque<Point>();
						HashMap<Point, ArrayList<Point>> marked = new HashMap<Point, ArrayList<Point>>();
						Point first = new Point(row, col);
						currentPath.push(first);
						marked.put(first, new ArrayList<Point>());
						marked.get(first).add(first);
						while (currentPath.size() != word.length() && !(currentPath.isEmpty())) {
							if (timeUp(startTime, timeMS))
								return solution;
							
							ArrayList<Point> toConsider = new ArrayList<Point>();
							Point current = currentPath.getFirst();
							for (int rowChange = -1; rowChange <= 1; ++rowChange) {
								for (int colChange = -1; colChange <= 1; ++colChange) {
									int rowCoord = current.row + rowChange;
									int colCoord = current.col + colChange;
									if ( (0 <= rowCoord) && (rowCoord < board.numRows()) && (0 <= colCoord) && (colCoord < board.numCols()) )
										toConsider.add(new Point(rowCoord, colCoord));
								}
							}

							Point next = null;
							for (Point p : toConsider) {
								if (!(filled.contains(p) || marked.get(current).contains(p) || currentPath.contains(p))) {
									next = p;
									break;
								}
							}

							if (next == null) {
								currentPath.pop();
								continue;
							}

							marked.get(current).add(next);
							if (board.get(next.row, next.col) == word.charAt(currentPath.size())) {
								currentPath.push(next);
								marked.put(next, new ArrayList<Point>());
								marked.get(next).add(current);
							}
						}

						if (currentPath.size() == word.length()) {
							STPath path = new STPath();
							Iterator<Point> iter = currentPath.descendingIterator();
							while (iter.hasNext()) {
								Point p = iter.next();
								path.addPoint(p);
								filled.add(p);
							}

							solution.addPath(path);
							foundWord = true;
							break;
						}
					}
				}

				if (foundWord)
					continue;
			}
		}

		return solution;
	}
}