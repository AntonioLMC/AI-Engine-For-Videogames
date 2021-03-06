package com.mygdx.iadevproject.aiReactive.behaviour.group;

import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.iadevproject.aiReactive.behaviour.acceleratedUnifMov.Seek_Accelerated;
import com.mygdx.iadevproject.aiReactive.steering.Steering;
import com.mygdx.iadevproject.aiReactive.steering.Steering_AcceleratedUnifMov;
import com.mygdx.iadevproject.model.Character;
import com.mygdx.iadevproject.model.Obstacle;
import com.mygdx.iadevproject.model.WorldObject;

public class Cohesion extends Seek_Accelerated {

	private List<WorldObject> targets; // Lista de objetivos.
	private float threshold;
	// Los atributos 'source' y 'maxAcceleration' están en el padre.
	
	/**
	 * Constructor de la clase.
	 * @param source Personaje que aplica el comportamiento.
	 * @param targets Lista de WorldObject sobre los que se aplica el comportamiento.
	 * @param maxAcceleration Máxima aceleración a aplicar en el comportamiento.
	 * @param threshold Radio en el que se tienen en cuenta los objetivos (todos los elementos fuera de este radio no son tenidos en cuenta al aplicar el comportamiento).
	 */
	public Cohesion(Character source, List<WorldObject> targets, float maxAcceleration, float threshold) {
		super(source, null, maxAcceleration);
		this.targets = targets;
		this.threshold = threshold;
	}
	
	@Override
	public Steering getSteering() {
		int count = 0;
		Vector3 centerOfMass = new Vector3(0.0f, 0.0f, 0.0f);
		// Recorremos la lista de objetivos.
		for (WorldObject worldObject : targets) {
			// Calculamos la dirección y distancia hacia el objetivo.
			Vector3 direction = new Vector3(worldObject.getPosition());
			direction = direction.sub(super.getSource().getPosition());
			float distance = direction.len();
			// Si está dentro del radio especificado, se añade a 'centerOfMass'
			if (distance <= this.threshold) {
				centerOfMass = centerOfMass.add(new Vector3(worldObject.getPosition()));
				count++;
			}
		}
		// Si ningún objetivo estaba dentro del rango a tener en cuenta, devolvemos el steering nulo.
		if (count == 0) {
			// Creamos el 'Steering' que será devuelto. El steering nulo en este caso.
			Steering_AcceleratedUnifMov output = new Steering_AcceleratedUnifMov();
			output.setLineal(new Vector3(0.0f, 0.0f, 0.0f));
			output.setAngular(0.0f);
			return output;
		}
		// Sino, creamos un personaje ficticio y hacemos un Seek hacie él.
		// La posición del personaje ficticio será el centro de masas calculado.
		centerOfMass = new Vector3(centerOfMass.x/((float)count), centerOfMass.y/((float)count), centerOfMass.z/((float)count));
		WorldObject fakeTarget = new Obstacle();
		fakeTarget.setPosition(centerOfMass);
		super.setTarget(fakeTarget); // Establecemos el objetivo real.
		// Llamamos al padre.
		return super.getSteering();
	}
	
	// Este método nos va a permitir obtener simplemente la posición del centro de masas (para poder, por ejemplo, dibujar dicho punto).
	public Vector3 getCenterOfMass() {
		Vector3 centerOfMass = new Vector3(0.0f, 0.0f, 0.0f);
		int count = 0;
		
		for (WorldObject worldObject : targets) {
			// Calculamos la dirección y distancia hacia el objetivo.
			Vector3 direction = new Vector3(worldObject.getPosition());
			direction = direction.sub(super.getSource().getPosition());
			float distance = direction.len();
			// Si está dentro del radio especificado, se añade a 'centerOfMass'
			if (distance <= this.threshold) {
				centerOfMass = centerOfMass.add(new Vector3(worldObject.getPosition()));
				count++;
			}
		}
		
		if (count > 0) {
			centerOfMass = new Vector3(centerOfMass.x/((float)count), centerOfMass.y/((float)count), centerOfMass.z/((float)count));
		}
		
		return centerOfMass;
	}
}
