package vn.thinher.quickjob.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.thinher.quickjob.domain.Company;
import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.domain.response.ResultPaginationDTO;
import vn.thinher.quickjob.repository.CompanyRepository;
import vn.thinher.quickjob.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleFetchAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(specification, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(companyPage.getContent());
        return result;
    }

    public Optional<Company> handleFetchCompanyById(long id) {
        return this.companyRepository.findById(id);
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
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            List<User> users = this.userRepository.findByCompany(companyOptional.get());
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}
