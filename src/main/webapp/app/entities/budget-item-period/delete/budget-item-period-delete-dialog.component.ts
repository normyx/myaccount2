import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBudgetItemPeriod } from '../budget-item-period.model';
import { BudgetItemPeriodService } from '../service/budget-item-period.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './budget-item-period-delete-dialog.component.html',
})
export class BudgetItemPeriodDeleteDialogComponent {
  budgetItemPeriod?: IBudgetItemPeriod;

  constructor(protected budgetItemPeriodService: BudgetItemPeriodService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.budgetItemPeriodService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
