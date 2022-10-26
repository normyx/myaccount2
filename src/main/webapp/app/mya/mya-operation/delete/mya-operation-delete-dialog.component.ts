import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ITEM_DELETED_EVENT } from '../../../config/navigation.constants';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { BudgetItemPeriodService } from '../../../entities/budget-item-period/service/budget-item-period.service';
import { IOperation } from '../../../entities/operation/operation.model';
import { MyaOperationService } from '../service/mya-operation.service';

@Component({
  templateUrl: './mya-operation-delete-dialog.component.html',
})
export class MyaOperationDeleteDialogComponent {
  operation?: IOperation;
  budgetItemPeriod: IBudgetItemPeriod | null = null;

  constructor(
    protected operationService: MyaOperationService,
    protected activeModal: NgbActiveModal,
    protected budgetItemPeriodService: BudgetItemPeriodService
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.operationService.deleteWithBudgetItemPeriod(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }

  setOperation(operation: IOperation): void {
    this.operation = operation;
    if (this.operation.budgetItemPeriod) {
      this.budgetItemPeriodService.find(this.operation.budgetItemPeriod.id).subscribe((bip: HttpResponse<IBudgetItemPeriod>) => {
        this.budgetItemPeriod = bip.body;
      });
    }
  }
}
