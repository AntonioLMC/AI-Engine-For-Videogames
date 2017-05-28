package com.mygdx.iadevproject.checksAndActions;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.IADeVProject;
import com.mygdx.iadevproject.aiReactive.behaviour.Behaviour;
import com.mygdx.iadevproject.aiReactive.behaviour.acceleratedUnifMov.Arrive_Accelerated;
import com.mygdx.iadevproject.aiReactive.behaviour.acceleratedUnifMov.Flee_Accelerated;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.CollisionAvoidance;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.Face;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.LookingWhereYouGoing;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.PathFollowingWithoutPathOffset_Arrive;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.WallAvoidance;
import com.mygdx.iadevproject.aiReactive.behaviour.delegated.Wander_Delegated;
import com.mygdx.iadevproject.aiReactive.behaviour.others.Attack;
import com.mygdx.iadevproject.aiReactive.behaviour.others.Cure;
import com.mygdx.iadevproject.aiReactive.pathfinding.pointToPoint.PointToPoint_PathFinding;
import com.mygdx.iadevproject.model.Character;
import com.mygdx.iadevproject.model.WorldObject;
import com.mygdx.iadevproject.model.formation.Formation;
import com.mygdx.iadevproject.waypoints.Waypoints;

public class Actions {
	
	// =============> EXTREMADAMENTE IMPORTANTE <===============
	// En la acción atacar debemos comprobrar que no sea una formación. COMPROBAMOS AQUÍ Y EN EL AUTÓMATA / ÁRBOL DE DECISIÓN ...
	// 	-> No podemos atacar a un objeto formación como tal.
	
	// =============> EXTREMADAMENTE IMPORTANTE <===============
	// LAS FORMACIONES TAMPOCO VAN A ATACAR.
	// SINO QUE SI QUIEREN ATACAR SE VA A MODIFICAR LA LISTA DE BEHAVIOURS DE SUS INTEGRANTES. --> Pensar.
	
	/**
	 * Método que refleja la acción de atacar.
	 * @param source Personaje que realiza el ataque.
	 * @param target Personaje que recibe el ataque.
	 * @param health Salud que restará el ataque.
	 * @param maxDistance Máxima distancia a la que el ataque se puede realizar.
	 * @return El Map de comportamientos correspondiente a esta acción.
	 */
	// Esta acción no va a necesitar un peso. Tal y como se explica en el comportamiento Attack, realmente lo que nos
	// 	interesa del comportamiento attack no es el comportamiento en sí, si no el hecho de que se ejecute el método
	//	de reducir vida del target del ataque.
	// IMPORTANTE -> Debemos comprobar que ambos personajes son enemigos. PERO ESA COMPROBACIÓN NO SE HACE AQUÍ.
	// 		Se hará en la maquina de estados/árbol de decisión... correspondiente.
	public static Map<Float, Behaviour> attack (Character source, Character target, float health, float maxDistance) {
		Map<Float, Behaviour> map = createListBehaviour();
		// Comprobamos el equipo al que pertenecen ambos personajes.
		// 		Aunque en teoría eso ya se ha comprobado antes de llegar a este método, lo volvemos a hacer para tener un mayor control 
		//		de la acción de atacar.
		if (Checks.isItFromMyTeam(source, target)) {return map;} // Si es de mi equipo no puedo atacarle.
		// Si el source es una formación, solemente indicamos que sus componentes deben atacar al objetivo,
		// 		PERO EL MAP SE DEVUELVE VACÍO (YA QUE EL BEHAVIOUR ATTACK NO SE INTRODUCE EN LA LISTA DE COMPORTAMIENTOS DE LA PROPIA FORMACIÓN).
		if (source instanceof Formation) {
			Formation formation = (Formation)source;
			formation.enableAttackMode(maxDistance, target, health);
			return map;
		} 
		// Si el source es un personaje normal, se añade le behaviour attack.
		map.put(500.0f, new Attack(source, target, health, maxDistance)); // El peso debe ser alto para que el behavior tenga una prioridad alta (en caso de tener un árbitro por prioridad).
		return map;
	}
	
