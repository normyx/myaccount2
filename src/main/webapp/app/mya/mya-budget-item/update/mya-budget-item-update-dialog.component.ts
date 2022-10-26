import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EVENT_UPDATE_BUDGET_ITEM_ROW } from '../../config/mya.event.constants';
import { EventManager } from '../../../core/util/event-manager.service';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { BudgetItemService } from '../../../entities/budget-item/service/budget-item.service';
import { MyaBudgetItemUpdateFormGroup, MyaBudgetItemUpdateFormService } from './mya-budget-item-update-form.service';

@Component({
  selector: 'jhi-mya-budget-item-update-dialog',
  templateUrl: './mya-budget-item-update-dialog.component.html',
})
export class MyaBudgetItemUpdateDialogComponent {
  budgetItem: IBudgetItem | null = null;

  isSaving = false;

  editForm: MyaBudgetItemUpdateFormGroup | null = null;

  constructor(
    protected budgetItemService: BudgetItemService,
    protected budgetItemFormService: MyaBudgetItemUpdateFormService,

    private activeModal: NgbActiveModal,
    private eventManager: EventManager
  ) {}

  setBudgetItem(budgetItem: IBudgetItem): void {
    this.budgetItem = budgetItem;

    this.editForm = this.budgetItemFormService.createBudgetItemFormGroup(this.budgetItem);
    this.updateForm(budgetItem);
  }
  dismiss(): void {
    this.activeModal.dismiss();
  }
  save(): void {
    this.isSaving = true;
    const budgetItem = this.budgetItemFormService.getBudgetItem(this.editForm!);
    this.subscribeToSaveResponse(this.budgetItemService.update(budgetItem));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBudgetItem>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.eventManager.broadcast({ name: EVENT_UPDATE_BUDGET_ITEM_ROW + String(this.budgetItem?.id), content: 'OK' });
    this.dismiss();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(budgetItem: IBudgetItem): void {
    this.budgetItem = budgetItem;
    this.budgetItemFormService.resetForm(this.editForm!, budgetItem);
  }
}
