package kr.gangmom.batch.job;

import kr.gangmom.batch.model.Sample;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final EntityManagerFactory entityManagerFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final int chunkSize = 1000;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job").start(step()).build();
    }

    @Bean
    @JobScope
    public Step step() {
        return stepBuilderFactory.get("step")
                .<Sample, Sample>chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public ItemStreamReader<Sample> reader() {

        return new JpaPagingItemReaderBuilder<Sample>()
                .queryString("SELECT s FROM Sample s")
                .pageSize(chunkSize)
                .entityManagerFactory(entityManagerFactory)
                .name("samplePagingReader")
                .build();
    }

    @Bean
    @StepScope
    public JpaItemWriter<Sample> writer() {
        JpaItemWriter<Sample> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

}
