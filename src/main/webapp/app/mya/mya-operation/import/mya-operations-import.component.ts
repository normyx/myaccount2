import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Dayjs } from 'dayjs';
import { MyaFileUploadModel } from '../../util/mya-file-upload.service';
import { MyaOperationService } from '../service/mya-operation.service';
import {
  convertImportOperationActionsFromClient,
  filterImportOperationActionsForServer,
  IOperationImportActions,
  RestOperationImportActions,
} from './mya-operation-import-action.model';

@Component({
  selector: 'jhi-mya-operations-import',
  templateUrl: './mya-operations-import.component.html',
})
export class MyaOperationsImportComponent {
  isLoading = false;

  fileModel: MyaFileUploadModel | null = null;

  actions: IOperationImportActions | null = null;

  operationToCreateVisible = true;
  operationToUpdateVisible = true;
  operationNotClosedVisible = true;
  operationToDeleteVisible = false;
  operationToDeleteWithHardLockVisible = false;

  constructor(protected myaOperationService: MyaOperationService, private router: Router) {}

  handleFileInput(event: any): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.fileModel = new MyaFileUploadModel();
      this.fileModel.data = file;
    }
  }

  uploadFile(): void {
    if (this.fileModel?.data) {
      this.isLoading = true;
      this.myaOperationService.uploadOperations(this.fileModel).subscribe((res: HttpResponse<IOperationImportActions>) => {
        this.actions = res.body;
        this.isLoading = false;
      });
    }
  }

  validateImport(): void {
    this.isLoading = true;
    const rest: RestOperationImportActions = filterImportOperationActionsForServer(convertImportOperationActionsFromClient(this.actions!));
    this.myaOperationService.validateOperationsImport(rest).subscribe(() => {
      this.isLoading = false;
      this.router.navigate(['/mya/mya-operation']);
    });
  }

  switchOperationToCreateVisible(): void {
    this.operationToCreateVisible = !this.operationToCreateVisible;
  }

  checkOperationToCreate(): void {
    this.actions?.operationsToCreate?.forEach(ope => {
      ope.checked = true;
    });
  }

  switchOperationToUpdateVisible(): void {
    this.operationToUpdateVisible = !this.operationToUpdateVisible;
  }

  checkOperationToUpdate(): void {
    this.actions?.operationsToUpdate?.forEach(ope => {
      ope.checked = true;
    });
  }
  switchOperationNotClosedVisible(): void {
    this.operationNotClosedVisible = !this.operationNotClosedVisible;
  }

  checkOperationNotClosed(): void {
    this.actions?.operationsNotClosed?.forEach(ope => {
      ope.checked = true;
    });
  }

  switchOperationToDeleteVisible(): void {
    this.operationToDeleteVisible = !this.operationToDeleteVisible;
  }

  checkOperationToDelete(): void {
    this.actions?.operationsToDelete?.forEach(ope => {
      if (!ope.budgetItemPeriod) {
        ope.checked = true;
      }
    });
  }
  hardLockOperationToDelete(): void {
    this.actions?.operationsToDelete?.forEach(ope => {
      if (!ope.checked) {
        ope.checkHardLock = true;
      }
    });
  }

  switchOperationToDeleteWithHardLockVisible(): void {
    this.operationToDeleteWithHardLockVisible = !this.operationToDeleteWithHardLockVisible;
  }

  checkOperationToDeleteWithHardLock(): void {
    this.actions?.operationsToDeleteWithHardLock?.forEach(ope => {
      ope.checked = true;
    });
  }

  checkDifferent(o1: any, o2: any): boolean {
    return o1 !== o2;
  }
  checkDifferentDate(o1: Dayjs | null | undefined, o2: Dayjs | null | undefined): boolean {
    if (o1 && o2) {
      return o1.diff(o2, 'days') !== 0;
    } else {
      return false;
    }
  }
}
