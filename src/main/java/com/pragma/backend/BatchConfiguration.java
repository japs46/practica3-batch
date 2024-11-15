package com.pragma.backend;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.pragma.backend.listener.JobListener;
import com.pragma.backend.model.Persona;
import com.pragma.backend.processor.PersonaItemProcesor;

@Configuration
public class BatchConfiguration {

	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Bean
	public FlatFileItemReader<Persona> reader(){
		return new FlatFileItemReaderBuilder<Persona>()
				.name("personaItemReader")
				.resource(new ClassPathResource("sample-data.csv"))
				.delimited()
				.names(new String[] {"nombre","apellido","telefono"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Persona>() {{
					setTargetType(Persona.class);
				}})
				.build();
	}
	
	@Bean
	public PersonaItemProcesor processor() {
		return new PersonaItemProcesor();
	}
	
	@Bean
	public JdbcBatchItemWriter<Persona> writer(DataSource dataSource){
		return new JdbcBatchItemWriterBuilder<Persona>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO persona (nombre,apellido,telefono) VALUES (:nombre,:apellido,:telefono)")
				.dataSource(dataSource)
				.build();
	}
	
	@Bean
	public Job importPersonaJob(JobListener jobListener, Step step1) {
		return new JobBuilder("importPersonaJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .flow(step1)
                .end()
                .build();
	}
	
	@Bean
	public Step step1(JdbcBatchItemWriter<Persona> writer) {
		return new StepBuilder("step1", jobRepository)
				.<Persona,Persona> chunk(10, transactionManager)
				.reader(reader())
				.processor(processor())
				.writer(writer)
				.build();
	}
	
}
