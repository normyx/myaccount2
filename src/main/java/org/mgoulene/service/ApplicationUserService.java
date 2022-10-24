package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.repository.ApplicationUserRepository;
import org.mgoulene.repository.search.ApplicationUserSearchRepository;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.mapper.ApplicationUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ApplicationUser}.
 */
@Service
@Transactional
public class ApplicationUserService {

    private final Logger log = LoggerFactory.getLogger(ApplicationUserService.class);

    private final ApplicationUserRepository applicationUserRepository;

    protected final ApplicationUserMapper applicationUserMapper;

    private final ApplicationUserSearchRepository applicationUserSearchRepository;

    public ApplicationUserService(
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserMapper applicationUserMapper,
        ApplicationUserSearchRepository applicationUserSearchRepository
    ) {
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserMapper = applicationUserMapper;
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }

    /**
     * Save a applicationUser.
     *
     * @param applicationUserDTO the entity to save.
     * @return the persisted entity.
     */
    public ApplicationUserDTO save(ApplicationUserDTO applicationUserDTO) {
        log.debug("Request to save ApplicationUser : {}", applicationUserDTO);
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDTO);
        applicationUser = applicationUserRepository.save(applicationUser);
        ApplicationUserDTO result = applicationUserMapper.toDto(applicationUser);
        applicationUserSearchRepository.index(applicationUser);
        return result;
    }

    /**
     * Update a applicationUser.
     *
     * @param applicationUserDTO the entity to save.
     * @return the persisted entity.
     */
    public ApplicationUserDTO update(ApplicationUserDTO applicationUserDTO) {
        log.debug("Request to update ApplicationUser : {}", applicationUserDTO);
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDTO);
        applicationUser = applicationUserRepository.save(applicationUser);
        ApplicationUserDTO result = applicationUserMapper.toDto(applicationUser);
        applicationUserSearchRepository.index(applicationUser);
        return result;
    }

    /**
     * Partially update a applicationUser.
     *
     * @param applicationUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO) {
        log.debug("Request to partially update ApplicationUser : {}", applicationUserDTO);

        return applicationUserRepository
            .findById(applicationUserDTO.getId())
            .map(existingApplicationUser -> {
                applicationUserMapper.partialUpdate(existingApplicationUser, applicationUserDTO);

                return existingApplicationUser;
            })
            .map(applicationUserRepository::save)
            .map(savedApplicationUser -> {
                applicationUserSearchRepository.save(savedApplicationUser);

                return savedApplicationUser;
            })
            .map(applicationUserMapper::toDto);
    }

    /**
     * Get all the applicationUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ApplicationUsers");
        return applicationUserRepository.findAll(pageable).map(applicationUserMapper::toDto);
    }

    /**
     * Get all the applicationUsers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ApplicationUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return applicationUserRepository.findAllWithEagerRelationships(pageable).map(applicationUserMapper::toDto);
    }

    /**
     * Get one applicationUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApplicationUserDTO> findOne(Long id) {
        log.debug("Request to get ApplicationUser : {}", id);
        return applicationUserRepository.findOneWithEagerRelationships(id).map(applicationUserMapper::toDto);
    }

    /**
     * Delete the applicationUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ApplicationUser : {}", id);
        applicationUserRepository.deleteById(id);
        applicationUserSearchRepository.deleteById(id);
    }

    /**
     * Search for the applicationUser corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationUserDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ApplicationUsers for query {}", query);
        return applicationUserSearchRepository.search(query, pageable).map(applicationUserMapper::toDto);
    }
}
