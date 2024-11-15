package com.pragma.backend.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.pragma.backend.model.Persona;

@Component
public class JobListener implements JobExecutionListener{
	
	private final Logger LOG = LoggerFactory.getLogger(JobListener.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus()==BatchStatus.COMPLETED) {
			LOG.info("FINALIZO EL JOB!! Verifica los resultados: ");
			
			jdbcTemplate.query("SELECT nombre,apellido,telefono FROM persona", 
					(rs,row)-> new Persona(rs.getString(1), rs.getString(2), rs.getString(3)))
			.forEach(persona -> LOG.info("Registro < "+persona+" >"));
		}
	}
	
}
