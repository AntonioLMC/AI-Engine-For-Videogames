package com.mygdx.iadevproject;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.model.WorldObject;
import com.mygdx.iadevproject.userInteraction.UserInteraction;
import com.mygdx.iadevproject.model.Character;

/**
 * Clase que implementa el InputProcessor del projecto.
 */
public class InputProcessorIADeVProject implements InputProcessor {
	private boolean keyPressed = false;		// Indica que se ha pulsado una tecla
	private int lastKey;					// Última tecla pulsada
	private Vector3 touchPos;
	private Character target; 
	
	/**
	 * Enumerado que refleja el estado en el que se está procesando las acciones del usuario
	 */
	private enum UserState { 
		NO_SELECTED_CHARACTERS, 	// Refleja el estado de que el usuario no ha seleccionado ningún personaje (estado inicial)
		JUST_SELECTED_CHARACTERS, 	// Refleja el estado de que el usuario acaba de seleccionar personajes
		ACCELERATED,				// El usuario quiere hacer un comportamiento acelerado
		DELEGATED,					// El usuario quiere hacer un comportamiento delegado
		GROUP,						// El usuario quiere hacer un comportamiento de grupo
		OTHERS,						// El usuario quiere hacer otros comportamientos
		MAKE_FORMATION				// El usuario quiere hacer una formación.
	};
	
	private UserState state = UserState.NO_SELECTED_CHARACTERS;
	
	
	@Override
	public boolean keyDown(int keycode) {
		
		keyPressed = true;
		lastKey = keycode;
		
		switch (keycode) {
		case Input.Keys.LEFT: 				// Movimiento hacia la izquierda
			IADeVProject.camera.translate(-5, 0, 0);
			break;
		case Input.Keys.RIGHT:				// Movimiento hacia la derecha
			IADeVProject.camera.translate(5, 0, 0);
			break;
		case Input.Keys.DOWN:				// Movimiento hacia abajo
			IADeVProject.camera.translate(0, -5, 0);
			break;
		case Input.Keys.UP:					// Movimiento hacia arriba
			IADeVProject.camera.translate(0, 5, 0);
			break;
		case Input.Keys.A:					// Alejar la cámara
			IADeVProject.camera.zoom += 0.02;	
			break;
		case Input.Keys.Q:					// Acercar la cámara
			IADeVProject.camera.zoom -= 0.02;
			break;
		default:
			processState(keycode);
			break;
		}
		
		return true;
	}
	