	/**
	 * Método que refleja la acción de curar.
	 * @param source Personaje que realiza el ataque.
	 * @param health Salud que se sumará a la vida del personaje.
	 * @return El Map de comportamientos correspondiente a esta acción.
	 */
	// Esta acción no va a necesitar un peso. Tal y como se explica en el comportamiento Cure, realmente lo que nos
	// 	interesa del comportamiento cure no es el comportamiento en sí, si no el hecho de que se ejecute el método
	//	de aumentar vida del source.
	public static Map<Float, Behaviour> cure (Character source, float health) {
		Map<Float, Behaviour> map = createListBehaviour();
		// Si el source es una formación, solemente indicamos que sus componentes deben curarse,
		// 		PERO EL MAP SE DEVUELVE VACÍO (YA QUE EL BEHAVIOUR CURE NO SE INTRODUCE EN LA LISTA DE COMPORTAMIENTOS DE LA PROPIA FORMACIÓN).
		if (source instanceof Formation) {
			Formation formation = (Formation)source;
			formation.enableCure(health);
			return map;
		}
		// Si el source es un personaje normal, se añade le behaviour cure.
		map.put(520.0f, new Cure(source, health));
		return map;
	}
	
	/**
	 * Método que refleja la acción de dejar de curar.
	 * @param source Personaje que realiza el ataque.
	 */
	public static void leaveCure (Character source) {
		// Para el caso concreto de las formaciones, debemos ejecutar este método para indicar a los componentes que dejen de curarse.
		if (source instanceof Formation) {
			Formation formation = (Formation)source;
			formation.disableCure();
		}
	}
	
	/**
	 * Método que refleja la acción de dejar de atacar.
	 * @param source Personaje que realiza el ataque.
	 */
	public static void leaveAttack (Character source) {
		// Para el caso concreto de las formaciones, debemos ejecutar este método para indicar a los componentes que dejen de atacar.
		if (source instanceof Formation) {
			Formation formation = (Formation)source;
			formation.disableAttackMode();
		}
	}
	
	/**
	 * Método que refleja la acción de liberar un waypoint.
	 * @param source Personaje que quiere liberar el waypoint.
	 */
	public static void leaveWaypoint(Character source) {
		Waypoints.freeBridgeWaypoint(source);
	}
	
	/**
	 * Método que refleja la acción de huir de un objetivo.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @param target Objetivo del que se quiere huir
	 * @param maxAcceleration Máxima aceleración a la que se quiere huir.
	 * @return El comportamiento correspondiente a la acción de huir con su correspondiente peso.
	 */
	public static Map<Float, Behaviour> flee(float weight, Character source, WorldObject target, float maxAcceleration) {
		Map<Float, Behaviour> map = createListBehaviour();
		map.put(weight, new Flee_Accelerated(source, target, maxAcceleration));
		return map;
	}
	
	/**
	 * Método que refleja la acción de llegar a un objetivo.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @param target Objetivo al que se quiere llegar
	 * @param maxAcceleration Máxima aceleración a la que se quiere llegar.
	 * @return El comportamiento correspondiente a la acción de llegar con su correspondiente peso.
	 */
	public static Map<Float, Behaviour> arrive(float weight, Character source, WorldObject target, float maxAcceleration) {
		Map<Float, Behaviour> map = createListBehaviour();
		map.put(weight, new Arrive_Accelerated(source, target, maxAcceleration, 50.0f, 15.0f, 25.0f, 1.0f));
		return map;
	}
	
	/**
	 * Método que crea un objeto PointToPoint_PathFinding que va desde la posición del personaje 'source' a la
	 * posición 'position'
	 * @param source Personaje desde el que se realiza el PathFinding.
	 * @param position Posición a la que se quiere llegar.
	 * @return Objeto PointToPoint_PathFinding creado.
	 */
	public static PointToPoint_PathFinding createPathFinding(Character source, Vector3 position) {
		PointToPoint_PathFinding pf = new PointToPoint_PathFinding(source, 30.0f, PointToPoint_PathFinding.EUCLIDEAN_DISTANCE, IADeVProject.GRID_CELL_SIZE, 
				source.getPosition().x, source.getPosition().y, position.x, position.y, IADeVProject.MAP_OF_COSTS, IADeVProject.GRID_WIDTH, IADeVProject.GRID_HEIGHT);
		return pf;
	}
	
	/**
	 * EXTREMADAMENTE IMPORTANTE: Solamente necesitamos un método 'goTo' porque este recibe el objeto PathFinding que hay que aplicar.
	 * En la creación de este PathFinding ya se le ha introducido el origen y el destino. Por lo que en este método solamente
	 * tenemos que aplicarlo
	 */
	/**
	 * Método que refleja la acción de ir hacia una determinada posición.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @param pf Objeto PathFinding que aplicamos.
	 * @param maxAcceleration Máxima acceleración a la que se quiere ir
	 * @return El comportamiento correspondiente a la acción de ir a la posición con su correspondiente peso.
	 */
	public static Map<Float, Behaviour> goTo(float weight, Character source, PointToPoint_PathFinding pf,  float maxAcceleration) {
		List<Vector3> pointsList = pf.applyPathFinding();
		
		Map<Float, Behaviour> map = createListBehaviour();
		map.put(weight, new LookingWhereYouGoing(source, 20.0f, 45.0f, 10.0f, 20.0f, 1.0f));
		map.put(weight, new PathFollowingWithoutPathOffset_Arrive(source, maxAcceleration, source.getMaxSpeed(), 
							30.0f, 30.0f, 1.0f, pointsList, PathFollowingWithoutPathOffset_Arrive.MODO_PARAR_AL_FINAL));
		return map;
	}
	
