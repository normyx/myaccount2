package org.mgoulene.mya.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mgoulene.mya.service.dto.OperationCSVDTO;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.mapper.EntityMapper;

/**
 * Mapper for the entity Operation and its DTO OperationDTO.
 */
@Mapper(componentModel = "spring")
public interface OperationCSVMapper extends EntityMapper<OperationDTO, OperationCSVDTO> {
    @Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "bankAccount", target = "bankAccount")
    @Mapping(source = "account", target = "account")
    @Mapping(source = "subCategory", target = "subCategory")
    OperationDTO toDto(OperationCSVDTO operation);

    OperationCSVDTO toEntity(OperationDTO operationDTO);
}