	/**
	 * Método que procesa el estado en el que se está procesando las acciones del usuario conjunto
	 * a la tecla pulsada 'keycode'
	 * @param keycode tecla pulsada
	 */
	private void processState(int keycode) {
		keyPressed = false;
		switch (state) {
			case NO_SELECTED_CHARACTERS: 
				break;
			case JUST_SELECTED_CHARACTERS: 
				processJustSelectedCharacters(keycode);
				break;
			case ACCELERATED:
				if (target == null) return;
				processAccelerated(keycode);
				break;
			case DELEGATED:
				if (target == null) return;
				processDelegated(keycode);
				break;
			case GROUP:
				processGroup(keycode);
				break;
			case OTHERS:
				processOthers(keycode);
				break;
			case MAKE_FORMATION:
				processMakeFormation(keycode);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Método que procesa la tecla pulsada para cambiar de estado cuando se ha seleccionado personajes
	 * y se quiere hacer algo con ellos
	 * @param keycode Código de la tecla pulsada.
	 */
	private void processJustSelectedCharacters(int keycode) {		
		switch (keycode) {
		case Input.Keys.NUM_1: // Apply accelerated behaviours
			UserInteraction.printAcceleratedBehaviours();
			System.out.println("Select the character with which you want to apply the behaviour");
			state = UserState.ACCELERATED;
			break;
		case Input.Keys.NUM_2: // Apply delegated behaviours
			UserInteraction.printDelegatedBehaviours();
			System.out.println("Select the character with which you want to apply the behaviour");
			state = UserState.DELEGATED;
			break;
		case Input.Keys.NUM_3: // Apply group behaviours
			UserInteraction.printGroupBehaviours();
			state = UserState.GROUP;
			break;
		case Input.Keys.NUM_4: // Others behaviours
			UserInteraction.printOthersBehaviours();
			state = UserState.OTHERS;
			break;
		case Input.Keys.NUM_5: // Make formation
			UserInteraction.printMakeFormation();
			state = UserState.MAKE_FORMATION;
			break;
		default:
			return;
		}
	}
	
	/**
	 * Método que procesa la interacción de comportamientos acelerados
	 * @param keycode tecla pulsada
	 */
	private void processAccelerated(int keycode) {
		switch (keycode) {
		case Input.Keys.NUM_1: // Align
			System.out.println("1) Align");
			UserInteraction.applyAlign(target);
			break;
		case Input.Keys.NUM_2: // Anti-Align
			System.out.println("2) Anti-Align");
			UserInteraction.applyAntiAlign(target);
			break;
		case Input.Keys.NUM_3: // Arrive
			System.out.println("3) Arrive");
			UserInteraction.applyArrive(target);
			break;
		case Input.Keys.NUM_4: // Flee
			System.out.println("4) Flee");
			UserInteraction.applyFlee(target);
			break;
		case Input.Keys.NUM_5: // Seek
			System.out.println("5) Seek");
			UserInteraction.applySeek(target);
			break;
		case Input.Keys.NUM_6: // Velocity matching
			System.out.println("6) Velocity matching");
			UserInteraction.applyVelocityMatching(target);
			break;
		case Input.Keys.NUM_7: // Return
			state = UserState.JUST_SELECTED_CHARACTERS;
			UserInteraction.printPossibleUserActions();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Método que procesa la interacción de comportamientos delegados
	 * @param keycode tecla pulsada
	 */
	private void processDelegated(int keycode) {
		switch (keycode) {
		case Input.Keys.NUM_1: // Evade
			System.out.println("1) Evade");
			UserInteraction.applyEvade(target);
			break;
		case Input.Keys.NUM_2: // Face
			System.out.println("2) Face");
			UserInteraction.applyFace(target);
			break;
		case Input.Keys.NUM_3: // Looking where you going
			System.out.println("3) Looking where you going");
			UserInteraction.applyLookingWhereYouGoing();
			break;
		case Input.Keys.NUM_4: // Pathfinding
			System.out.println("4) Pathfinding");
			UserInteraction.applyPathFinding(touchPos);
			break;
		case Input.Keys.NUM_5: // Persue
			System.out.println("5) Persue");
			UserInteraction.applyPersue(target);
			break;
		case Input.Keys.NUM_6: // Wander
			System.out.println("6) Wander");
			UserInteraction.applyWander();
			break;
		case Input.Keys.NUM_7: // Return
			state = UserState.JUST_SELECTED_CHARACTERS;
			UserInteraction.printPossibleUserActions();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Método que procesa la interacción de comportamientos de grupo
	 * @param keycode tecla pulsada
	 */
	private void processGroup(int keycode) {
		switch (keycode) {
		case Input.Keys.NUM_1: // Cohesion
			System.out.println("1) Cohesion");
			break;
		case Input.Keys.NUM_2: // Separation
			System.out.println("2) Separation");
			break;
		case Input.Keys.NUM_3: // Return
			state = UserState.JUST_SELECTED_CHARACTERS;
			UserInteraction.printPossibleUserActions();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Método que procesa la interacción de otros comportamientos
	 * @param keycode tecla pulsada
	 */
	private void processOthers(int keycode) {
		switch (keycode) {
		case Input.Keys.NUM_1: // Attack
			System.out.println("1) Attack");
			break;
		case Input.Keys.NUM_2: // Cure
			System.out.println("2) Cure");
			break;
		case Input.Keys.NUM_3: // Return
			state = UserState.JUST_SELECTED_CHARACTERS;
			UserInteraction.printPossibleUserActions();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Método que procesa la interacción de crear una formación
	 * @param keycode tecla pulsada
	 */
	private void processMakeFormation(int keycode) {
		switch (keycode) {
		case Input.Keys.NUM_1: // Return
			state = UserState.JUST_SELECTED_CHARACTERS;
			UserInteraction.printPossibleUserActions();
			break;
		default:
			break;
		}
	}
	
	

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		// Si el botón presionado es el botón IZQUIERDO, se quiere seleccionar a un personaje
		if (button == Input.Buttons.LEFT) {
			
			// Obtenemos las coordenadas de la cámara correspondiente a las coordenadas de la pantalla
			touchPos = new Vector3();
			touchPos.set(screenX, screenY, 0);
			IADeVProject.camera.unproject(touchPos);
			
			// Solo dejamos que seleccione un personaje cuando no estamos ejecutando ninguna comportamiento
			if (state == UserState.NO_SELECTED_CHARACTERS || state == UserState.JUST_SELECTED_CHARACTERS) {
				Character character = getCharacterOfPosition(touchPos);
				
				if (character != null) {
					// Si se ha pinchado sobre él, añadimos a la lista de objetos seleccionados
					IADeVProject.addToSelectedCharactersList(character);
					// Mostramos las posibles acciones del usuario
					UserInteraction.printPossibleUserActions();
					// Cambiamos de estado
					state = UserState.JUST_SELECTED_CHARACTERS;
				}
			} else {
				// Si estamos ejecutando algún comportamiento, obtenemos el personaje que se ha seleccionado
				target = getCharacterOfPosition(touchPos);
				if (target != null) { 
					System.out.println("The character has been selected. Press some number to apply a behaviour");
				} else {
					System.out.println("The target character has been released. Select another character.");
				}
			}
			
		} else if (button == Input.Buttons.RIGHT) {
			// Si el botón presionado es el botón DERECHO, limpiamos la lista de objetos seleccionados.
			IADeVProject.clearSelectedCharactersList();
			state = UserState.NO_SELECTED_CHARACTERS;
		}
		
		// Devolvemos true indicando que se ha procesado el evento
		return true;
	}
	
	/**
	 * Método que dada una posición devuelve el personaje que se encuentra en la posición
	 * pasada como parámetro. 
	 * @param position Posición
	 * @return Personaje en esa posición, null en caso contrario.
	 */
	private static Character getCharacterOfPosition(Vector3 position) {
		for (WorldObject obj : IADeVProject.worldObjects) {
			if (obj instanceof Character) {
				Character c = (Character)obj;
				// Comprobamos si se ha pinchado sobre él
				if (c.getBoundingRectangle().contains(new Vector2(position.x, position.y))) { 
					return c;
				}
			}
		}
		return null;
	}
	
	/**
	 * Este método ha sido creado porque InputProcessor no tiene ningún método para controlar
	 * que una tecla se mantenga pulsada. Lo que hace es comprobar si hay una tecla pulsada
	 * y si es así, llama al método 'keyDown' con la tecla pulsada para procesarla.
	 */
	public void processKeyPressed() {
		if (keyPressed) {
			keyDown(lastKey);
		}
	}
	
	@Override
	public boolean keyUp(int keycode) {
		// Con esto indicamos que si la tecla que se suelta no es la última que se ha
		// pulsado, no hacemos nada, se sigue haciendo el efecto de la última tecla pulsada
		if (keycode != lastKey) return true;
		
		keyPressed = false;
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
