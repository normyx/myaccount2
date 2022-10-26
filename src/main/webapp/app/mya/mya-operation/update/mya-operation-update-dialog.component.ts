import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EventManager } from '../../../core/util/event-manager.service';
import { IOperation } from '../../../entities/operation/operation.model';
import { OperationService } from '../../../entities/operation/service/operation.service';
import { EVENT_LOAD_OPERATIONS } from '../../config/mya.event.constants';
import { MyaOperationUpdateFormGroup, MyaOperationUpdateFormService } from './mya-operation-update-form.service';

@Component({
  selector: 'jhi-mya-operation-update-dialog',
  templateUrl: './mya-operation-update-dialog.component.html',
})
export class MyaOperationUpdateDialogComponent {
  operation: IOperation | null = null;

  isSaving = false;

  editForm: MyaOperationUpdateFormGroup | null = null;

  constructor(
    protected operationService: OperationService,
    protected operationFormService: MyaOperationUpdateFormService,

    private activeModal: NgbActiveModal,
    private eventManager: EventManager
  ) {}

  setOperation(operation: IOperation): void {
    this.operation = operation;
    this.editForm = this.operationFormService.createOperationFormGroup(this.operation);
    this.updateForm(operation);
  }
  dismiss(): void {
    this.activeModal.dismiss();
  }
  save(): void {
    this.isSaving = true;
    const operation = this.operationFormService.getOperation(this.editForm!);
    this.subscribeToSaveResponse(this.operationService.update(operation));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOperation>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.eventManager.broadcast({ name: EVENT_LOAD_OPERATIONS, content: 'OK' });
    this.dismiss();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(operation: IOperation): void {
    this.operation = operation;
    this.operationFormService.resetForm(this.editForm!, operation);
  }
}
