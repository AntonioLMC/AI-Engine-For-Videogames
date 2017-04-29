package com.mygdx.iadevproject.model.formation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.aiReactive.arbitrator.WeightedBlendArbitrator_Accelerated;
import com.mygdx.iadevproject.aiReactive.arbitrator.WeightedBlendArbitrator_NoAccelerated;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.Wander_Delegated;
import com.mygdx.iadevproject.aiReactive.behaviour.noAcceleratedUnifMov.Seek_NoAccelerated;
import com.mygdx.iadevproject.aiReactive.behaviour.noAcceleratedUnifMov.Wander_NoAccelerated;
import com.mygdx.iadevproject.model.Character;
import com.mygdx.iadevproject.model.Obstacle;
import com.mygdx.iadevproject.model.WorldObject;

public class TestStarFormation extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private BitmapFont font;
	private ShapeRenderer renderer;
	
	private Character gota;
	private Character gota2;
	private Character gota3;
	private Character gota4;
	private Character gota5;
	private Character gota6;
	private Character gota7;
	private Character gota8;
	private StarFormation formacion;
	
	// Este falso personaje contendrá en todo momento la posción del ratón.
	private WorldObject fakeMouse;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
        camera = new OrthographicCamera(w, h);
        batch = new SpriteBatch();
        font = new BitmapFont();
        renderer = new ShapeRenderer();
        
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        
        // Creamos 'fakeMouse'
        fakeMouse = new Obstacle();
        
        // Creamos el personaje.
        gota = new Character(new WeightedBlendArbitrator_Accelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota.setBounds(50.0f, 50.0f, 64.0f, 64.0f);
        gota.setOrientation(0.0f);
        gota.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos el personaje.
        gota2 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota2.setBounds(150.0f, 150.0f, 64.0f, 64.0f);
        gota2.setOrientation(30.0f);
        gota2.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos el personaje.
        gota3 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota3.setBounds(250.0f, 250.0f, 64.0f, 64.0f);
        gota3.setOrientation(30.0f);
        gota3.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos el personaje.
        gota4 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota4.setBounds(350.0f, 350.0f, 64.0f, 64.0f);
        gota4.setOrientation(30.0f);
        gota4.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos el personaje.
        gota5 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota5.setBounds(450.0f, 450.0f, 64.0f, 64.0f);
        gota5.setOrientation(30.0f);
        gota5.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        gota5.addToListBehaviour(new Wander_NoAccelerated(gota5, 50.0f, 20.0f)); // En formación, el wander no deberia tenerse en cuenta.
        
        // Creamos el personaje.
        gota6 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota6.setBounds(550.0f, 550.0f, 64.0f, 64.0f);
        gota6.setOrientation(30.0f);
        gota6.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos el personaje.
        gota7 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota7.setBounds(650.0f, 650.0f, 64.0f, 64.0f);
        gota7.setOrientation(30.0f);
        gota7.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos el personaje.
        gota8 = new Character(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), new Texture(Gdx.files.internal("../core/assets/droplet.png")));
        gota8.setBounds(750.0f, 750.0f, 64.0f, 64.0f);
        gota8.setOrientation(30.0f);
        gota8.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        
        // Creamos la formación.
        formacion = new StarFormation(new WeightedBlendArbitrator_NoAccelerated(200.0f, 200.0f), 50.0f);
        formacion.setBounds(500.0f, 500.0f, 64.0f, 64.0f);
        formacion.setOrientation(0.0f);
        formacion.setVelocity(new Vector3(0.0f,0.0f,0.0f));
        formacion.addCharacterToCharactersList(gota);
        formacion.addCharacterToCharactersList(gota2);
        formacion.addCharacterToCharactersList(gota3);
        formacion.addCharacterToCharactersList(gota4);
        formacion.addCharacterToCharactersList(gota5);
        formacion.addCharacterToCharactersList(gota6);
        formacion.addCharacterToCharactersList(gota7);
        formacion.addCharacterToCharactersList(gota8);
        formacion.setSeparationDistance(70.0f);   
        formacion.setArmSize(200.0f);
        formacion.setComponentFormationOrientationMode(Formation.LOOK_INSIDE);
        
        renderer = new ShapeRenderer();
        gota.addToListBehaviour(new Wander_Delegated(gota, 50.0f, 60.0f, 0.0f, 10.0f, 1.0f, 20.0f, 5.0f, 20.0f, 0.0f, 50.0f));
        formacion.addToListBehaviour(new Seek_NoAccelerated(formacion, fakeMouse, 50.0f));
        
        System.out.println("Cantidad de integrantes en la formación: " + formacion.getNumberOfCharacters());

	}
	
	@Override
	public void render() {
		handleInput();
        camera.update();
        // Estas 2 lineas sirven para que los objetos dibujados actualicen su posición cuando se mueva la cámara. (Que se muevan también).
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        
        // Establecemos la posición de 'fakeMouse'.
        //fakeMouse.setPosition(Gdx.input.getX(), Gdx.input.getY()); // TODO Los objetos se mueven en formación, pero a veces no hacia donde deberían. ANALIZAR.
        fakeMouse.setPosition(150, 150); // TODO Esto si va OK. AL METER EL RATÓN DE POR MEDIO, SE PRODUCE UNA PERTURBACIÓN EN LA FUERZA. XD
        
        
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        formacion.applyBehaviour();
        gota5.applyBehaviour(); // No debería hacer nada.
        
        batch.begin();
        gota.draw(batch);
		gota2.draw(batch);
		gota3.draw(batch);
		gota4.draw(batch);
		gota5.draw(batch);
		gota6.draw(batch);
		gota7.draw(batch);
		gota8.draw(batch);
		formacion.draw(batch);
        batch.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		renderer.dispose();
	}
	
	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.translate(-3, 0, 0);
//			float x = gota.getPosition().x - 3;
//			float y = gota.getPosition().y;
//			gota.setPosition(x, y);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.translate(3, 0, 0);
//			float x = gota.getPosition().x + 3;
//			float y = gota.getPosition().y;
//			gota.setPosition(x, y);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.translate(0, -3, 0);
//			float x = gota.getPosition().x;
//			float y = gota.getPosition().y - 3;
//			gota.setPosition(x, y);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.translate(0, 3, 0);
//			float x = gota.getPosition().x;
//			float y = gota.getPosition().y + 3;
//			gota.setPosition(x, y);
		}
//		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//			camera.rotate(-rotationSpeed, 0, 0, 1);
//		}
//		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
//			camera.rotate(rotationSpeed, 0, 0, 1);
//		}

//		camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 1);
//
//		float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
//		float effectiveViewportHeight = camera.viewportHeight * camera.zoom;
//
//		camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f,
//				100 - effectiveViewportWidth / 2f);
//		camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f,
//				100 - effectiveViewportHeight / 2f);
	}
}
