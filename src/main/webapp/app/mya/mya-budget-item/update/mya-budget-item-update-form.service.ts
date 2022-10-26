import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBudgetItem for edit and NewBudgetItemFormGroupInput for create.
 */
type MyaBudgetItemUpdateFormGroupInput = IBudgetItem;

type MyaBudgetItemFormGroupContent = {
  id: FormControl<IBudgetItem['id']>;
  name: FormControl<IBudgetItem['name']>;
  order: FormControl<IBudgetItem['order']>;
  category: FormControl<IBudgetItem['category']>;
  account: FormControl<IBudgetItem['account']>;
};

export type MyaBudgetItemUpdateFormGroup = FormGroup<MyaBudgetItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemUpdateFormService {
  createBudgetItemFormGroup(budgetItem: MyaBudgetItemUpdateFormGroupInput): MyaBudgetItemUpdateFormGroup {
    const budgetItemRawValue = {
      ...budgetItem,
    };
    return new FormGroup<MyaBudgetItemFormGroupContent>({
      id: new FormControl(
        { value: budgetItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(budgetItemRawValue.name, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      order: new FormControl(budgetItemRawValue.order, {
        validators: [Validators.required],
      }),
      category: new FormControl(budgetItemRawValue.category),
      account: new FormControl(budgetItemRawValue.account),
    });
  }

  getBudgetItem(form: MyaBudgetItemUpdateFormGroup): IBudgetItem {
    return form.getRawValue() as IBudgetItem;
  }

  resetForm(form: MyaBudgetItemUpdateFormGroup, budgetItem: MyaBudgetItemUpdateFormGroupInput): void {
    const budgetItemRawValue = { ...budgetItem };
    form.reset(
      {
        ...budgetItemRawValue,
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }
}
