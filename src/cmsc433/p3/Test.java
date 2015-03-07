package cmsc433.p3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * The <code>Test</code> class is for testing the grid solver.
 * It will take as arguments a grid, a dictionary, and time
 * limit, and compute a solution to that grid that is compatible
 * with the given dictionary, within the given time limit.
 */
public class Test {
	private static final String GRIDNAME = "public_grid_30x30r.txt";
	private static final String DICTNAME = "dict_animals.txt";

	private JLabel[][] leftLabels;
	private JLabel[][] rightLabels;
	private JSlider timeSlider;
	private JLabel leftRuntimeLabel;
	private JLabel leftScoreLabel;
	private JCheckBox checkBox;
	private JLabel rightRuntimeLabel;
	private JLabel rightScoreLabel;

	public static void main(String[] args)
	{

		Dictionary dict;
		Grid board;
		if (args.length > 0) {
			dict = Dictionary.makeDict(args[0]);
		} else {
			dict = Dictionary.makeDict(DICTNAME);
		}	
		if (args.length > 1) {
			board = Grid.makeGrid(args[1]);
		} else {
			board = Grid.makeGrid(GRIDNAME);
		}
		new Test(dict, board);
	}

	public Test(Dictionary dict, Grid board)
	{
		displayGui(dict,board);
	}

	public void displayGui(Dictionary dict, Grid board)
	{
		int numRows = board.numRows();
		int numCols = board.numCols();

		JFrame frame = new JFrame("Grid");
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel gridPanel = new JPanel(new GridLayout(1,2));
		JPanel leftGridPanel = new JPanel(new GridLayout(numRows,numCols));
		JPanel rightGridPanel = new JPanel(new GridLayout(numRows,numCols));
		JPanel buttonPanel = new JPanel(new GridLayout(2,1));
		JPanel runPanel = new JPanel(new GridLayout(1,6));
		leftLabels = new JLabel[numRows][numCols];
		rightLabels = new JLabel[numRows][numCols];

		for(int y=0;y<numRows;y++)
		{
			for(int x=0;x<numCols;x++)
			{
				leftLabels[y][x] = new JLabel(board.get(y,x)+"",JLabel.CENTER);
				rightLabels[y][x] = new JLabel(board.get(y,x)+"",JLabel.CENTER);
				leftGridPanel.add(leftLabels[y][x]);
				rightGridPanel.add(rightLabels[y][x]);
			}
		}
		leftGridPanel.setBorder(BorderFactory.createTitledBorder("Your Solution"));
		rightGridPanel.setBorder(BorderFactory.createTitledBorder("Single Threaded Solution"));
		gridPanel.add(leftGridPanel);
		gridPanel.add(rightGridPanel);

		timeSlider = new JSlider();
		timeSlider.setMaximum(500);
		timeSlider.setMinimum(0);
		timeSlider.setMajorTickSpacing(100);
		timeSlider.setMinorTickSpacing(10);
		timeSlider.setValue(200);
		timeSlider.setPaintTicks(true);
		timeSlider.setPaintLabels(true);
		timeSlider.setSnapToTicks(true);
		timeSlider.setBorder(BorderFactory.createTitledBorder("Time limit: "));

		leftRuntimeLabel = new JLabel("0",JLabel.CENTER);
		leftRuntimeLabel.setBorder(BorderFactory.createTitledBorder("Your Run Time: "));
		leftScoreLabel = new JLabel("0",JLabel.CENTER);
		leftScoreLabel.setBorder(BorderFactory.createTitledBorder("Your Score: "));
		rightRuntimeLabel = new JLabel("0",JLabel.CENTER);
		rightRuntimeLabel.setBorder(BorderFactory.createTitledBorder("ST Run Time: "));
		rightScoreLabel = new JLabel("0",JLabel.CENTER);
		rightScoreLabel.setBorder(BorderFactory.createTitledBorder("ST Score: "));
		JButton solveButton = new JButton("Solve!");
		solveButton.addActionListener(new ButtonAction(dict, board));
		checkBox = new JCheckBox("Run ST Solver");
		runPanel.add(solveButton);
		runPanel.add(leftRuntimeLabel);
		runPanel.add(leftScoreLabel);
		runPanel.add(checkBox);
		runPanel.add(rightRuntimeLabel);
		runPanel.add(rightScoreLabel);

		buttonPanel.add(timeSlider);
		buttonPanel.add(runPanel);

		gridPanel.setSize(500*2, 500);
		mainPanel.add(gridPanel,BorderLayout.CENTER);
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		frame.add(mainPanel);
		frame.setSize(500*numCols/numRows*2,500+100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void displayBoard(Dictionary dict, Grid board, Solution sol, Solution solST)
	{
		//reset colors
		for(int i=0;i<leftLabels.length;i++)
		{
			for(int j=0;j<leftLabels[0].length;j++)
			{
				leftLabels[i][j].setBorder(null);
				leftLabels[i][j].setForeground(Color.black);
				if(solST!=null)
				{
					rightLabels[i][j].setBorder(null);
					rightLabels[i][j].setForeground(Color.black);
				}
			}
		}

		//paint paths
		List<Path> paths = sol.getPaths();
		for(int i=0;i<paths.size();i++)
		{
			List<Point> points = paths.get(i).getPoints();
			for(int j=0;j<points.size();j++)
			{
				Point p = points.get(j);
				leftLabels[p.row][p.col].setForeground(intToColor(i));
				int borderWidth = 1;
				if(j==0)
					borderWidth *= 2;
				leftLabels[p.row][p.col].setBorder(BorderFactory.createLineBorder(intToColor(i), borderWidth));
			}
		}
		if(solST!=null)
		{
			paths = solST.getPaths();
			for(int i=0;i<paths.size();i++)
			{
				List<Point> points = paths.get(i).getPoints();
				for(int j=0;j<points.size();j++)
				{
					Point p = points.get(j);
					rightLabels[p.row][p.col].setForeground(intToColor(i));
					int borderWidth = 1;
					if(j==0)
						borderWidth *= 2;
					rightLabels[p.row][p.col].setBorder(BorderFactory.createLineBorder(intToColor(i), borderWidth));
				}
			}
		}
	}

	private class ButtonAction implements ActionListener
	{
		private Dictionary dict;
		private Grid board;

		public ButtonAction(Dictionary dict, Grid board)
		{
			this.dict = dict;
			this.board = board;
		}

		public void actionPerformed(ActionEvent e)
		{
			long startTime = System.currentTimeMillis();
			Solution sol = GridSolver.solve(board, dict, timeSlider.getValue());
			long stopTime = System.currentTimeMillis();
			List<Path> paths = sol.getPaths();
			leftRuntimeLabel.setText(Long.toString(stopTime-startTime));			
			leftScoreLabel.setText(Integer.toString(board.score(dict, paths)));
			startTime = System.currentTimeMillis();
			Solution solST = null;
			if(checkBox.isSelected())
			{
				solST = STGridSolver.solve(board, dict, timeSlider.getValue());
				List<Path> pathsST = solST.getPaths();
				stopTime = System.currentTimeMillis();
				rightRuntimeLabel.setText(Long.toString(stopTime-startTime));
				rightScoreLabel.setText(Integer.toString(board.score(dict, pathsST)));
			}
			displayBoard(dict, board, sol, solST);
		}
	}

	public static Color intToColor(int id)
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
