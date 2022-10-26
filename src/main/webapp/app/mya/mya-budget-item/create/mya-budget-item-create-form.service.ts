import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { IMyaNewBudgetItem } from './mya-new-budget-item.model';

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBudgetItem for edit and NewBudgetItemFormGroupInput for create.
 */
type BudgetItemFormGroupInput = IMyaNewBudgetItem;

type MyaBudgetItemCreateFormGroupContent = {
  name: FormControl<IMyaNewBudgetItem['name']>;
  categoryId: FormControl<IMyaNewBudgetItem['categoryId']>;
  amount: FormControl<IMyaNewBudgetItem['amount']>;
  month: FormControl<IMyaNewBudgetItem['month']>;
  dayInMonth: FormControl<IMyaNewBudgetItem['dayInMonth']>;
  isSmoothed: FormControl<IMyaNewBudgetItem['isSmoothed']>;
};

export type MyaBudgetItemCreateFormGroup = FormGroup<MyaBudgetItemCreateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemCreateFormService {
  createBudgetItemFormGroup(budgetItem: BudgetItemFormGroupInput = {}): MyaBudgetItemCreateFormGroup {
    const budgetItemRawValue = {
      ...this.getFormDefaults(),
      ...budgetItem,
    };
    return new FormGroup<MyaBudgetItemCreateFormGroupContent>({
      name: new FormControl(budgetItemRawValue.name, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),

      categoryId: new FormControl(budgetItemRawValue.categoryId, {
        validators: [Validators.required],
      }),
      amount: new FormControl(budgetItemRawValue.amount, {
        validators: [Validators.required],
      }),
      month: new FormControl(budgetItemRawValue.month, {
        validators: [Validators.required],
      }),
      dayInMonth: new FormControl(budgetItemRawValue.dayInMonth, {
        validators: [Validators.required, Validators.min(1), Validators.max(31)],
      }),
      isSmoothed: new FormControl(budgetItemRawValue.isSmoothed, {
        validators: [Validators.required],
      }),
    });
  }

  getBudgetItem(form: MyaBudgetItemCreateFormGroup): IMyaNewBudgetItem {
    return form.getRawValue() as IMyaNewBudgetItem;
  }

  resetForm(form: MyaBudgetItemCreateFormGroup, budgetItem: BudgetItemFormGroupInput): void {
    const budgetItemRawValue = { ...this.getFormDefaults(), ...budgetItem };
    form.reset(
      {
        ...budgetItemRawValue,
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): IMyaNewBudgetItem {
    return {
      isSmoothed: false,
    };
  }
}