	/**
	 * Método que refleja la acción de patrullar la base del personaje
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @return El comportamiento correspondiente esta acción.
	 */
	public static Map<Float, Behaviour> patrolYourBase(float weight, Character source, float maxAcceleration) {
		
		Map<Float, Behaviour> map = createListBehaviour();
		// Obtenemos los waypoints de la base del personaje a patrullar.
		List<Vector3> pointsList = Waypoints.getWaypointsOfMyBase(source);
		// Introducimos los comportamientos
		map.put(weight, new LookingWhereYouGoing(source, 20.0f, 45.0f, 10.0f, 20.0f, 1.0f));
		map.put(weight, new PathFollowingWithoutPathOffset_Arrive(source, maxAcceleration, source.getMaxSpeed(), 
				25.0f, 35.0f, 1.0f, pointsList, PathFollowingWithoutPathOffset_Arrive.MODO_IDA_Y_VUELTA));
		return map;
	}
	
	/**
	 * Método que refleja la acción de patrullar el waypoint del personaje.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @return El comportamiento correspondiente esta acción. Si el personaje
	 * no tiene asociado un waypoint, entonces el comportamiento es vacío.
	 */
	public static Map<Float, Behaviour> patrolYourWaypoint(float weight, Character source, float maxAcceleration) {
		Map<Float, Behaviour> map = createListBehaviour();
		
		// Comprobamos si el personaje tiene asociado un waypoint.
		if (Checks.haveIGotWayPoint(source)) {
			// Obtenemos los waypoints del personaje a patrullar.
			List<Vector3> pointsList = Waypoints.getAssociatedWaypointAndNeighboring(source);
			// Introducimos los comportamientos
			map.put(weight, new LookingWhereYouGoing(source, 20.0f, 45.0f, 10.0f, 20.0f, 1.0f));
			map.put(weight, new PathFollowingWithoutPathOffset_Arrive(source, maxAcceleration, source.getMaxSpeed(), 
					25.0f, 35.0f, 1.0f, pointsList, PathFollowingWithoutPathOffset_Arrive.MODO_IDA_Y_VUELTA));
		}
		
		return map;
	}
	
	/**
	 * Método que refleja la acción de reservar un waypoint.
	 * @param source Personaje que quiere reservar el waypoint.
	 */
	public static void bookWaypoint(Character source) {
		Waypoints.bookBridgeWaypoint(source);
	}
	
	/**
	 * Método que refleja la acción de hacer cosas aleatorias. Básicamente se basa en que el 
	 * personaje se mueva de manera aleatoria.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @return El comportamiento correspondiente a la acción de hacer cosas aleatorias con su correspondiente peso.
	 */
	public static Map<Float, Behaviour> doRandomThings(float weight, Character source) {
		Map<Float, Behaviour> map = createListBehaviour();
		map.put(weight, new Wander_Delegated(source, 50.0f, 50.0f, 5.0f, 15.0f, 1.0f, 100.0f, 50.0f, 60.0f, source.getOrientation(), 10.0f));
		return map;
	}
	
	/**
	 * Método que refleja la acción de no colisionar con los objetos del mundo. Utiliza tanto el 
	 * WallAvoidance como el CollisionAvoidance.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @return El comportamiento correspondiente a la acción de no colisionar con su correspondiente peso.
	 */
	public static Map<Float, Behaviour> notCollide(float weight, Character source) {
		Map<Float, Behaviour> map = createListBehaviour();
		map.put(weight, new WallAvoidance(source, 300.0f, IADeVProject.worldObjects, 100.0f, 20.0f, 100.0f));
		map.put(weight, new CollisionAvoidance(source, IADeVProject.worldObjects, 200.0f));
		return map;
	}
	
