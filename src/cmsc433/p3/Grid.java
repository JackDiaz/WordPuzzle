// DO NOT CHANGE THIS CLASS
package cmsc433.p3;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The <code>Grid</code> class is a matrix of letters which are intended 
 * to form words. Words are constructed as paths through the matrix. A 
 * solution to a grid is simply a list of (nonoverlapping) paths that 
 * correspond to words in a dictionary. The score of a solution depends
 * on the length of the word, and the frequency of words of that length
 * in the given dictionary.
 * 
 * This class is immutable, and so is thread-safe.
 */
public class Grid {
	private final char grid[][];

	private Grid(char grid[][]) {
		this.grid = grid;
	}

	/**
	 * This factory method constructs a <code>Grid</code> object from the 
	 * contents of the given file. The file is expected to formatted as a series of
	 * rows of characters; each row in the file corresponds to a row in the
	 * grid.
	 * 
	 * @param file - a text file containing (equal-length) rows of characters
	 * @return a <code>Grid</code> object representing the contents of the file
	 */
	public static Grid makeGrid(String file) {
		char[][] gridFromFile;
		ArrayList<String> linesFromFile = new ArrayList<String>();
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file);
			System.exit(1);
		}

		try {
			String line = fileReader.readLine();
			while (line != null) {
				linesFromFile.add(line);
				line = fileReader.readLine();
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("Error while file was open: " + file);
			System.exit(1);
		}

		gridFromFile = new char[linesFromFile.size()][linesFromFile.get(0).length()];
		for (int i = 0; i < linesFromFile.size(); ++i) {
			String line = linesFromFile.get(i);
			for (int j = 0; j < line.length(); ++j) {
				gridFromFile[i][j] = line.charAt(j);
			}
		}

		return new Grid(gridFromFile);
	}

	/**
	 * Provides the number of rows in this grid.
	 * @return the number of rows in the grid
	 */
	public int numRows() { return grid.length; }

	/**
	 * Provides the number of columns in this grid
	 * @return the number of columns in the grid
	 */
	public int numCols() { return grid[0].length; }

	/**
	 * Provides the character at the given row,col coordinates
	 * @param row - the row of the character
	 * @param col - the column of the character
	 * @return the character at (row,col)
	 */
	public char get(int row, int col) {
		return grid[row][col];
	}

	/**
	 * Returns the word corresponding to the given path. Assumes that the
	 * path consists of adjacent coordinates (must be checked in caller).
	 * @param path - a Path object
	 * @return the word that corresponds to that path in this grid
	 */
	public String getWord(Path path) {
		try {
			StringBuffer word = new StringBuffer("");
			for (Point p : path.getPoints()) {
				char c = get(p.row,p.col);
				word.append(c);
			}
			return word.toString();
		} catch (Throwable e) {
			return "";
		}
	}

	/**
	 * Computes the score of the given Solution. It does this by
	 * extracting each path from the solution and ensuring that (a)
	 * it is a legal path (i.e., all points are adjacent), (b) that 
	 * it does not overlap with any other paths already processed,
	 * and (c) that	it corresponds to an actual word in the dictionary.
	 * If these conditions are met, the word is scored according
	 * the dictionary's score() method and added to the total
	 * score.
	 *  
	 * @param dict - the dictionary of words to use for scoring
	 * @param sol - the solution to score
	 * @return the integer score value
	 */
	public int score(Dictionary dict, List<Path> sol) {
		int total = 0;
		boolean[][] mark = new boolean[numRows()][numCols()];
		for (int i = 0; i<numRows(); i++)
			for (int j = 0; j<numCols(); j++) 
				mark[i][j] = false;
		for (Path path : sol) {
			boolean ok = true;
			Point last = null;
			boolean[][] loop = new boolean[numRows()][numCols()];
			for (Point p : path.getPoints()) {
				// make sure we have not used this point before
				if (mark[p.row][p.col] || loop[p.row][p.col]) {
					ok = false;
					break;
				} else {
					// check that this point is next to the last one
					if (last == null) last = p;
					else {
						if (!last.isAdjacent(p)) {
							ok = false;
							break;
						}
					}
					loop[p.row][p.col]=true;
				}
				last = p;
			}
			// now get the word associated with this path and score it
			String word = getWord(path);
			if (ok && dict.score(word)>0) {
				total += dict.score(word);
				for(Point p : path.getPoints())
					mark[p.row][p.col] = true;
			}
			else
				total -= dict.getScore(word.length());
		}
		return total;
	}

	/**
	 * This method displays the grid using the Swing GUI
	 * library, along with a depiction of the given solution.
	 * @param sol
	 */
	public void display(Solution sol)
	{
		JFrame frame = new JFrame("Grid");
		JPanel panel = new JPanel(new GridLayout(numRows(),numCols()));
		JLabel[][] labels = new JLabel[numRows()][numCols()];
		boolean[][] visited = new boolean[numRows()][numCols()];

		for(int y=0;y<numRows();y++)
		{
			for(int x=0;x<numCols();x++)
			{
				labels[y][x] = new JLabel(grid[y][x]+"",JLabel.CENTER);
				panel.add(labels[y][x]);
			}
		}

		List<Path> paths = sol.getPaths();
		for(int i=0;i<paths.size();i++)
		{
			List<Point> points = paths.get(i).getPoints();
			for(int j=0;j<points.size();j++)
			{
				Point p = points.get(j);
				visited[p.row][p.col]=true;
				labels[p.row][p.col].setBackground(intToColor(i));
				labels[p.row][p.col].setForeground(intToColor(i));
				int borderWidth = 2;
				if(j==0)
					borderWidth = 4;
				if(grid.length>=40)
					borderWidth/=2;
				labels[p.row][p.col].setBorder(BorderFactory.createLineBorder(intToColor(i), borderWidth));
			}
		}

		frame.add(panel);
		frame.setSize(600*numCols()/numRows(),600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public Color intToColor(int id)
	{
		switch(id%9)
		{
		case 0: return Color.red;
		case 1: return Color.orange;
		case 2: return Color.yellow;
		case 3: return Color.green;
		case 4: return Color.cyan;
		case 5: return Color.blue;
		case 6: return Color.magenta;
		case 7: return Color.pink;
		case 8: return Color.lightGray;
		default: return Color.darkGray;
		}
	}
}



