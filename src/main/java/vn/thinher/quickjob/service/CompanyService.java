package vn.thinher.quickjob.service;

import java.util.List;
import java.util.Optional;

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

    public List<Company> handleFetchAllCompanies() {
        return this.companyRepository.findAll();
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
        if (companyOptional.isPresent()) {
            Company existingCompany = companyOptional.get();
            existingCompany.setName(company.getName());
            existingCompany.setDescription(company.getDescription());
            existingCompany.setAddress(company.getAddress());
            existingCompany.setLogo(company.getLogo());
            return this.companyRepository.save(existingCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }
}
