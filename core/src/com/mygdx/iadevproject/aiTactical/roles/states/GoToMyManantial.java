package com.mygdx.iadevproject.aiTactical.roles.states;

import java.util.Map;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.iadevproject.IADeVProject;
import com.mygdx.iadevproject.aiReactive.behaviour.Behaviour;
import com.mygdx.iadevproject.aiReactive.pathfinding.pointToPoint.PointToPoint_PathFinding;
import com.mygdx.iadevproject.checksAndActions.Actions;
import com.mygdx.iadevproject.model.Character;

public class GoToMyManantial implements State<Character> {

	private PointToPoint_PathFinding pf; 	// Objeto pathfinding para ir hacia mi base
	
	public GoToMyManantial() { /* empty constructor */ }

	@Override
	public void update(Character entity) {
		
		/** IMPORTANTE: SOLAMENTE SE INCLUYE EL NO CHOCAR. */
		
		// Obtenemos los comportamientos para no colisionar
		Map<Float, Behaviour> behaviours = Actions.notCollide(200.0f, entity);
		// Obtenemos los comportamientos para patrullar la base
		Map<Float, Behaviour> goToMyManantial = Actions.goTo(50.0f, entity, pf, 50.0f);
		// Juntamos ambos comportamientos
		behaviours.putAll(goToMyManantial);
		
		// Establecemos los nuevos comportamientos al personaje.
		entity.setListBehaviour(behaviours);
	}

	@Override
	public void enter(Character entity) { 
		// Cuando entramos a este estado, calculamos el pathfinding hacia el manantial
		pf = Actions.createPathFinding(entity, IADeVProject.getPositionOfTeamManantial(entity.getTeam())); 
	}

	@Override
	public void exit(Character entity) { /* empty method */ }

	@Override
	public boolean onMessage(Character entity, Telegram telegram) { return false; }

}
