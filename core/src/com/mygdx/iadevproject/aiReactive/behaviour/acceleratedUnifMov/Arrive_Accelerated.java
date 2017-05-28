package com.mygdx.iadevproject.aiReactive.behaviour.acceleratedUnifMov;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.IADeVProject;
import com.mygdx.iadevproject.aiReactive.behaviour.Behaviour;
import com.mygdx.iadevproject.aiReactive.steering.*;
import com.mygdx.iadevproject.model.Character;
import com.mygdx.iadevproject.model.WorldObject;

public class Arrive_Accelerated implements Behaviour {
	
	/**
	 * Método para pintar las líneas de debug del Behaviour
	 */
	private void debug() {
		if (IADeVProject.PRINT_PATH_BEHAVIOUR) {
			IADeVProject.renderer.begin(ShapeType.Line);
			IADeVProject.renderer.setColor(Color.YELLOW);
			
			IADeVProject.renderer.circle(this.target.getPosition().x, this.target.getPosition().y, targetRadious);
			IADeVProject.renderer.circle(this.target.getPosition().x, this.target.getPosition().y, slowRadious);
						
			IADeVProject.renderer.end();
		}
	}
	
	
	private Character source;
	private WorldObject target;
	// Máxima aceleración lineal. (Módulo del vector aceleración).
	private float maxAcceleration;
	private float maxSpeed;
	// Radio interior.
	private float targetRadious;
	// Radio exterior.
	private float slowRadious;
	private float timeToTarget;

	/**
	 * Constructor de la clase.
	 * @param source 
	 * @param target
	 * @param maxAcceleration Máxima aceleración a aplicar en este comportamiento.
	 * @param maxSpeed Máxima velocidad a aplicar en este comportamineto (no confundir con la máxima velocidad del propio personaje).
	 * @param targetRadious Radio interior.
	 * @param slowRadious Radio exterior.
	 * @param timeToTarget Tiempo en el que se realizará este comportamiento. 
	 */
	public Arrive_Accelerated(Character source, WorldObject target, float maxAcceleration, float maxSpeed, float targetRadious, float slowRadious, float timeToTarget) {
		this.source = source;
		this.target = target;
		this.maxAcceleration = maxAcceleration;
		this.maxSpeed = maxSpeed;
		this.targetRadious = targetRadious;
		this.slowRadious = slowRadious;
		this.timeToTarget = timeToTarget;
	}

	public Character getSource() {
		return source;
	}

	public void setSource(Character source) {
		this.source = source;
	}

	public WorldObject getTarget() {
		return target;
	}

	public void setTarget(WorldObject target) {
		this.target = target;
	}

	public float getMaxAcceleration() {
		return maxAcceleration;
	}

	public void setMaxAcceleration(float maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getTargetRadious() {
		return targetRadious;
	}

	public void setTargetRadious(float targetRadious) {
		this.targetRadious = targetRadious;
	}

	public float getSlowRadious() {
		return slowRadious;
	}

	public void setSlowRadious(float slowRadious) {
		this.slowRadious = slowRadious;
	}

	public float getTimeToTarget() {
		return timeToTarget;
	}

	public void setTimeToTarget(float timeToTarget) {
		this.timeToTarget = timeToTarget;
	}

	@Override
	public Steering getSteering() {
		// Creamos el 'Steering' que será devuelto.
		Steering_AcceleratedUnifMov output = new Steering_AcceleratedUnifMov();
		
		// Calculamos el vector 'dirección' entre la fuente y el destino y la distancia entre ambos (el módulo del vector).
		Vector3 direction = new Vector3(target.getPosition());
		direction = direction.sub(source.getPosition());
		float distance = direction.len();
		
		// Si la fuente está dentro del radio interior del destino, no hay que hacer nada más. 
		if (distance < targetRadious) {
			output.setLineal(new Vector3(0,0,0));
			output.setAngular(0);
			return output;
		}
		
		// Si la fuente está fuera del radio exterior, debe ir a máxima velocidad. 
		// 		Si está entre el exterior y el interior, la velocidad debe adaptarse a la distancia que separa a los 2 personajes.
		float targetSpeed; // Velocidad a la que debe ir la fuente (hay que escalar la velocidad).
		if (distance > slowRadious) {
			targetSpeed = this.maxSpeed;
		} else {
			targetSpeed = maxSpeed * distance / slowRadious;
		}
		
		// Tras haber calculado la velocidad (Speed) a la que debemos ir y el vector 'dirección', ahora hay que calcular el vector velocidad (velocity).
		Vector3 targetVelocity = new Vector3(direction);
		targetVelocity = targetVelocity.nor();
		targetVelocity.x = targetVelocity.x * targetSpeed;
		targetVelocity.y = targetVelocity.y * targetSpeed;
		targetVelocity.z = targetVelocity.z * targetSpeed;
		
		// Calculamos el vector aceleración del Steering.
		output.setLineal(targetVelocity.sub(source.getVelocity()));
		targetVelocity.x = targetVelocity.x / timeToTarget;
		targetVelocity.y = targetVelocity.y / timeToTarget;
		targetVelocity.z = targetVelocity.z / timeToTarget;
		
		// Si el módulo de la aceleración es mayor que el máximo permitido, establecemos la aceleración al máximo.
		if (output.getLineal().len() > maxAcceleration) {
			targetVelocity = targetVelocity.nor();
			targetVelocity.x = targetVelocity.x * maxAcceleration;
			targetVelocity.y = targetVelocity.y * maxAcceleration;
			targetVelocity.z = targetVelocity.z * maxAcceleration;
		}
		
		// Establecemos la aceleración angular a 0.
		output.setAngular(0);
		
		this.debug(); // Mostramos información de depuración, si procede.
		
		return output;
	}

}
