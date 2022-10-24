import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BudgetItemPeriodFormService, BudgetItemPeriodFormGroup } from './budget-item-period-form.service';
import { IBudgetItemPeriod } from '../budget-item-period.model';
import { BudgetItemPeriodService } from '../service/budget-item-period.service';
import { IOperation } from 'app/entities/operation/operation.model';
import { OperationService } from 'app/entities/operation/service/operation.service';
import { IBudgetItem } from 'app/entities/budget-item/budget-item.model';
import { BudgetItemService } from 'app/entities/budget-item/service/budget-item.service';

@Component({
  selector: 'jhi-budget-item-period-update',
  templateUrl: './budget-item-period-update.component.html',
})
export class BudgetItemPeriodUpdateComponent implements OnInit {
  isSaving = false;
  budgetItemPeriod: IBudgetItemPeriod | null = null;

  operationsCollection: IOperation[] = [];
  budgetItemsSharedCollection: IBudgetItem[] = [];

  editForm: BudgetItemPeriodFormGroup = this.budgetItemPeriodFormService.createBudgetItemPeriodFormGroup();

  constructor(
    protected budgetItemPeriodService: BudgetItemPeriodService,
    protected budgetItemPeriodFormService: BudgetItemPeriodFormService,
    protected operationService: OperationService,
    protected budgetItemService: BudgetItemService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOperation = (o1: IOperation | null, o2: IOperation | null): boolean => this.operationService.compareOperation(o1, o2);

  compareBudgetItem = (o1: IBudgetItem | null, o2: IBudgetItem | null): boolean => this.budgetItemService.compareBudgetItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ budgetItemPeriod }) => {
      this.budgetItemPeriod = budgetItemPeriod;
      if (budgetItemPeriod) {
        this.updateForm(budgetItemPeriod);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const budgetItemPeriod = this.budgetItemPeriodFormService.getBudgetItemPeriod(this.editForm);
    if (budgetItemPeriod.id !== null) {
      this.subscribeToSaveResponse(this.budgetItemPeriodService.update(budgetItemPeriod));
    } else {
      this.subscribeToSaveResponse(this.budgetItemPeriodService.create(budgetItemPeriod));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBudgetItemPeriod>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(budgetItemPeriod: IBudgetItemPeriod): void {
    this.budgetItemPeriod = budgetItemPeriod;
    this.budgetItemPeriodFormService.resetForm(this.editForm, budgetItemPeriod);

    this.operationsCollection = this.operationService.addOperationToCollectionIfMissing<IOperation>(
      this.operationsCollection,
      budgetItemPeriod.operation
    );
    this.budgetItemsSharedCollection = this.budgetItemService.addBudgetItemToCollectionIfMissing<IBudgetItem>(
      this.budgetItemsSharedCollection,
      budgetItemPeriod.budgetItem
    );
  }

  protected loadRelationshipsOptions(): void {
    this.operationService
      .query({ 'budgetItemPeriodId.specified': 'false' })
      .pipe(map((res: HttpResponse<IOperation[]>) => res.body ?? []))
      .pipe(
        map((operations: IOperation[]) =>
          this.operationService.addOperationToCollectionIfMissing<IOperation>(operations, this.budgetItemPeriod?.operation)
        )
      )
      .subscribe((operations: IOperation[]) => (this.operationsCollection = operations));

    this.budgetItemService
      .query()
      .pipe(map((res: HttpResponse<IBudgetItem[]>) => res.body ?? []))
      .pipe(
        map((budgetItems: IBudgetItem[]) =>
          this.budgetItemService.addBudgetItemToCollectionIfMissing<IBudgetItem>(budgetItems, this.budgetItemPeriod?.budgetItem)
        )
      )
      .subscribe((budgetItems: IBudgetItem[]) => (this.budgetItemsSharedCollection = budgetItems));
  }
}
