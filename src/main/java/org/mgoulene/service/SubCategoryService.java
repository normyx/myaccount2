package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mgoulene.domain.SubCategory;
import org.mgoulene.repository.SubCategoryRepository;
import org.mgoulene.repository.search.SubCategorySearchRepository;
import org.mgoulene.service.dto.SubCategoryDTO;
import org.mgoulene.service.mapper.SubCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SubCategory}.
 */
@Service
@Transactional
public class SubCategoryService {

    private final Logger log = LoggerFactory.getLogger(SubCategoryService.class);

    private final SubCategoryRepository subCategoryRepository;

    private final SubCategoryMapper subCategoryMapper;

    private final SubCategorySearchRepository subCategorySearchRepository;

    public SubCategoryService(
        SubCategoryRepository subCategoryRepository,
        SubCategoryMapper subCategoryMapper,
        SubCategorySearchRepository subCategorySearchRepository
    ) {
        this.subCategoryRepository = subCategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
        this.subCategorySearchRepository = subCategorySearchRepository;
    }

    /**
     * Save a subCategory.
     *
     * @param subCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public SubCategoryDTO save(SubCategoryDTO subCategoryDTO) {
        log.debug("Request to save SubCategory : {}", subCategoryDTO);
        SubCategory subCategory = subCategoryMapper.toEntity(subCategoryDTO);
        subCategory = subCategoryRepository.save(subCategory);
        SubCategoryDTO result = subCategoryMapper.toDto(subCategory);
        subCategorySearchRepository.index(subCategory);
        return result;
    }

    /**
     * Update a subCategory.
     *
     * @param subCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public SubCategoryDTO update(SubCategoryDTO subCategoryDTO) {
        log.debug("Request to update SubCategory : {}", subCategoryDTO);
        SubCategory subCategory = subCategoryMapper.toEntity(subCategoryDTO);
        subCategory = subCategoryRepository.save(subCategory);
        SubCategoryDTO result = subCategoryMapper.toDto(subCategory);
        subCategorySearchRepository.index(subCategory);
        return result;
    }

    /**
     * Partially update a subCategory.
     *
     * @param subCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SubCategoryDTO> partialUpdate(SubCategoryDTO subCategoryDTO) {
        log.debug("Request to partially update SubCategory : {}", subCategoryDTO);

        return subCategoryRepository
            .findById(subCategoryDTO.getId())
            .map(existingSubCategory -> {
                subCategoryMapper.partialUpdate(existingSubCategory, subCategoryDTO);

                return existingSubCategory;
            })
            .map(subCategoryRepository::save)
            .map(savedSubCategory -> {
                subCategorySearchRepository.save(savedSubCategory);

                return savedSubCategory;
            })
            .map(subCategoryMapper::toDto);
    }

    /**
     * Get all the subCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SubCategoryDTO> findAll() {
        log.debug("Request to get all SubCategories");
        return subCategoryRepository.findAll().stream().map(subCategoryMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the subCategories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SubCategoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return subCategoryRepository.findAllWithEagerRelationships(pageable).map(subCategoryMapper::toDto);
    }

    /**
     * Get one subCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SubCategoryDTO> findOne(Long id) {
        log.debug("Request to get SubCategory : {}", id);
        return subCategoryRepository.findOneWithEagerRelationships(id).map(subCategoryMapper::toDto);
    }

    /**
     * Delete the subCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SubCategory : {}", id);
        subCategoryRepository.deleteById(id);
        subCategorySearchRepository.deleteById(id);
    }

    /**
     * Search for the subCategory corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SubCategoryDTO> search(String query) {
        log.debug("Request to search SubCategories for query {}", query);
        return StreamSupport
            .stream(subCategorySearchRepository.search(query).spliterator(), false)
            .map(subCategoryMapper::toDto)
            .collect(Collectors.toList());
    }
}
