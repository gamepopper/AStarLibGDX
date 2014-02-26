package com.Gamepopper.AStar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class PathFinder {
	private Array<Array<GridNode>> grid = new Array<Array<GridNode>>();
	private Array<GridNode> openPath = new Array<GridNode>();
	private Array<GridNode> closedPath = new Array<GridNode>();
	private Array<GridNode> openTemp = new Array<GridNode>();
	private int GridX, GridY;
	private int StartX = -1, StartY = -1;
	private int EndX = -1, EndY = -1;
	
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
		//Clear current path
		openPath.clear();
		closedPath.clear();
		
		//Reset all F, G and H values to 0
		for (int y = 0; y < grid.size; y++)
		{
			for (int x = 0; x < grid.get(y).size; x++)
			{
				grid.get(y).get(x).Reset();
			}
		}
		
		//If no Start or End nodes have been set, quit the findPath.
		if (StartX == -1 || StartY == -1 || EndX == -1 || EndY == -1)
		{
			return NonExistant;
		}
		else if (StartX == EndX && StartY == EndY) // If Start = End, return found.
		{
			return Found;
		}
		else
		{
			openPath.add(grid.get(StartY).get(StartX)); //Add Start node to open
			SetOpenList(StartX, StartY); //Set neighbours.
			
			closedPath.add(grid.get(StartY).get(StartX)); //Add Start node to closed.
			openPath.removeValue(grid.get(StartY).get(StartX),true); //Remove Start Node from open.
			openTemp = openPath;
			
			while (closedPath.get(closedPath.size - 1) != grid.get(EndY).get(EndX))
			{				
				if (openPath.size != 0)
				{
					float bestF = 10000;
					int bestFIndex = -1;
					
					//Get node with lowest F in open.
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
						openPath.removeValue(openPath.get(bestFIndex), true); //remove from open list
						
						openPath = new Array<GridNode>();
						
						//Set Neighbours for parent
						SetOpenList((int)(closedPath.get(closedPath.size-1).X/GridX), (int)(closedPath.get(closedPath.size-1).Y/GridY));
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
		
		return Found;
	}
	
	public void CompareParentwithOpen(GridNode Parent, GridNode Open)
	{
		//If the G value of open is smaller than the G value in Parent, recalculate F, G and H.
		if (Open.G < Parent.G)
		{
			openPath.get(
					openPath.indexOf(Open, true)).CalculateNode(
							Parent, 
							grid.get(StartY).get(StartX), 
							grid.get(EndY).get(EndX));
			openTemp.add(Open);
		}
	}
	
	public void SetOpenList(int X, int Y)
	{
		//Check position of X and Y to avoid IndexOutofBounds.
		Boolean ignoreLeft = (X - 1 < 0);
		Boolean ignoreRight = (X + 1 >= grid.get(Y).size);
		Boolean ignoreUp = (Y - 1 < 0);
		Boolean ignoreDown = (Y + 1 >= grid.size);
		
		new Array<GridNode>();
		
		//For each check, if the checked grid is not an unpassable type and not in the closed list, continute checking.
		//If checked grid is already in Open List, go to Compare parent with open.
		if (!ignoreLeft && !ignoreUp)
		{
			if (grid.get(Y-1).get(X-1).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y-1).get(X-1), true))
			{
				if (!(openPath.contains(grid.get(Y-1).get(X-1), true) || openPath.contains(grid.get(Y-1).get(X-1), false)))
				{
					grid.get(Y-1).get(X-1).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y-1).get(X-1));
					openTemp.add(grid.get(Y-1).get(X-1));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y-1).get(X-1), true)));
				}
			}
		}
		
		if (!ignoreUp)
		{
			if (grid.get(Y-1).get(X).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y-1).get(X), true))
			{
				if (!(openPath.contains(grid.get(Y-1).get(X), true) || openPath.contains(grid.get(Y-1).get(X), false)))
				{
					grid.get(Y-1).get(X).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y-1).get(X));
					openTemp.add(grid.get(Y-1).get(X));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y-1).get(X), true)));
				}
			}
		}
		
		if (!ignoreRight && !ignoreUp)
		{
			if (grid.get(Y-1).get(X+1).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y-1).get(X+1), true))
			{
				if (!(openPath.contains(grid.get(Y-1).get(X+1), true) || openPath.contains(grid.get(Y-1).get(X+1), false)))
				{
					grid.get(Y-1).get(X+1).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y-1).get(X+1));
					openTemp.add(grid.get(Y-1).get(X+1));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y-1).get(X+1), true)));
				}
			}
		}
		
		if (!ignoreLeft)
		{
			if (grid.get(Y).get(X-1).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y).get(X-1), true))
			{
				if (!(openPath.contains(grid.get(Y).get(X-1), true) || openPath.contains(grid.get(Y).get(X-1), false)))
				{
					grid.get(Y).get(X-1).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y).get(X-1));
					openTemp.add(grid.get(Y).get(X-1));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y).get(X-1), true)));
				}
			}
		}
		
		if (!ignoreRight)
		{
			if (grid.get(Y).get(X+1).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y).get(X+1), true))
			{
				if (!(openPath.contains(grid.get(Y).get(X+1), true) || openPath.contains(grid.get(Y).get(X+1), false)))
				{
					grid.get(Y).get(X+1).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y).get(X+1));
					openTemp.add(grid.get(Y).get(X+1));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y).get(X+1), true)));
				}
			}
		}
		
		if (!ignoreLeft && !ignoreDown)
		{
			if (grid.get(Y+1).get(X-1).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y+1).get(X-1), true))
			{
				if (!(openPath.contains(grid.get(Y+1).get(X-1), true) || openPath.contains(grid.get(Y+1).get(X-1), false)))
				{
					grid.get(Y+1).get(X-1).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y+1).get(X-1));
					openTemp.add(grid.get(Y+1).get(X-1));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y+1).get(X-1), true)));
				}
			}
		}
		
		if (!ignoreDown)
		{
			if (grid.get(Y+1).get(X).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y+1).get(X), true))
			{
				if (!(openPath.contains(grid.get(Y+1).get(X), true) || openPath.contains(grid.get(Y+1).get(X), false)))
				{
					grid.get(Y+1).get(X).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y+1).get(X));
					openTemp.add(grid.get(Y+1).get(X));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y+1).get(X), true)));
				}
			}
		}
		
		if (!ignoreRight && !ignoreDown)
		{
			if (grid.get(Y+1).get(X+1).type != GridNode.GridType.UNPASSABLE && 
					!closedPath.contains(grid.get(Y+1).get(X+1), true))
			{
				if (!(openPath.contains(grid.get(Y+1).get(X+1), true) || openPath.contains(grid.get(Y+1).get(X+1), false)))
				{
					grid.get(Y+1).get(X+1).CalculateNode(grid.get(Y).get(X), grid.get(StartY).get(StartX), grid.get(EndY).get(EndX));
					openPath.add(grid.get(Y+1).get(X+1));
					openTemp.add(grid.get(Y+1).get(X));
				}
				else
				{
					CompareParentwithOpen(grid.get(Y).get(X), 
							openPath.get(openPath.indexOf(grid.get(Y+1).get(X+1), true)));
				}
			}
		}
		
		/*for (GridNode c : closedPath) //incase any nodes in closed are in open, remove closed nodes.
		{
			openPath.removeValue(c, true);
		}*/
	}
	
	public void SetGridNode(int screenX, int screenY, GridNode.GridType Type)
	{
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
		return closedPath;
	}
	
	public void DrawGrid(ShapeRenderer shape)
	{
		shape.begin(ShapeType.Filled);
		
		for (int i = 0; i < closedPath.size; i++)
		{
			GridNode g = closedPath.get(i);
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
