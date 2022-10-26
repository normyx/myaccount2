import { HttpResponse } from '@angular/common/http';
import dayjs from 'dayjs/esm';
import { DATE_FORMAT } from '../../../config/input.constants';
import { IOperation } from '../../../entities/operation/operation.model';
import { RestOperation } from '../../../entities/operation/service/operation.service';

export interface IOperationImportActions {
  operationsToCreate: IOperationToCheck[] | null;
  operationsToDelete: IOperationToCheck[] | null;
  operationsToDeleteWithHardLock: IOperationToCheck[] | null;
  operationsToUpdate: IOperationSourceAndTarget[] | null;
  operationsNotClosed: IOperationSourceAndTarget[] | null;
}

export interface RestOperationImportActions {
  operationsToCreate: RestOfOperationToCheck[] | null;
  operationsToDelete: RestOfOperationToCheck[] | null;
  operationsToDeleteWithHardLock: RestOfOperationToCheck[] | null;
  operationsToUpdate: RestOperationSourceAndTarget[] | null;
  operationsNotClosed: RestOperationSourceAndTarget[] | null;
}

export interface IOperationToCheck extends IOperation {
  checked?: boolean | null;
  checkHardLock?: boolean | null;
}

export interface IOperationSourceAndTarget {
  checked?: boolean | null;
  source?: IOperation | null;
  target?: IOperation | null;
}

export interface RestOperationSourceAndTarget {
  checked?: boolean | null;
  source?: RestOperation | null;
  target?: RestOperation | null;
}

export type RestOfOperationToCheck = Omit<IOperationToCheck, 'date'> & {
  date?: string | null;
};

export function convertRestImportOperationActionsFromServer(rest: RestOperationImportActions): IOperationImportActions {
  return {
    operationsToCreate: rest.operationsToCreate
      ? rest.operationsToCreate.map(op => ({
          ...op,
          checked: true,
          date: op.date ? dayjs(op.date) : undefined,
        }))
      : null,
    operationsToDelete: rest.operationsToDelete
      ? rest.operationsToDelete.map(op => ({
          ...op,
          checked: false,
          hardLock: false,
          date: op.date ? dayjs(op.date) : undefined,
        }))
      : null,
    operationsToDeleteWithHardLock: rest.operationsToDeleteWithHardLock
      ? rest.operationsToDeleteWithHardLock.map(op => ({
          ...op,
          checked: false,
          date: op.date ? dayjs(op.date) : undefined,
        }))
      : null,
    operationsToUpdate: rest.operationsToUpdate
      ? rest.operationsToUpdate.map(item => ({
          checked: true,
          source: item.source ? { ...item.source, date: item.source.date ? dayjs(item.source.date) : undefined } : null,
          target: item.target ? { ...item.target, date: item.target.date ? dayjs(item.target.date) : undefined } : null,
        }))
      : null,
    operationsNotClosed: rest.operationsNotClosed
      ? rest.operationsNotClosed.map(item => ({
          checked: true,
          source: item.source ? { ...item.source, date: item.source.date ? dayjs(item.source.date) : undefined } : null,
          target: item.target ? { ...item.target, date: item.target.date ? dayjs(item.target.date) : undefined } : null,
        }))
      : null,
  };
}

export function convertImportOperationActionsFromClient(op: IOperationImportActions): RestOperationImportActions {
  return {
    operationsToCreate: op.operationsToCreate
      ? op.operationsToCreate.map(item => ({
          ...item,
          date: item.date?.format(DATE_FORMAT) ?? null,
        }))
      : null,
    operationsToDelete: op.operationsToDelete
      ? op.operationsToDelete.map(item => ({
          ...item,
          date: item.date?.format(DATE_FORMAT) ?? null,
        }))
      : null,
    operationsToDeleteWithHardLock: op.operationsToDeleteWithHardLock
      ? op.operationsToDeleteWithHardLock.map(item => ({
          ...item,
          date: item.date?.format(DATE_FORMAT) ?? null,
        }))
      : null,
    operationsToUpdate: op.operationsToUpdate
      ? op.operationsToUpdate.map(item => ({
          ...item,
          source: item.source ? { ...item.source, date: item.source.date?.format(DATE_FORMAT) ?? null } : null,
          target: item.target ? { ...item.target, date: item.target.date?.format(DATE_FORMAT) ?? null } : null,
        }))
      : null,
    operationsNotClosed: op.operationsNotClosed
      ? op.operationsNotClosed.map(item => ({
          ...item,
          source: item.source ? { ...item.source, date: item.source.date?.format(DATE_FORMAT) ?? null } : null,
          target: item.target ? { ...item.target, date: item.target.date?.format(DATE_FORMAT) ?? null } : null,
        }))
      : null,
  };
}

export function filterImportOperationActionsForServer(actions: RestOperationImportActions): RestOperationImportActions {
  if (actions.operationsToCreate) {
    actions.operationsToCreate = actions.operationsToCreate.filter(item => item.checked);
  }
  if (actions.operationsToUpdate) {
    actions.operationsToUpdate = actions.operationsToUpdate.filter(item => item.checked);
  }
  if (actions.operationsNotClosed) {
    if (actions.operationsToUpdate) {
      actions.operationsToUpdate = actions.operationsToUpdate.concat(actions.operationsNotClosed.filter(item => item.checked));
    } else {
      actions.operationsToUpdate = actions.operationsNotClosed.filter(item => item.checked);
    }
    actions.operationsNotClosed = new Array<RestOfOperationToCheck>();
  }
  if (actions.operationsToDelete) {
    const operationToHardLock = actions.operationsToDelete.filter(item => item.checkHardLock);
    actions.operationsToDelete = actions.operationsToDelete.filter(item => item.checked);
    if (actions.operationsToDeleteWithHardLock) {
      actions.operationsToDelete = actions.operationsToDelete.concat(actions.operationsToDeleteWithHardLock.filter(item => item.checked));
    }
    actions.operationsToDeleteWithHardLock = operationToHardLock;
  }
  return actions;
}

export function convertResponseImportOperationActionsFromServer(
  rest: HttpResponse<RestOperationImportActions>
): HttpResponse<IOperationImportActions> {
  return rest.clone({
    body: rest.body ? convertRestImportOperationActionsFromServer(rest.body) : null,
  });
}
