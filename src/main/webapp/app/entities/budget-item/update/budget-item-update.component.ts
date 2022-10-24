import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BudgetItemFormService, BudgetItemFormGroup } from './budget-item-form.service';
import { IBudgetItem } from '../budget-item.model';
import { BudgetItemService } from '../service/budget-item.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';

@Component({
  selector: 'jhi-budget-item-update',
  templateUrl: './budget-item-update.component.html',
})
export class BudgetItemUpdateComponent implements OnInit {
  isSaving = false;
  budgetItem: IBudgetItem | null = null;

  categoriesSharedCollection: ICategory[] = [];
  applicationUsersSharedCollection: IApplicationUser[] = [];

  editForm: BudgetItemFormGroup = this.budgetItemFormService.createBudgetItemFormGroup();

  constructor(
    protected budgetItemService: BudgetItemService,
    protected budgetItemFormService: BudgetItemFormService,
    protected categoryService: CategoryService,
    protected applicationUserService: ApplicationUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  compareApplicationUser = (o1: IApplicationUser | null, o2: IApplicationUser | null): boolean =>
    this.applicationUserService.compareApplicationUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ budgetItem }) => {
      this.budgetItem = budgetItem;
      if (budgetItem) {
        this.updateForm(budgetItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const budgetItem = this.budgetItemFormService.getBudgetItem(this.editForm);
    if (budgetItem.id !== null) {
      this.subscribeToSaveResponse(this.budgetItemService.update(budgetItem));
    } else {
      this.subscribeToSaveResponse(this.budgetItemService.create(budgetItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBudgetItem>>): void {
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

  protected updateForm(budgetItem: IBudgetItem): void {
    this.budgetItem = budgetItem;
    this.budgetItemFormService.resetForm(this.editForm, budgetItem);

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing<ICategory>(
      this.categoriesSharedCollection,
      budgetItem.category
    );
    this.applicationUsersSharedCollection = this.applicationUserService.addApplicationUserToCollectionIfMissing<IApplicationUser>(
      this.applicationUsersSharedCollection,
      budgetItem.account
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, this.budgetItem?.category)
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));

    this.applicationUserService
      .query()
      .pipe(map((res: HttpResponse<IApplicationUser[]>) => res.body ?? []))
      .pipe(
        map((applicationUsers: IApplicationUser[]) =>
          this.applicationUserService.addApplicationUserToCollectionIfMissing<IApplicationUser>(applicationUsers, this.budgetItem?.account)
        )
      )
      .subscribe((applicationUsers: IApplicationUser[]) => (this.applicationUsersSharedCollection = applicationUsers));
  }
}
