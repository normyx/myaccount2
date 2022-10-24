import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBudgetItemPeriod, NewBudgetItemPeriod } from '../budget-item-period.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBudgetItemPeriod for edit and NewBudgetItemPeriodFormGroupInput for create.
 */
type BudgetItemPeriodFormGroupInput = IBudgetItemPeriod | PartialWithRequiredKeyOf<NewBudgetItemPeriod>;

type BudgetItemPeriodFormDefaults = Pick<NewBudgetItemPeriod, 'id' | 'isSmoothed' | 'isRecurrent'>;

type BudgetItemPeriodFormGroupContent = {
  id: FormControl<IBudgetItemPeriod['id'] | NewBudgetItemPeriod['id']>;
  date: FormControl<IBudgetItemPeriod['date']>;
  month: FormControl<IBudgetItemPeriod['month']>;
  amount: FormControl<IBudgetItemPeriod['amount']>;
  isSmoothed: FormControl<IBudgetItemPeriod['isSmoothed']>;
  isRecurrent: FormControl<IBudgetItemPeriod['isRecurrent']>;
  operation: FormControl<IBudgetItemPeriod['operation']>;
  budgetItem: FormControl<IBudgetItemPeriod['budgetItem']>;
};

export type BudgetItemPeriodFormGroup = FormGroup<BudgetItemPeriodFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BudgetItemPeriodFormService {
  createBudgetItemPeriodFormGroup(budgetItemPeriod: BudgetItemPeriodFormGroupInput = { id: null }): BudgetItemPeriodFormGroup {
    const budgetItemPeriodRawValue = {
      ...this.getFormDefaults(),
      ...budgetItemPeriod,
    };
    return new FormGroup<BudgetItemPeriodFormGroupContent>({
      id: new FormControl(
        { value: budgetItemPeriodRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(budgetItemPeriodRawValue.date),
      month: new FormControl(budgetItemPeriodRawValue.month, {
        validators: [Validators.required],
      }),
      amount: new FormControl(budgetItemPeriodRawValue.amount, {
        validators: [Validators.required],
      }),
      isSmoothed: new FormControl(budgetItemPeriodRawValue.isSmoothed),
      isRecurrent: new FormControl(budgetItemPeriodRawValue.isRecurrent),
      operation: new FormControl(budgetItemPeriodRawValue.operation),
      budgetItem: new FormControl(budgetItemPeriodRawValue.budgetItem),
    });
  }

  getBudgetItemPeriod(form: BudgetItemPeriodFormGroup): IBudgetItemPeriod | NewBudgetItemPeriod {
    return form.getRawValue() as IBudgetItemPeriod | NewBudgetItemPeriod;
  }

  resetForm(form: BudgetItemPeriodFormGroup, budgetItemPeriod: BudgetItemPeriodFormGroupInput): void {
    const budgetItemPeriodRawValue = { ...this.getFormDefaults(), ...budgetItemPeriod };
    form.reset(
      {
        ...budgetItemPeriodRawValue,
        id: { value: budgetItemPeriodRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BudgetItemPeriodFormDefaults {
    return {
      id: null,
      isSmoothed: false,
      isRecurrent: false,
    };
  }
}
