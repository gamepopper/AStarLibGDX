package com.Gamepopper.AStar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class PathFinder {
	private Array<Array<GridNode>> grid = new Array<Array<GridNode>>(); //Actual Grid
	private Array<GridNode> openPath = new Array<GridNode>(); //Open List: For all checked nodes
	private Array<GridNode> closedPath = new Array<GridNode>(); //Closed Lists: For all checked nodes possibly used for final path.
	private Array<GridNode> finalPath = new Array<GridNode>(); //Final Path
	private int GridX, GridY; //Width and Height for each GridNode.
	private int StartX = -1, StartY = -1; //Grid coordinates for start node.
	private int EndX = -1, EndY = -1; //Grid coordinates for the end node.
	
	public static int Found = 1;
	public static int NonExistant = 2;
	
	public PathFinder(int ScreenWidth, int ScreenHeight, int GridSize)
	{
		for (int y = 0; y < (int)(ScreenHeight/GridSize); y++)
		{
			grid.add(new Array<GridNode>());
			for (int x = 0; x < (int)(ScreenWidth/GridSize); x++)
			{
				grid.get(y).add(new GridNode(x * GridSize, y * GridSize, GridSize, GridSize));
			}
		}
		
		GridX = GridSize;
		GridY = GridSize;
	}
	
	public PathFinder(int ScreenWidth, int ScreenHeight, int GridSizeX, int GridSizeY)
	{
		for (int y = 0; y < (int)(ScreenHeight/GridSizeY); y++)
		{
			grid.add(new Array<GridNode>());
			for (int x = 0; x < (int)(ScreenWidth/GridSizeX); x++)
			{
				grid.get(y).add(new GridNode(x * GridSizeX, y * GridSizeY, GridSizeX, GridSizeY));
			}
		}
		
		GridX = GridSizeX;
		GridY = GridSizeY;
	}
	
	public int findPath()
	{
		/*Clear current path*/
		openPath.clear();
		closedPath.clear();
		finalPath.clear();
		
		/*Reset all F, G and H values to 0*/
		for (int y = 0; y < grid.size; y++)
		{
			for (int x = 0; x < grid.get(y).size; x++)
			{
				grid.get(y).get(x).Reset();
			}
		}
		
		/*If no Start or End nodes have been set, quit the findPath.*/
		if (StartX == -1 || StartY == -1 || EndX == -1 || EndY == -1)
		{
			return NonExistant;
		}
		else if (StartX == EndX && StartY == EndY) /*If Start = End, return found.*/
		{
			return Found;
		}
		else
		{
			openPath.add(grid.get(StartY).get(StartX)); //Add Start node to open
			SetOpenList(StartX, StartY); //Set neighbours for Start node.
			
			closedPath.add(openPath.first()); //Add Start node to closed.
			openPath.removeIndex(0); //Remove Start Node from open.
			
			/*If the last value in the closedPath array isn't the end node, go through the while loop*/
			while (closedPath.peek() != grid.get(EndY).get(EndX))
			{				
				if (openPath.size != 0)
				{
					float bestF = 100000;
					int bestFIndex = -1;
					
					//Get node with lowest F cost in the open list.
					for (int i = 0; i < openPath.size; i++)
					{
						if (openPath.get(i).F < bestF)
						{
							bestF = openPath.get(i).F;
							bestFIndex = i;
						}
					}
					
					if (bestFIndex != -1)
					{
						closedPath.add(openPath.get(bestFIndex)); //Add node to closed list
						openPath.removeIndex(bestFIndex); //remove from open list
						
						//Set Neighbours for parent
						SetOpenList((int)(closedPath.peek().X/GridX), (int)(closedPath.peek().Y/GridY));
					}
					else
						return NonExistant;
				}
				else
				{
					return NonExistant;
				}
			}
		}
		
		/*Time to get our final path*/
		/*Add our end node to the final path*/
		GridNode g = closedPath.peek();
		finalPath.add(g);
		
		/*Then while our last finalPath element is not the start node...*/
		while (g != grid.get(StartY).get(StartX))
		{
			/*Add the parent of that last finalPath element to the finalPath Array*/
			g = g.Parent;
			finalPath.add(g);
			/*Once the finalPath reaches the start node, we have a complete path.*/
		}
		
		/*Reverse the path so the start node will be the first element of the array
		 *not the last*/
		finalPath.reverse();
		
		return Found;
	}
	
	public void SetOpenList(int X, int Y)
	{
		/*Check position of X and Y to avoid IndexOutofBounds.*/
		Boolean ignoreLeft = (X - 1 < 0);
		Boolean ignoreRight = (X + 1 >= grid.get(Y).size);
		Boolean ignoreUp = (Y - 1 < 0);
		Boolean ignoreDown = (Y + 1 >= grid.size);
		
		/*If the adjacent node isn't out of bounds, look at the node*/
		if (!ignoreLeft && !ignoreUp)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y-1).get(X-1));
		}
		
		if (!ignoreUp)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y-1).get(X));
		}
		
		if (!ignoreRight && !ignoreUp)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y-1).get(X+1));
		}
		
		if (!ignoreLeft)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y).get(X-1));
		}
		
		if (!ignoreRight)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y).get(X+1));
		}
		
		if (!ignoreLeft && !ignoreDown)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y+1).get(X-1));
		}
		
		if (!ignoreDown)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y+1).get(X));
		}
		
		if (!ignoreRight && !ignoreDown)
		{
			LookNode(grid.get(Y).get(X), grid.get(Y+1).get(X+1));
		}
	}
	
	public void CompareParentwithOpen(GridNode Parent, GridNode Open)
	{
		/*Compares to see if Open Listed node would lead to a better path than the Parent node.
		 *This is done by setting a temporary G cost using the open node and an added cost
		 *depending on whether the Parent Node is Diagonal or not to said open node.*/
		
		float tempGCost = Open.G;
		
		if (Math.abs(Open.X - Parent.X)/GridX == 1 && Math.abs(Open.Y - Parent.Y)/GridY == 1)
		{
			tempGCost += 14;
		}
		else
		{
			tempGCost += 10;
		}
		
		/*If the temporary G cost is smaller than the Parent Node's G cost,
		 *the open node is recalcuated and the Parent Node is set as the
		 *open node's parent node.*/
		if (tempGCost < Parent.G)
		{
			Open.CalculateNode(Parent, 
							grid.get(StartY).get(StartX), 
							grid.get(EndY).get(EndX));
			openPath.set(openPath.indexOf(Open, true), Open);
		}
	}
	
	public void LookNode(GridNode Parent, GridNode Current)
	{
		/*The Adjacent Node must be ignored if it's either an unpassable grid type or it's in the closedPath list*/
		if (Current.type != GridNode.GridType.UNPASSABLE && 
				!(closedPath.contains(Current, true) || closedPath.contains(Current, false)))
		{
			if (!(openPath.contains(Current, true) || openPath.contains(Current, false)))
			{
				/*Since the node is valid, it must be added to the openPath, with the current node
				 *set as its Parent and the F, G and H costs calculated based on the start and end node.*/
				Current.CalculateNode(Parent, grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
				openPath.add(Current);
			}
			else
			{
				/*If the node is already in the openPath list, it must be compared with the current node
				 *to see if this node will lead to a better path than the current node's path.*/
				CompareParentwithOpen(Parent, 
						openPath.get(openPath.indexOf(Current, true)));
			}
		}
	}
	
	public void SetGridNode(int screenX, int screenY, GridNode.GridType Type)
	{
		/*Sets the GrideNode Type, to either START, END, UNPASSABLE or NONE.*/
		int pointX = (int)(screenX/GridX);
		int pointY = (int)(screenY/GridY);
		
		if (pointY >= 0 && pointY < grid.size)
		{
			if (pointX >=0 && pointX < grid.get(pointY).size)
			{
				if (Type == GridNode.GridType.START || Type == GridNode.GridType.END)
				{
					for (int y = 0; y < grid.size; y++)
					{
						for (int x = 0; x < grid.get(y).size; x++)
						{
							if (grid.get(y).get(x).type == Type)
							{
								if (Type == GridNode.GridType.START)
								{
									StartX = -1;
									StartY = -1;
								}
								else if (Type == GridNode.GridType.END)
								{
									EndX = -1;
									EndY = -1;
								}
								
								grid.get(y).get(x).type = GridNode.GridType.NONE;
							}
						}
					}
				}
				
				if (grid.get(pointY).get(pointX).type == Type)
					grid.get(pointY).get(pointX).type = GridNode.GridType.NONE;
				else
				{
					if (Type == GridNode.GridType.START)
					{
						StartX = pointX;
						StartY = pointY;
					}
					else if (Type == GridNode.GridType.END)
					{
						EndX = pointX;
						EndY = pointY;
					}
					
					grid.get(pointY).get(pointX).type = Type;
				}
			}
		}
	}
	
	public Array<GridNode> GetPath()
	{
		return finalPath;
	}
	
	public void DrawGrid(ShapeRenderer shape)
	{
		/*Draws out the grid and the path.
		 *Black = Unpassable
		 *Green = Start Node
		 *Red = End Node
		 *Yellow = finalPath Node*/
		shape.begin(ShapeType.Filled);
		
		for (int i = 0; i < finalPath.size; i++)
		{
			GridNode g = finalPath.get(i);
			shape.setColor(Color.YELLOW);
			shape.rect(g.X, g.Y, g.Width, g.Height);
		}
		
		for (int y = 0; y < grid.size; y++)
		{
			for (int x = 0; x < grid.get(y).size; x++)
			{
				GridNode g = grid.get(y).get(x);
				
				if (g.type == GridNode.GridType.UNPASSABLE)
				{
					shape.setColor(Color.BLACK);
					shape.rect(g.X, g.Y, g.Width, g.Height);
				}
				else if (g.type == GridNode.GridType.START)
				{
					shape.setColor(Color.GREEN);
					shape.rect(g.X, g.Y, g.Width, g.Height);
				}
				else if (g.type == GridNode.GridType.END)
				{
					shape.setColor(Color.RED);
					shape.rect(g.X, g.Y, g.Width, g.Height);
				}
			}
		}
		shape.end();
	}
}
