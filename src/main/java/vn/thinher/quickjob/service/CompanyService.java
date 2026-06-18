package vn.thinher.quickjob.service;

import org.springframework.stereotype.Service;
import vn.thinher.quickjob.domain.Company;
import vn.thinher.quickjob.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }
}
