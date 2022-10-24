package org.mgoulene.mya.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.mgoulene.service.dto.OperationDTO;

public class MyaImportOperationActions implements Serializable {

    private List<OperationDTO> operationsToCreate = new ArrayList<OperationDTO>();

    private List<MyaOperationToUpdate> operationsToUpdate = new ArrayList<MyaOperationToUpdate>();

    private List<MyaOperationToUpdate> operationsNotClosed = new ArrayList<MyaOperationToUpdate>();

    private List<OperationDTO> operationsToDeleteWithHardLock = new ArrayList<OperationDTO>();

    private List<OperationDTO> operationsToDelete = new ArrayList<OperationDTO>();

    public MyaImportOperationActions() {}

    public MyaImportOperationActions addOperationToDeleteWithHardLock(OperationDTO operation) {
        this.getOperationsToDeleteWithHardLock().add(operation);
        return this;
    }

    public List<OperationDTO> getOperationsToDeleteWithHardLock() {
        return operationsToDeleteWithHardLock;
    }

    public void setOperationsToDeleteWithHardLock(List<OperationDTO> operationsToDeleteWithHardLock) {
        this.operationsToDeleteWithHardLock = operationsToDeleteWithHardLock;
    }

    public List<MyaOperationToUpdate> getOperationsNotClosed() {
        return operationsNotClosed;
    }

    public void setOperationsNotClosed(List<MyaOperationToUpdate> operationsNotClosed) {
        this.operationsNotClosed = operationsNotClosed;
    }

    public List<OperationDTO> getOperationsToDelete() {
        return operationsToDelete;
    }

    public void setOperationsToDelete(List<OperationDTO> operationsToDelete) {
        this.operationsToDelete = operationsToDelete;
    }

    public MyaImportOperationActions addOperationNotClosed(OperationDTO source, OperationDTO target) {
        this.operationsNotClosed.add(new MyaOperationToUpdate(source, target));
        return this;
    }

    public MyaImportOperationActions addOperationToDelete(OperationDTO operation) {
        this.getOperationsToDelete().add(operation);
        return this;
    }

    public List<OperationDTO> getOperationsToCreate() {
        return operationsToCreate;
    }

    public void setOperationsToCreate(List<OperationDTO> operationsToCreate) {
        this.operationsToCreate = operationsToCreate;
    }

    public MyaImportOperationActions addOperationToCreate(OperationDTO operation) {
        this.getOperationsToCreate().add(operation);
        return this;
    }

    public List<MyaOperationToUpdate> getOperationsToUpdate() {
        return operationsToUpdate;
    }

    public void setOperationsToUpdate(List<MyaOperationToUpdate> operationsToUpdate) {
        this.operationsToUpdate = operationsToUpdate;
    }

    public MyaImportOperationActions addOperationToUpdate(OperationDTO source, OperationDTO target) {
        this.operationsToUpdate.add(new MyaOperationToUpdate(source, target));
        return this;
    }

    @Override
    public String toString() {
        String returnStr = "";
        returnStr += "operationsToCreate :\n------------------\n";
        for (OperationDTO op : operationsToCreate) {
            returnStr += "   " + op + "\n";
        }
        returnStr += "operationsToUpdate :\n------------------\n";
        for (MyaOperationToUpdate op : operationsToUpdate) {
            returnStr += "   source: " + op.getSource() + "\n";
            returnStr += "   target: " + op.getTarget() + "\n";
        }
        returnStr += "operationsNotClosed :\n------------------\n";
        for (MyaOperationToUpdate op : operationsNotClosed) {
            returnStr += "   source: " + op.getSource() + "\n";
            returnStr += "   target: " + op.getTarget() + "\n";
        }
        returnStr += "operationsToDeleteWithHardLock :\n------------------\n";
        for (OperationDTO op : operationsToDeleteWithHardLock) {
            returnStr += "   " + op + "\n";
        }
        returnStr += "operationsToDelete :\n------------------\n";
        for (OperationDTO op : operationsToDelete) {
            returnStr += "   " + op + "\n";
        }
        return returnStr;
    }
}
