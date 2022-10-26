import { Component } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { EventManager } from 'app/core/util/event-manager.service';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { EVENT_UPDATE_BUDGET_ITEM_ROW } from '../../config/mya.event.constants';
import { MyaBudgetItemPeriodService } from '../service/mya-budget-item-period.service';

@Component({
  templateUrl: './mya-budget-item-period-delete-dialog.component.html',
})
export class MyaBudgetItemPeriodDeleteDialogComponent {
  budgetItemPeriod?: IBudgetItemPeriod;

  constructor(
    protected budgetItemPeriodService: MyaBudgetItemPeriodService,
    protected activeModal: NgbActiveModal,
    private eventManager: EventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.budgetItemPeriodService.deleteBudgetItemPeriodWithNext(id).subscribe(() => {
      this.eventManager.broadcast({ name: EVENT_UPDATE_BUDGET_ITEM_ROW + String(this.budgetItemPeriod?.budgetItem?.id), content: 'OK' });
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
