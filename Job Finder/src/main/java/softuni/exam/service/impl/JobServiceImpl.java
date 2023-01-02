package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportJobDTO;
import softuni.exam.models.dto.ImportJobRootDTO;
import softuni.exam.models.entity.Company;
import softuni.exam.models.entity.Job;
import softuni.exam.repository.JobRepository;
import softuni.exam.service.CompanyService;
import softuni.exam.service.JobService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final CompanyService companyService;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/xml/jobs.xml");

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, CompanyService companyService, ModelMapper modelMapper) throws JAXBException {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.modelMapper = modelMapper;
        JAXBContext context = JAXBContext.newInstance(ImportJobRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Override
    public boolean areImported() {
        return this.jobRepository.count() >0;
    }

    @Override
    public String readJobsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importJobs() throws IOException, JAXBException {
        ImportJobRootDTO jobs = (ImportJobRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return jobs.getJobs().stream()
                .map(this::importJob).collect(Collectors.joining("\n"));
    }

    private String importJob(ImportJobDTO job) {
        Set<ConstraintViolation<ImportJobDTO>> validate
                = this.validator.validate(job);
          if (validate.isEmpty()){
              Company company = this.companyService.findById(job.getCompanyId());
              Job jobToAdd = this.modelMapper.map(job,Job.class);
              jobToAdd.setCompany(company);
              this.jobRepository.save(jobToAdd);
              return String.format("Successfully imported job %s"
                      ,jobToAdd.getTitle());
          }else{
              return "Invalid job";
          }

    }

    @Override
    public String getBestJobs() {
        StringBuilder sb = new StringBuilder();
        double minSalary = 5000.00;
        double hoursAWeek = 30.00;
        List<Job> jobs = this.jobRepository
                .findAllBySalaryGreaterThanEqualAndHoursAWeekLessThanEqualOrderBySalaryDesc
                        (minSalary,hoursAWeek);
        for (Job job : jobs) {
            sb.append(String.format("Job title: %s%n" +
                    "-Salary: %.2f$%n" +
                    "--Hours a week: %.2fh.%n",job.getTitle(),job.getSalary(),job.getHoursAWeek()));
        }

        return sb.toString();
    }

    @Override
    public Job findById(Long id) {
        return this.jobRepository.findById(id).orElse(null);
    }
}