	/**
	 * Método que devuelve el enemigo más cercano del personaje 'source'
	 * @param source Personaje que quiere obtener el enemigo más cercano.
	 * @return Personaje más cercano o null en caso contrario.
	 */
	public static Character getTheNearestEnemy(Character source) {
		Character target = null;
		float minDistance = Float.MAX_VALUE;
		
		for (WorldObject obj : IADeVProject.worldObjects) {
			// Consideramos los personajes que no sean formaciones
			if (obj instanceof Character && !(obj instanceof Formation)) {	
				Character character = (Character)obj;
				
				// Comprobamos que sea del equipo contrario. No comprobamos que el personaje obtenido no sea el 'source' porque este método 
				// cuando se compruebe, va a devolver un 'false', ya que es el mismo objeto. 
				// La comprobación de si el personaje está muerto, es para evitar que cuando se ataque, si uno de sus componentes está muerto,
				// como solo se va a eliminar la formación si se ha matado a todos los de la formación, el 'source' ataque a otro personaje.
				if (Checks.isItFromEnemyTeam(source, character) && !Checks.haveIDead(character)) {
					// Obtenemos las posiciones del source y del personaje
					Vector3 srcPostion = source.getPosition();
					Vector3 charPosition = character.getPosition();
					// Calculamos la distancia 
					float distance = srcPostion.dst(charPosition);
					
					// Comprobamos que la distancia sea menor que la mínima calculada y 
					// si es así actualizamos.
					if (distance < minDistance) {
						minDistance = distance;
						target = character;
					}
				}
			}
		}
		
		return target;
	}
	
	/**
	 * Método que mueve un personaje a una posición concreta A PELO. Este método es una método
	 * recursivo para las formaciones, ya que hay que mover a todos los integrantes de las formaciones
	 * al mismo punto. Con esto se permite que las formaciones complejas se muevan todas al mismo punto.
	 * @param source Personaje que se va a modificar su posición
	 * @param position Nueva posición del personaje.
	 */
	public static void moveToPosition(Character source, Vector3 position) {
		if (source instanceof Formation) {
			// Si el personaje es una formación:
			Formation formation = (Formation)source;
			// Establecemos la nueva posición de la formación.
			formation.setPosition(position);
			for (Character c : formation.getCharactersList()) {
				// Para cada componente de la formación, lo movemos a esa formación.
				moveToPosition(c, position);
			}
		} else {
			source.setPosition(position);
		}
	}
	
	
	/**
	 * Método que realiza la acción de atacar al enemigo más cercano. Hace uso de las acciones
	 * 'attack' y 'getTheNearestEnemy'.
	 * @param source Personaje que realiza el ataque.
	 * @param target Personaje que recibe el ataque.
	 * @param health Salud que restará el ataque.
	 * @param maxDistance Máxima distancia a la que el ataque se puede realizar.
	 * @return El Map de comportamientos correspondiente a esta acción.
	 */
	public static Map<Float, Behaviour> attackTheNearestEnemy(Character source, float health, float maxDistance) {
		// Obtenemos el enemigo más cercano
		Character target = getTheNearestEnemy(source);
		// Lo atacamos.
		return attack(source, target, health, maxDistance);
	}
	
	/**
	 * Método que realiza la acción de mirar a un objetivo.
	 * @param weight Peso que tiene esta acción.
	 * @param source Personaje que quiere aplicar la acción.
	 * @param target Personaje al que se quiere mirar.
	 * @return El comportamiento correspondiente a esta acción.
	 */
	public static Map<Float, Behaviour> faceToTarget(float weight, Character source, Character target) {
		Map<Float, Behaviour> map = createListBehaviour();
		
		map.put(weight, new Face(source, target, 20.0f, 30.0f, 2.0f, 7.0f, 1.0f));
		return map;
	}
	
	
	/**
	 * Método que crea la lista de comportamientos. Se ha creado este método para evitar
	 * repetir el código en los constructores. IMPORTANTE: LA CREACIÓN DEL MAP PERMITE
	 * QUE SE LE PASE CUALQUIER VALOR COMO CLAVE, YA SEA UN float O UN new Float() Y
	 * NO SE SUSTITUYE.
	 */
	private static Map<Float, Behaviour> createListBehaviour() {
		// Añadimos el comparador porque si no, el TreeMap por defecto, si se encuentra
		// dos claves que tengan el mismo valor (aunque sean objetos distintos) se queda con 
		// el último que introduces
		return new TreeMap<Float, Behaviour>(new Comparator<Float>() {
			@Override
			public int compare(Float o1, Float o2) {
				// Para que los ordene de mayor a menor
				if (o1 > o2) return -1;
				if (o1 == o2) return 0;
				return 1;
			}
		});
	}
}
