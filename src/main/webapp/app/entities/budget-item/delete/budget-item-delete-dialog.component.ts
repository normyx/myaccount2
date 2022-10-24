import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBudgetItem } from '../budget-item.model';
import { BudgetItemService } from '../service/budget-item.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './budget-item-delete-dialog.component.html',
})
export class BudgetItemDeleteDialogComponent {
  budgetItem?: IBudgetItem;

  constructor(protected budgetItemService: BudgetItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.budgetItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
