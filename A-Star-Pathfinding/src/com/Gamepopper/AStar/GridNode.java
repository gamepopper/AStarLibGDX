package com.Gamepopper.AStar;

public class GridNode {
	public float X = 0, Y = 0;
	public int Width = 10, Height = 10;
	public GridType type = GridType.NONE;
	public float F = 0, G = 0, H = 0;
	
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
		if (Parent != null)
		{
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
		
		//H = ((Math.abs(X-End.X)/Width) + (Math.abs(Y - End.Y)/Height)) * 10;
		
		float xDistance = (Math.abs(X-End.X)/Width);
		float yDistance = (Math.abs(End.Y-Y)/Height);
		
		if (xDistance > yDistance)
			H = 14*yDistance + 10*(xDistance-yDistance);
	    else
	    	H = 14*xDistance + 10*(yDistance-xDistance);
		
		F = G + H;
	}
	
	public void Reset()
	{
		F = G = H = 0;
	}
}
