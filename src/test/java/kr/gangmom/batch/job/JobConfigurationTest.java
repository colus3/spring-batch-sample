package kr.gangmom.batch.job;

import kr.gangmom.batch.TestBatchConfig;
import kr.gangmom.batch.model.Sample;
import kr.gangmom.batch.repository.SampleRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes={JobConfiguration.class, TestBatchConfig.class})
class JobConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private SampleRepository sampleRepository;

    @Test
    public void sampleTest() throws Exception {
        //given
        for (long i = 0; i < 100; i++) {
            sampleRepository.save(new Sample(null, "test", LocalDateTime.now()));
        }
        List<Sample> samples = IntStream.range(0, 100).boxed()
                .map(i -> new Sample(null, "test" + i, LocalDateTime.now()))
                .collect(toList());
        sampleRepository.saveAll(samples);

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(sampleRepository.findAll().size()).isEqualTo(100);

    }

}