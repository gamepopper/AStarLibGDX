package com.Gamepopper.AStar;

public class GridNode {
	public float X = 0, Y = 0;
	public int Width = 10, Height = 10;
	public GridType type = GridType.NONE;
	
	/*These three floats are for path scoring, F Cost, G Cost and Heuristical value*/
	public float F = 0, G = 0, H = 0;
	
	/*Parent Node is used for tracing the final path. 
	 * The node set to be the parent should be the node that leads the shortest route from
	 * the End to the Start*/
	public GridNode Parent = null;
	
	public static enum GridType
	{
		NONE,
		UNPASSABLE,
		START,
		END
	}
	
	public GridNode(float X, float Y)
	{
		this.X = X;
		this.Y = Y;
	}
	
	public GridNode(float X, float Y, int Width, int Height)
	{
		this.X = X;
		this.Y = Y;
		this.Width = Width;
		this.Height = Height;
	}
	
	public GridNode(float X, float Y, int Width, int Height, Boolean Unpassable)
	{
		this.X = X;
		this.Y = Y;
		this.Width = Width;
		this.Height = Height;
	}
	
	public GridNode(float X, float Y, Boolean Unpassable)
	{
		this.X = X;
		this.Y = Y;
	}
	
	public float GetCenterX()
	{
		return X + (Width/2);
	}
	
	public float GetCenterY()
	{
		return Y + (Height/2);
	}
	
	public void CalculateNode(GridNode Parent, GridNode Start, GridNode End)
	{
		this.Parent = Parent;
		
		if (Parent != null)
		{
			/*G Cost is movement cost between this node's parent and itself.
			 * If Horizontal/Vertical, the G Cost is 10.
			 * If Diagonal, the G Cost is sqrt(10^2 + 10^2) = 14.44... or 14 for rounding.*/
			if (Math.abs(X - Parent.X) != 0 &&
					Math.abs(Y - Parent.Y) != 0)
			{
				G = Parent.G + 14;
			}
			else
			{
				G = Parent.G + 10;
			}
		}
		
		/*Heuristic Value is the distance between this node and the end node. It's used to optimise the search
		 *for the best node in each turn.		
		 */
		
		/*Manhattan Method of Calculating Heuristic, simpler to calculate but innaccurate for 8-way pathfinding*/
		//H = ((Math.abs(X-End.X)/Width) + (Math.abs(Y - End.Y)/Height)) * 10;
		
		/*Diagonal Shortcut of Calculating Heuristic, takes diagonal movements into account but slower.*/
		float xDistance = (Math.abs(X-End.X)/Width);
		float yDistance = (Math.abs(End.Y-Y)/Height);
		
		if (xDistance > yDistance)
			H = 14*yDistance + 10*(xDistance-yDistance);
	    else
	    	H = 14*xDistance + 10*(yDistance-xDistance);
		
		/*F cost is the sum of G cost and Heuristic, and is used to compare each node to see which
		 *is closest to the end node. */
		F = G + H;
	}
	
	public void Reset()
	{
		F = G = H = 0;
		Parent = null;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		super.equals(obj);
		GridNode a = (GridNode)obj;
		
		return (X == a.X && 
				Y == a.Y &&
				Width == a.Width &&
				Height == a.Height &&
				type == a.type);
	}
}
