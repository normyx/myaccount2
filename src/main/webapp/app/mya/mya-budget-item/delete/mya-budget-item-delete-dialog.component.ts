import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITEM_DELETED_EVENT } from '../../../config/navigation.constants';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { MyaBudgetItemService } from '../service/mya-budget-item.service';

@Component({
  templateUrl: './mya-budget-item-delete-dialog.component.html',
})
export class MyaBudgetItemDeleteDialogComponent {
  budgetItem?: IBudgetItem;

  constructor(protected budgetItemService: MyaBudgetItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(): void {
    this.budgetItemService.deleteWithPeriods(this.budgetItem!).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
