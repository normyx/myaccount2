package org.mgoulene.mya.service.dto;

import java.io.Serializable;
import org.mgoulene.service.dto.OperationDTO;

public class MyaOperationToUpdate implements Serializable {

    private OperationDTO source;
    private OperationDTO target;

    public MyaOperationToUpdate() {}

    public MyaOperationToUpdate(OperationDTO source, OperationDTO target) {
        this.source = source;
        this.target = target;
    }

    public OperationDTO getSource() {
        return source;
    }

    public void setSource(OperationDTO source) {
        this.source = source;
    }

    public OperationDTO getTarget() {
        return target;
    }

    public void setTarget(OperationDTO target) {
        this.target = target;
    }
}
