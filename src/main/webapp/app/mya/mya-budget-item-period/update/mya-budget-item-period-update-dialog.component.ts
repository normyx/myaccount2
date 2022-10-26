import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Dayjs } from 'dayjs/esm';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { EventManager } from '../../../core/util/event-manager.service';
import { IBudgetItemPeriod, NewBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { IOperation } from '../../../entities/operation/operation.model';
import { EVENT_LOAD_OPERATIONS, EVENT_UPDATE_BUDGET_ITEM_ROW } from '../../config/mya.event.constants';
import { MyaOperationService } from '../../mya-operation/service/mya-operation.service';
import { MyaBudgetItemPeriodService } from '../service/mya-budget-item-period.service';
import { MyaBudgetItemPeriodFormGroup, MyaBudgetItemPeriodFormService } from './mya-budget-item-period-update-form.service';

@Component({
  selector: 'jhi-mya-budget-item-period-update-dialog',
  templateUrl: './mya-budget-item-period-update-dialog.component.html',
})
export class MyaBudgetItemPeriodUpdateDialogComponent {
  isSaving = false;
  budgetItemPeriod: IBudgetItemPeriod | null = null;
  budgetItem: IBudgetItem | null = null;

  operationsCollection: IOperation[] = [];

  editForm: MyaBudgetItemPeriodFormGroup = this.budgetItemPeriodFormService.createBudgetItemPeriodFormGroup();

  constructor(
    protected budgetItemPeriodService: MyaBudgetItemPeriodService,
    protected budgetItemPeriodFormService: MyaBudgetItemPeriodFormService,
    protected operationService: MyaOperationService,
    public activeModal: NgbActiveModal,
    private eventManager: EventManager
  ) {}

  compareOperation = (o1: IOperation | null, o2: IOperation | null): boolean => this.operationService.compareOperation(o1, o2);

  setBudgetItemPeriod(budgetItemPeriod: IBudgetItemPeriod, budgetItem: IBudgetItem): void {
    this.budgetItemPeriod = budgetItemPeriod;
    this.budgetItem = budgetItem;

    this.updateForm();

    this.loadRelationshipsOptions();
    this.selectOperation();
  }

  dismiss(): void {
    this.activeModal.dismiss();
  }

  save(): void {
    this.isSaving = true;
    const budgetItemPeriod = this.budgetItemPeriodFormService.convertBudgetItemPeriodUpdateToBudgetItemPeriod(
      this.budgetItemPeriodFormService.getBudgetItemPeriod(this.editForm)
    );
    if (budgetItemPeriod.isSmoothed) {
      budgetItemPeriod.date = null;
    }
    if (budgetItemPeriod.id != null) {
      let withNext = false;
      if (this.editForm.getRawValue().modifyNexts && this.budgetItemPeriod!.isRecurrent) {
        withNext = true;
      }

      this.subscribeToSaveResponse(
        this.budgetItemPeriodService.updateWithNext(budgetItemPeriod, this.editForm.getRawValue()!.dayInMonth!, withNext)
      );
    } else {
      const newBIP: NewBudgetItemPeriod = budgetItemPeriod;
      const newBI = { id: this.budgetItem!.id } as Pick<IBudgetItem, 'id' | 'name'>;
      newBIP.budgetItem = newBI;
      if (budgetItemPeriod.isSmoothed == null) {
        budgetItemPeriod.isSmoothed = false;
      }
      budgetItemPeriod.isRecurrent = false;
      this.subscribeToSaveResponse(this.budgetItemPeriodService.create(newBIP));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBudgetItemPeriod>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.eventManager.broadcast({ name: EVENT_UPDATE_BUDGET_ITEM_ROW + String(this.budgetItem?.id), content: 'OK' });
    this.eventManager.broadcast({ name: EVENT_LOAD_OPERATIONS, content: 'OK' });
    this.activeModal.dismiss();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(): void {
    if (this.budgetItemPeriod && this.budgetItem) {
      this.budgetItemPeriodFormService.resetForm(
        this.editForm,
        this.budgetItemPeriodFormService.convertBudgetItemPeriodToBudgetItemPeriodUpdate(this.budgetItemPeriod)
      );
    }
  }

  protected loadRelationshipsOptions(): void {
    if (!this.budgetItemPeriod!.isSmoothed) {
      this.loadOperations(this.budgetItemPeriod!.amount!, this.budgetItem!.category!.id, this.budgetItemPeriod!.date!);
    }
  }

  protected loadOperations(amount: number, categoryId: number, date: Dayjs): void {
    this.operationService.findOperationsCloseToBudgetItemPeriod(amount, categoryId, date).subscribe((res: HttpResponse<IOperation[]>) => {
      this.operationsCollection = res.body!;
    });
  }

  protected dataChange(): void {
    if (!this.editForm.get('isSmoothed')?.getRawValue() && this.editForm.get('amount')?.valid && this.editForm.get('dayInMonth')?.valid) {
      this.loadOperations(
        this.editForm.get('amount')?.getRawValue(),
        this.budgetItem!.category!.id,
        this.budgetItemPeriod!.month!.clone().date(this.editForm.get('dayInMonth')?.getRawValue())!
      );
    }
  }

  protected selectOperation(): void {
    if (this.editForm.getRawValue().operationId != null) {
      this.editForm.get('amount')?.disable();
      this.editForm.get('dayInMonth')?.disable();
      this.editForm.get('isSmoothed')?.setValue(false);
      this.editForm.get('isSmoothed')?.disable();
      this.setValuesFromOperation(this.editForm.getRawValue().operationId!);
    } else {
      this.editForm.get('amount')?.enable();
      this.editForm.get('dayInMonth')?.enable();
      this.editForm.get('isSmoothed')?.enable();
    }
  }

  protected setValuesFromOperation(operationId: number): void {
    const operation = this.operationsCollection.find(op => op.id === operationId);
    if (operation) {
      this.editForm.get('amount')?.setValue(operation.amount);
      this.editForm.get('dayInMonth')?.setValue(operation.date?.date());
    }
  }

  protected checkOperationDisable(operation: IOperation): string | null {
    return operation.budgetItemPeriod?.id != null && this.budgetItemPeriod?.id !== operation.budgetItemPeriod.id ? '' : null;
  }

  protected amountsEqual(a1: number | null | undefined, a2: number | null | undefined): boolean {
    if (!a1 || !a2) {
      return false;
    }
    return a1 === a2;
  }
}
