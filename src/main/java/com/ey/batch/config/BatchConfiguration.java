package com.ey.batch.config;

import com.ey.batch.dto.InputDto;
import com.ey.batch.dto.OutputDto;
import com.ey.batch.listener.JobCompletionNotificationListener;
import com.ey.batch.processor.InputItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

   
    

    @Bean
    public JdbcCursorItemReader<InputDto> itemReader(@Autowired DataSource dataSource) {
        
        return new JdbcCursorItemReaderBuilder<InputDto>()
                .name("cursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT firstName, lastName FROM Person ORDER BY firstName ASC")
                .verifyCursorPosition(false)
                .rowMapper(new BeanPropertyRowMapper<>(InputDto.class))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<OutputDto> itemWriter(@Autowired DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<OutputDto>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO People (fullName) VALUES (:fullName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job createPeopleJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory
                .get("createEmployeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(ItemReader<InputDto> reader, ItemWriter<OutputDto> writer,
                      InputItemProcessor processor) {
        return stepBuilderFactory
                .get("step1")
                .<InputDto, OutputDto>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch") ;
        simpleAsyncTaskExecutor.setConcurrencyLimit(5);
        return simpleAsyncTaskExecutor;
        

    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
