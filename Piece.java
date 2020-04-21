// Piece.java

import java.util.*;
import java.lang.String; 
/**
 An immutable representation of a tetris piece in a particular rotation.
 Each piece is defined by the blocks that make up its body.
 
 Typical client code looks like...
 <pre>
 Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 int width = pyra.getWidth();			// 3
 Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way
 
 Piece[] pieces = Piece.getPieces();	// the array of root pieces
 Piece stick = pieces[STICK];
 int width = stick.getWidth();		// get its width
 Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 </pre>
*/
public class Piece {
	// Starter code specs out a few basic things, leaving
	// the algorithms to be done.
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation

	static private Piece[] pieces;	// singleton static array of first rotations
	
	
	private boolean[][] rotatedGrid(boolean[][] grid) {
		boolean[][] rotatedgrid= new boolean[grid[0].length][grid.length];
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				rotatedgrid[rotatedgrid.length-1-j][i]=grid[i][j];
			}
		}
		return rotatedgrid;
	}
	private boolean[][] makeABoolGrid() {
		boolean[][] grid=new boolean[width][height];
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				grid[i][j]=false;
			}
		}
		for(int i=0;i<body.length;i++) {
			grid[body[i].x][body[i].y]=true;
		}
		return grid;
	}
	private void findMesurments() {
		if(body.length>0) {
			int maxX=body[0].x;
			int maxY=body[0].y;
			int minX=body[0].x;
			int minY=body[0].y;
			for(int i=0;i<body.length;i++) {
				 if(body[i].y>maxY) {
					 maxY=body[i].y;
				 }else {
					 if(minY>body[i].y) {
						 minY=body[i].y;
					 }
				 }
			 	if(body[i].x>maxX) {
			 		maxX=body[i].x;
			 	}else {
				 	if(minX>body[i].x) {
				 		minX=body[i].x;
				 		}
			 	}
			}
			
			width=maxX-minX+1;
			height=maxY-minY+1;
		 }
	}
	/**
	 Defines a new piece given a TPoint[] array of its body.
	 Makes its own copy of the array and the TPoints inside it.
	*/
	public Piece(TPoint[] points) {
		this.body=points;
		findMesurments();
		valueSkirt();
		// YOUR CODE HERE
	}
	

	
	
	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {
		String[] str=points.split("\\s+");
		body=new TPoint[str.length/2];
		int curent=0;
		for(int i=0;i<str.length-1;i++) {
			int x=Integer.parseInt(str[i]);
			i++;
			int y=Integer.parseInt(str[i]);
			TPoint h=new TPoint(x,y);
			body[curent]=h;
			curent++;
		}
		findMesurments();
		valueSkirt();
	}

	private void valueSkirt() {
		// TODO Auto-generated method stub
		skirt=new int[width];
		for(int i=0;i<skirt.length;i++) {
			int min=-1;
			for(int j=0;j<body.length;j++) {
				if(body[j].x==i) {
					if(min==-1 || body[j].y<min) {
						min=body[j].y;
					}
				}
			}
			skirt[i]=min;
		}
	}
	/**
	 Returns the width of the piece measured in blocks.
	*/
	public int getWidth() {
		return width;
	}

	/**
	 Returns the height of the piece measured in blocks.
	*/
	public int getHeight() {
		return height;
	}

	/**
	 Returns a pointer to the piece's body. The caller
	 should not modify this array.
	*/
	public TPoint[] getBody() {
		return body;
	}

	/**
	 Returns a pointer to the piece's skirt. For each x value
	 across the piece, the skirt gives the lowest y value in the body.
	 This is useful for computing where the piece will land.
	 The caller should not modify this array.
	*/
	public int[] getSkirt() {
		return skirt;
	}

	
	/**
	 Returns a new piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		// YOUR CODE HERE
		if(next!=null)
			return next;
		boolean[][] grid=makeABoolGrid();
		grid=rotatedGrid(grid);
		TPoint[] rotated=new TPoint[body.length];
		int current=0;
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				if(grid[i][j]) {
					TPoint h=new TPoint(i,j);
					rotated[current]=h;
					current++;
				}
			}
		}
		Piece p=new Piece(rotated);
		if(next==null) {
			next=p;
		}
			return next; 
			// YOUR CODE HERE
	}

	/**
	 Returns a pre-computed piece that is 90 degrees counter-clockwise
	 rotated from the receiver.	 Fast because the piece is pre-computed.
	 This only works on pieces set up by makeFastRotations(), and otherwise
	 just returns null.
	*/	
	public Piece fastRotation() {
		getPieces();
		for(int i=0;i<pieces.length;i++) {
			if(this.equals(pieces[i]));
		}
		return next;
	}
	


	/**
	 Returns true if two pieces are the same --
	 their bodies contain the same points.
	 Interestingly, this is not the same as having exactly the
	 same body arrays, since the points may not be
	 in the same order in the bodies. Used internally to detect
	 if two rotations are effectively the same.
	*/
	public boolean equals(Object obj) {
		Piece p=(Piece)obj;
		if(this.body.length==p.getBody().length) {
			TPoint[] pbody=p.getBody();
			int counter=0;
			for(int i=0;i<body.length;i++) {
				for(int j=0;j<body.length;j++) {
					if(body[i].equals(pbody[j])) {
						counter++;
						break;
					}
				}
			}
			if(counter==body.length) {
				if(p.fastRotation()!=null)
					next=p.computeNextRotation();
				return true;
			}else
				return false;
		}
		else 
			return false;
	}


	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";
	
	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;
	
	/**
	 Returns an array containing the first rotation of
	 each of the 7 standard tetris pieces in the order
	 STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
	 The next (counterclockwise) rotation can be obtained
	 from each piece with the {@link #fastRotation()} message.
	 In this way, the client can iterate through all the rotations
	 until eventually getting back to the first rotation.
	 (provided code)
	*/
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		
		
		return Piece.pieces;
	}
	


	/**
	 Given the "first" root rotation of a piece, computes all
	 the other rotations and links them all together
	 in a circular list. The list loops back to the root as soon
	 as possible. Returns the root piece. fastRotation() relies on the
	 pointer structure setup here.
	*/
	/*
	 Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	*/
	private static Piece makeFastRotations(Piece root) {
		// YOUR CODE HERE
		Piece p=root.computeNextRotation();
		while(true) {
			if(p.equals(root))
				break;
			else
				p=p.computeNextRotation();
		}
		return p; // YOUR CODE HERE
	}
	
	

	/**
	 Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 the points into a TPoint[] array.
	 (Provided code)
	*/
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}
		
		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

	


}
