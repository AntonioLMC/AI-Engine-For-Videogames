package com.mygdx.iadevproject.arbitrator.weightedBlend;

import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.arbitrator.Arbitrator;
import com.mygdx.iadevproject.behaviour.Behaviour;
import com.mygdx.iadevproject.steering.Steering;
import com.mygdx.iadevproject.steering.Steering_NoAcceleratedUnifMov;

/**
 * Clase que representa al árbitro de Mezcla Ponderada. Este árbitro
 * trabaja solamente con comportamientos No Acelerados. Si alguno de los 
 * comportamientos es acelerado, no se tiene en cuenta.
 */
public class WeightedBlendArbitrator_NoAccelerated implements Arbitrator {

	private float maxSpeed;
	private float maxRotation;
	
	public WeightedBlendArbitrator_NoAccelerated(float maxSpeed, float maxRotation) {
		this.maxSpeed = maxSpeed;
		this.maxRotation = maxRotation;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getMaxRotation() {
		return maxRotation;
	}

	public void setMaxRotation(float maxRotation) {
		this.maxRotation = maxRotation;
	}

	@Override
	public Steering getSteering(Map<Float, Behaviour> behaviours) {
		// Steering de salida
		Steering_NoAcceleratedUnifMov steering = new Steering_NoAcceleratedUnifMov();
		
		// Valores que se asignarán al Steering anterior, que se van
		// modificando conforme se obtienen los steerings de los comportamientos
		Vector3 velocity = new Vector3(0,0,0);
		float rotation = 0.0f;
		
		// Empty vector para comprobar si el steering es 0 (en vez de null)
		Vector3 emptyVector = new Vector3(0,0,0);
		
		// Para cada uno de los comportamientos
		for (Entry<Float, Behaviour> behaviour : behaviours.entrySet()) {
			// Obtenemos el steering del comportamiento
			Steering steer = behaviour.getValue().getSteering();
			
			// Si el steering es acelerado, lo tenemos en cuenta.
			if (steer instanceof Steering_NoAcceleratedUnifMov) {
				Steering_NoAcceleratedUnifMov steerAcc = (Steering_NoAcceleratedUnifMov) steer;
				
				// Si es distinto de null o no es un vector 0, actualizamos
				// los valores
				if (steerAcc != null || !steerAcc.getVelocity().idt(emptyVector)) {	 
					velocity.add(steerAcc.getVelocity().scl(behaviour.getKey()));
					rotation += steerAcc.getRotation()*behaviour.getKey();
				}
			}
		}
		
		// Comprobamos que no superen el máximo
		if (velocity.len() > this.maxSpeed) {
			velocity.nor();
			velocity.scl(this.maxSpeed);
		}
		
		rotation = (rotation > this.maxRotation) ? this.maxRotation : rotation;
		
		// Devolvemos el steering
		steering.setVelocity(velocity);
		steering.setRotation(rotation);
		return steering;
	}

}
