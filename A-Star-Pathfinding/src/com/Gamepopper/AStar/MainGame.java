package com.Gamepopper.AStar;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MainGame implements ApplicationListener {
	private ShapeRenderer shapeRenderer;
	private Rectangle viewport;
	private SpriteBatch batch;
	
	public GameState currentState;
	
	public float cropR = 0.0f, cropG = 0.0f, cropB = 0.0f;
	public float clearR = 1.0f, clearG = 1.0f, clearB = 1.0f;
	
	public Boolean Paused = false;
	public Boolean Muted = false;
	
	public Preferences prefs;
	
	public static final int VIRTUAL_WIDTH=1280;
	public static final int VIRTUAL_HEIGHT=720;
	public static final float ASPECT_RATIO = 
			(float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
	
	@Override
	public void create() {		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		currentState = new GameState(this);
	}
	
	public void setCropColor(float r, float g, float b)
	{
		cropR = r;
		cropB = b;
		cropG = g;
	}
	
	public void setClearColor(float r, float g, float b)
	{
		clearR = r;
		clearG = g;
		clearB = b;
	}

	public Vector2 ConvertCoords(int x, int y)
	{
		Vector2 newCoords = new Vector2();
		
		x -= viewport.x;
		y -= viewport.y;
		
		float scaleX = (float)viewport.width/(float)VIRTUAL_WIDTH;
		float scaleY = (float)viewport.height/(float)VIRTUAL_HEIGHT;
		
		newCoords.x = (x/scaleX);
		newCoords.y = (y/scaleY);
		
		return newCoords;
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		currentState.UnloadContent();
	}

	@Override
	public void render() {
		if (Paused)
		{
			currentState.PauseUpdate(Gdx.graphics.getDeltaTime());
		}
		else
		{
			currentState.Update(Gdx.graphics.getDeltaTime());
		}
        
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                (int) viewport.width, (int) viewport.height);
        
        Gdx.gl.glClearColor(cropR, cropG, cropB, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
		shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(clearR, clearG, clearB, 1);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        shapeRenderer.end();
        
        currentState.Draw(batch);
	}

	@Override
	public void resize(int width, int height) {
		 // calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);
		
		if(aspectRatio > ASPECT_RATIO)
        {
            scale = (float)height/(float)VIRTUAL_HEIGHT;
            crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < ASPECT_RATIO)
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
            crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
        }

        float w = (float)VIRTUAL_WIDTH*scale;
        float h = (float)VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
	}

	@Override
	public void pause() 
	{
		currentState.pause();
	}

	@Override
	public void resume() {
		currentState.resume();
	}
}

