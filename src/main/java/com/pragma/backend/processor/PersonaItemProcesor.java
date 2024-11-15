package com.pragma.backend.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.pragma.backend.model.Persona;

@Component
public class PersonaItemProcesor implements ItemProcessor<Persona, Persona>{
	
	private final Logger LOG = LoggerFactory.getLogger(PersonaItemProcesor.class);

	@Override
	public Persona process(Persona item) throws Exception {
		LOG.info("Inicio conversion!!!!!");
		String nombre= item.getNombre().toUpperCase();
		String apellido= item.getApellido().toUpperCase();
		String telefono= item.getTelefono();
		
		Persona persona= new Persona(nombre, apellido, telefono);
		
		LOG.info("Conversion de ("+item+") a ("+persona+")");
		
		return persona;
	}

}
