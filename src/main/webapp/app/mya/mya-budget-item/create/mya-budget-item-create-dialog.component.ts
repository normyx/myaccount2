import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BsDatepickerConfig, BsDatepickerViewMode } from 'ngx-bootstrap/datepicker';
import { setTheme } from 'ngx-bootstrap/utils';
import { IBudgetItem, NewBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { ICategory } from '../../../entities/category/category.model';
import { CategoryService } from '../../../entities/category/service/category.service';
import { MyaBudgetItemService } from '../service/mya-budget-item.service';
import { MyaBudgetItemCreateFormGroup, MyaBudgetItemCreateFormService } from './mya-budget-item-create-form.service';

@Component({
  selector: 'jhi-mya-budget-item-create-dialog',
  templateUrl: './mya-budget-item-create-dialog.component.html',
})
export class MyaBudgetItemCreateDialogComponent implements OnInit {
  isSaving = false;
  budgetItem: IBudgetItem | null = null;

  categoriesSharedCollection: ICategory[] = [];

  bsConfig?: Partial<BsDatepickerConfig>;
  minMode: BsDatepickerViewMode = 'month';

  editForm: MyaBudgetItemCreateFormGroup = this.budgetItemFormService.createBudgetItemFormGroup();

  constructor(
    protected budgetItemService: MyaBudgetItemService,
    protected budgetItemFormService: MyaBudgetItemCreateFormService,
    protected categoryService: CategoryService,
    private activeModal: NgbActiveModal
  ) {
    setTheme('bs5');
  }

  ngOnInit(): void {
    this.bsConfig = Object.assign(
      {},
      {
        minMode: this.minMode,
        containerClass: 'theme-default',
        isAnimated: true,
        dateInputFormat: 'MMM-YY',
      }
    );

    this.loadRelationshipsOptions();
  }

  dismiss(): void {
    this.activeModal.dismiss();
  }

  save(): void {
    this.isSaving = true;
    const budgetItemNew = this.budgetItemFormService.getBudgetItem(this.editForm);
    const budgetItem: NewBudgetItem = {
      id: null,
      category: { id: budgetItemNew.categoryId!, categoryName: '' },
      name: budgetItemNew.name,
    };
    this.subscribeToSaveResponse(
      this.budgetItemService.createWithBudgetItemPeriods(
        budgetItem,
        budgetItemNew.isSmoothed!,
        budgetItemNew.month!,
        budgetItemNew.amount!,
        budgetItemNew.dayInMonth!
      )
    );
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBudgetItem>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.activeModal.close();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(budgetItem: IBudgetItem): void {
    this.budgetItem = budgetItem;
    this.budgetItemFormService.resetForm(this.editForm, budgetItem);

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing<ICategory>(
      this.categoriesSharedCollection,
      budgetItem.category
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService.query().subscribe((res: HttpResponse<ICategory[]>) => (this.categoriesSharedCollection = res.body!));
  }
}
