package com.mygdx.iadevproject.aiReactive.behaviour.others;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.aiReactive.behaviour.Behaviour;
import com.mygdx.iadevproject.aiReactive.steering.Steering;
import com.mygdx.iadevproject.model.Character;

public class Attack implements Behaviour {

	private Character source;
	private Character target; // El target también debe ser un Character.
	// Salud que se le restará al target en el ataque.
	private float health;
	// Distancia máxima a la que puede realizar el ataque.
	// 	Si la separación entre origin y destino es MAYOR ESTRICTO, no se realiza el ataque.
	private float maxDistance;
	
	// Constructor.
	public Attack(Character source, Character target, float health, float maxDistance) {
		super();
		this.source = source;
		this.target = target;
		this.health = health;
		this.maxDistance = maxDistance;
	}

	public Character getSource() {
		return source;
	}

	public void setSource(Character source) {
		this.source = source;
	}

	public Character getTarget() {
		return target;
	}

	public void setTarget(Character target) {
		this.target = target;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
	}

	/**
	 * Hemos decidido implementar el ataque como un Behavior para poder tratarlo de manera homogenea con los otros Behaviours y poder
	 * meterlo en el Map.
	 * Aunque este Behaviour devuelva null y un árbitro nunca lo tenga en cuenta, al menos el código sí se ha ejecutado y la vida del
	 * target sí se ha restado (QUE ES REALMENTE LO QUE ANDAMOS BUSCANDO).
	 */
	
	@Override
	public Steering getSteering() {
		// Primero, comprueba si source y target están a la distancia adecuada para que se pueda realizar el ataque.
		Vector3 direction = new Vector3(target.getPosition());
		direction = direction.sub(source.getPosition());
		float distance = direction.len();
		
		if (distance <= this.maxDistance) {
			// Realizamos el ataque, reduciendo vida al target.
			target.reduceHealth(this.health);
		}
		
		// Devolvemos null, puesto que lo importante de este Behaviour no es el propio Behavior sino la reducción de vida del target.
		return null;
	}

}
