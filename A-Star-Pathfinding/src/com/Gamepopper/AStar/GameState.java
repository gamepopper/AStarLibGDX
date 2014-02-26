package com.Gamepopper.AStar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class GameState implements InputProcessor {
	
	protected MainGame game;
    
    protected int screenWidth;
    protected int screenHeight;
    
    protected OrthographicCamera camera;
    
    ShapeRenderer shape = new ShapeRenderer();
    BitmapFont font;
    
    PathFinder pathFinder;
    int pathFound = 0;
	
	public GameState(MainGame game)
	{
		this.game = game;
		screenWidth = MainGame.VIRTUAL_WIDTH;
    	screenHeight = MainGame.VIRTUAL_HEIGHT;
    	
    	camera = new OrthographicCamera(MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);
		camera.setToOrtho(true, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);
    	
    	Gdx.input.setInputProcessor(this);
    	
    	LoadContent();
	}
	
	public void LoadContent()
	{
		pathFinder = new PathFinder(screenWidth, screenHeight, 20);
		font = new BitmapFont(true);
		font.setColor(Color.BLACK);
	}
	
	public void UnloadContent()
	{
		shape.dispose();
	}
	
	public void Update(float dt)
	{        
        camera.update();
	}
	
	public void PauseUpdate(float dt)
	{
		
	}
	
	public void Draw(SpriteBatch batch)
	{
		shape.setProjectionMatrix(camera.combined);
		pathFinder.DrawGrid(shape);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		switch (pathFound)
		{
		case 0:
			font.draw(batch, "Press Space to Calculate Path", 10, 10);
			break;
		case 1:
			font.draw(batch, "Path Found", 10, 10);
			break;
		case 2:
			font.draw(batch, "Path Non Existant", 10, 10);
			break;
		}
		
		Array<GridNode> path = pathFinder.GetPath();
		
		for (int i = 0; i < path.size; i++)
		{
			font.draw(batch, "" + (i+1), path.get(i).X + 2, path.get(i).Y + 2);
		}
		batch.end();
	}

    public void pause() 
    {
    	
    }

    public void resume() 
    {
    	
    }

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == Keys.SPACE)
		{
			pathFound = pathFinder.findPath();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			if (button == 0)
				pathFinder.SetGridNode(screenX, screenY, GridNode.GridType.START);
			else
				pathFinder.SetGridNode(screenX, screenY, GridNode.GridType.END);
		}
		else
		{
			pathFinder.SetGridNode(screenX, screenY, GridNode.GridType.UNPASSABLE);
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
