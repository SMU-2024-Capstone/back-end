package capstone.courseweb.ai;
/*
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class FileReaderJobConfig {

    private final CsvReader csvReader;
    private final CsvPlaceWriter csvPlaceWriter;

    private static final int chunkSize = 785;

    @Bean
    public Job csvPlaceJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("csvPlaceJob", jobRepository)
                .start(csvPlaceReaderStep(jobRepository, transactionManager))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step csvPlaceReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csvPlaceReaderStep", jobRepository)
                .<AIPlaceDto, AIPlaceDto>chunk(chunkSize, transactionManager) // Use the transaction manager here
                .reader(csvReader.csvPlaceReader())
                .writer(csvPlaceWriter)
                .allowStartIfComplete(true)
                .build();
    }

    // Ensure you have a PlatformTransactionManager bean defined in your context
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource()); // or any other transaction manager you are using
    }

    // You will also need a DataSource bean if you haven't defined it yet
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;

    }
}

 */
