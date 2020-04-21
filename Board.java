// Board.java
import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private int maxHeight;
	private boolean[][] grid;
	private boolean DEBUG = true;
	private boolean committed;
	private  SavedBoard lastState;
	private int[] heights;
	private int[] widths;
	// Here a few trivial methods are provided:
	private class SavedBoard {
		public int width;
		public int height;
		public int maxHeight;
		public boolean[][] grid;
		public boolean committed;
		public int[] widths;
		public int[] heights;
		
		public SavedBoard(int width, int height)
		{
			this.width = width;
			this.height = height;
			widths = new int[height];
			heights = new int[width];
			grid = new boolean[getHeight()][getHeight()];
			maxHeight=0;
			editBoard();
		}
		
		private void editBoard() {
			for(int i=0;i<grid.length;i++) {
				for(int j=0;j<grid[i].length;j++) {
					grid[i][j]=false;
				}
			}
			for(int i=0;i<heights.length;i++) {
				heights[i]=0;
			}
			for(int i=0;i<widths.length;i++) {
				widths[i]=0;
			}
		}
	}
	private class TetrisGrid {
		private boolean[][] grid;
		/**
		 * Constructs a new instance with the given grid.
		 * Does not make a copy.
		 * @param grid
		 */
		public TetrisGrid(boolean[][] grid) {
			this.grid= new boolean[grid[0].length][grid.length];
			for(int i=0;i<grid.length;i++) {
				for(int j=0;j<grid[i].length;j++) {
					this.grid[this.grid.length-1-j][i]=grid[i][j];
				}
			}

		}
		
		/**
		 * Does row-clearing on the grid (see handout).
		 */
		public int clearRows() {
			int count=0;
			boolean helper;
			for(int i=0;i<grid.length;i++) {
				helper=true;
				for(int j=0;j<grid[i].length;j++) {
					if(grid[i][j]==false) {
						helper=false;
					}
				}
				if(helper) {
					removeRow(i);
					count++;
				}
			}
			return count;
		}
		
		private void removeRow(int row) {
			for(int i=row;i>0;i--) {
				for(int j=0;j<grid[i].length;j++) {
					grid[i][j]=grid[i-1][j];
				}
			}
			for(int i=0;i<grid[0].length;i++) {
				grid[0][i]=false;
			}
		}


		/**
		 * Returns the internal 2d grid array.
		 * @return 2d grid array
		 */
		boolean[][] getGrid() {
			boolean[][] result= new boolean[grid[0].length][grid.length];
			for(int i=0;i<result.length;i++) {
				for(int j=0;j<result[i].length;j++) {
					result[i][j]=grid[this.grid.length-1-j][i];
				}
			}
//			for(int i=0;i<result.length;i++) {
//				for(int j=0;j<result[i].length;j++) {
//					System.out.print(result[i][j]+" ");
//					}
//				System.out.println();
//				}
			return result; // YOUR CODE HERE
		}
	}
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.maxHeight=0;
		this.grid = new boolean[width][height];
		this.widths=new int[height];
		this.heights=new int[width];
		committed = true;
		editBoard();
		lastState=new SavedBoard(width,height);
		// YOUR CODE HERE
	}
	
	
	private void editBoard() {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				grid[i][j]=false;
			}
		}
		for(int i=0;i<heights.length;i++) {
			heights[i]=0;
		}
		for(int i=0;i<widths.length;i++) {
			widths[i]=0;
		}
	}


	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		return maxHeight; // YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			boolean result;
			result=checkHeights();
			if(!result)
				throw new RuntimeException("Hights check revealed a flaw one or more elements are incorect");
			// YOUR CODE HERE
			result=checkWidths();
			if(!result)
				throw new RuntimeException("Widths check revealed a flaw one or more elements are incorect");
			result=checkMaxHeight();
			if(!result)
				throw new RuntimeException("MaxHeight check revealed that it is incorect");
		}
	}
	
	private boolean checkMaxHeight() {
		// TODO Auto-generated method stub
		boolean b=false;
		for(int i=0;i<width;i++) {
			if(maxHeight<heights[i]) {
				return false;
			}else {
				if(heights[i]==maxHeight)
					b=true;
			}
		}
		return b;
	}


	private boolean checkWidths() {
		boolean b=true;;
		for(int i=0;i<height;i++) {
			int counter=0;
			for(int j=0;j<width;j++) {
				if(grid[j][i]) {
					counter++;
				}
			}
			if(counter!=widths[i]) {
				b=false;
				break;
			}
		}
		return b;
	}


	private boolean checkHeights() {
		boolean b=true;;
		for(int i=0;i<width;i++) {
			int counter=0;
			for(int j=0;j<height;j++) {
				if(grid[i][j]) {
					counter=j+1;
				}
			}
			if(counter!=heights[i]) {
				b=false;
				break;
			}
		}
		return b;
	}


	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int landY=0;
		int[] skirt=piece.getSkirt();
		for(int i=0;i<skirt.length;i++) {
			int blockland=Math.max(0,this.heights[i+x] - skirt[i] );
			if(blockland>landY)
				landY=blockland;
		}
		return landY; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widths[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		return grid[x][y]; // YOUR CODE HERE
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
			saveLastState();
			committed=false;
		int result = PLACE_OK;
		if(checkPieceOutOfBounds(piece,x,y)) {
			return PLACE_OUT_BOUNDS;
		}
		TPoint[] body=piece.getBody();
		for(int i=0;i<body.length;i++) {
			TPoint helper=new TPoint(body[i].x+x,body[i].y+y);
			if(getGrid(helper.x, helper.y)) {
				return PLACE_BAD;
			}
			grid[helper.x][helper.y]=true;
			heights[helper.x]=Math.max(heights[helper.x], helper.y+1);
			if(heights[helper.x] > maxHeight) {
				maxHeight=heights[helper.x];
			}
			widths[helper.y]=widths[helper.y]+1;
			if(widths[helper.y]==width) {
				result=PLACE_ROW_FILLED;
			}
		}
		sanityCheck();
		return result;
	}
	
	
	private void saveLastState() {
		// TODO Auto-generated method stub
		lastState.width = width;
		lastState.height = height;
		lastState.maxHeight = maxHeight;

		//copy arrays
		System.arraycopy(widths, 0, lastState.widths, 0, widths.length);
		System.arraycopy(heights, 0, lastState.heights, 0, heights.length);
		//deep copy
		for (int i=0; i < getWidth(); i++)
			System.arraycopy(grid[i], 0, lastState.grid[i], 0, grid[i].length);
		
	}


	private boolean checkPieceOutOfBounds(Piece piece, int x, int y) {
		// TODO Auto-generated method stub
		if (piece.getWidth() + x > this.getWidth() ||piece.getHeight() + y > this.getHeight() ||x < 0 || y < 0)
				return true;
			else
				return false;
	}


	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if(committed) {
			saveLastState();
			committed=false;
		}
		int rowsCleared = 0;
		TetrisGrid t=new TetrisGrid(grid);
		rowsCleared=t.clearRows();
		grid=t.getGrid();
		editHightsWidths();
		sanityCheck();
		return rowsCleared;
	}



	private void editHightsWidths() {
		int max=0;
		for(int i=0;i<width;i++) {
			int counter=0;
			for(int j=0;j<height;j++) {
				if(grid[i][j]) {
					counter=j+1;
				}
			}
			heights[i]=counter;
			if(counter>max)
				max=counter;
		}
		maxHeight=max;
		for(int i=0;i<height;i++) {
			int counter=0;
			for(int j=0;j<width;j++) {
				if(grid[j][i]) {
					counter++;
				}
			}
			widths[i]=counter;
		}
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		// YOUR CODE HERE
		if (!committed)
		{
			boolean[][] helpergrid = grid;
			int[] helper = widths;
			committed = true;
			width = lastState.width;
			height = lastState.height;
			maxHeight = lastState.maxHeight;
			grid = lastState.grid;
			lastState.grid = helpergrid;
			widths = lastState.widths;
			lastState.widths = helper;
			helper = heights;
			heights = lastState.heights;
			lastState.heights = helper;
		}
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
		saveLastState();
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


