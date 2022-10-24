package org.mgoulene.mya.service.mapper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.mgoulene.mya.service.dto.OperationCSVDTO;
import org.mgoulene.service.dto.OperationDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-09-06T17:54:48+0200",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.15.0.v20180830-1017, environment: Java 1.8.0_171 (Oracle Corporation)"
)
@Component
public class OperationCSVToDTO {

    public List<OperationDTO> toDto(List<OperationCSVDTO> entityList) {
        if (entityList == null) {
            return null;
        }

        List<OperationDTO> list = new ArrayList<OperationDTO>(entityList.size());
        for (OperationCSVDTO operationCSVDTO : entityList) {
            list.add(toDto(operationCSVDTO));
        }

        return list;
    }

    public OperationDTO toDto(OperationCSVDTO operation) {
        if (operation == null) {
            return null;
        }

        OperationDTO operationDTO = new OperationDTO();

        if (operation.getDate() != null) {
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            // LocalDate date = java.time.LocalDate.parse(operation.getDate(), formatter);
            // operationDTO.setDate(date);
        }
        if (operation.getAmount() != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            DecimalFormat format = new DecimalFormat("0.#");
            format.setDecimalFormatSymbols(symbols);
            /*
             * try {
             * operationDTO.setAmount(format.parse(operation.getAmount()).floatValue());
             * } catch (ParseException e) {
             * // TODO Auto-generated catch block
             * e.printStackTrace();
             * }
             */
        }
        operationDTO.setLabel(operation.getLabel());
        operationDTO.setNote(operation.getNote());
        operationDTO.setCheckNumber(operation.getCheckNumber());

        return operationDTO;
    }
}
