import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBudgetItem, NewBudgetItem } from '../budget-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBudgetItem for edit and NewBudgetItemFormGroupInput for create.
 */
type BudgetItemFormGroupInput = IBudgetItem | PartialWithRequiredKeyOf<NewBudgetItem>;

type BudgetItemFormDefaults = Pick<NewBudgetItem, 'id'>;

type BudgetItemFormGroupContent = {
  id: FormControl<IBudgetItem['id'] | NewBudgetItem['id']>;
  name: FormControl<IBudgetItem['name']>;
  order: FormControl<IBudgetItem['order']>;
  category: FormControl<IBudgetItem['category']>;
  account: FormControl<IBudgetItem['account']>;
};

export type BudgetItemFormGroup = FormGroup<BudgetItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BudgetItemFormService {
  createBudgetItemFormGroup(budgetItem: BudgetItemFormGroupInput = { id: null }): BudgetItemFormGroup {
    const budgetItemRawValue = {
      ...this.getFormDefaults(),
      ...budgetItem,
    };
    return new FormGroup<BudgetItemFormGroupContent>({
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

  getBudgetItem(form: BudgetItemFormGroup): IBudgetItem | NewBudgetItem {
    return form.getRawValue() as IBudgetItem | NewBudgetItem;
  }

  resetForm(form: BudgetItemFormGroup, budgetItem: BudgetItemFormGroupInput): void {
    const budgetItemRawValue = { ...this.getFormDefaults(), ...budgetItem };
    form.reset(
      {
        ...budgetItemRawValue,
        id: { value: budgetItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BudgetItemFormDefaults {
    return {
      id: null,
    };
  }
}
