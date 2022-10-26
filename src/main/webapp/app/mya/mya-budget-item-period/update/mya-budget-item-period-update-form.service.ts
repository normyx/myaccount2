import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IBudgetItemPeriod, NewBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { IMyaBudgetItemPeriodUpdate, NewMyaBudgetItemPeriodUpdate } from './mya-budget-item-period-update.model';

/**
 * A partial Type with required key is used as form input.
 */
type MyaPartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBudgetItemPeriod for edit and NewBudgetItemPeriodFormGroupInput for create.
 */
type MyaBudgetItemPeriodFormGroupInput = IMyaBudgetItemPeriodUpdate | MyaPartialWithRequiredKeyOf<NewMyaBudgetItemPeriodUpdate>;

type MyaBudgetItemPeriodFormDefaults = Pick<NewMyaBudgetItemPeriodUpdate, 'id' | 'isSmoothed' | 'modifyNexts'>;

type MyaBudgetItemPeriodFormGroupContent = {
  id: FormControl<IMyaBudgetItemPeriodUpdate['id'] | NewMyaBudgetItemPeriodUpdate['id']>;
  dayInMonth: FormControl<IMyaBudgetItemPeriodUpdate['dayInMonth']>;
  month: FormControl<IMyaBudgetItemPeriodUpdate['month']>;
  amount: FormControl<IMyaBudgetItemPeriodUpdate['amount']>;
  isSmoothed: FormControl<IMyaBudgetItemPeriodUpdate['isSmoothed']>;
  modifyNexts: FormControl<IMyaBudgetItemPeriodUpdate['modifyNexts']>;
  operationId: FormControl<IMyaBudgetItemPeriodUpdate['operationId']>;
  budgetItem: FormControl<IMyaBudgetItemPeriodUpdate['budgetItem']>;
};

export type MyaBudgetItemPeriodFormGroup = FormGroup<MyaBudgetItemPeriodFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemPeriodFormService {
  createBudgetItemPeriodFormGroup(budgetItemPeriod: MyaBudgetItemPeriodFormGroupInput = { id: null }): MyaBudgetItemPeriodFormGroup {
    const budgetItemPeriodRawValue = {
      ...this.getFormDefaults(),
      ...budgetItemPeriod,
    };
    const form = new FormGroup<MyaBudgetItemPeriodFormGroupContent>({
      id: new FormControl(
        { value: budgetItemPeriodRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      dayInMonth: new FormControl(budgetItemPeriodRawValue.dayInMonth, {
        validators: [Validators.required, Validators.min(1), Validators.max(31)],
      }),
      month: new FormControl(budgetItemPeriodRawValue.month, {
        validators: [Validators.required],
      }),
      amount: new FormControl(budgetItemPeriodRawValue.amount, {
        validators: [Validators.required],
      }),
      isSmoothed: new FormControl(budgetItemPeriodRawValue.isSmoothed),
      modifyNexts: new FormControl(budgetItemPeriodRawValue.modifyNexts),
      operationId: new FormControl(budgetItemPeriodRawValue.operationId),
      budgetItem: new FormControl(budgetItemPeriodRawValue.budgetItem),
    });
    form.get('isSmoothed')?.valueChanges.subscribe(value => {
      if (value) {
        form.get('dayInMonth')?.setValidators([Validators.min(1), Validators.max(31)]);
      } else {
        form.get('dayInMonth')?.setValidators([Validators.required, Validators.min(1), Validators.max(31)]);
      }
    });
    return form;
  }

  getBudgetItemPeriod(form: MyaBudgetItemPeriodFormGroup): IMyaBudgetItemPeriodUpdate | NewMyaBudgetItemPeriodUpdate {
    return form.getRawValue() as IMyaBudgetItemPeriodUpdate | NewMyaBudgetItemPeriodUpdate;
  }

  convertBudgetItemPeriodUpdateToBudgetItemPeriod(
    bipUpdate: IMyaBudgetItemPeriodUpdate | NewMyaBudgetItemPeriodUpdate
  ): IBudgetItemPeriod | NewBudgetItemPeriod {
    return {
      id: bipUpdate.id,
      date: bipUpdate.month?.clone().date(bipUpdate.dayInMonth ? bipUpdate.dayInMonth : 1),
      month: bipUpdate.month,
      amount: bipUpdate.amount,
      isSmoothed: bipUpdate.isSmoothed,
      isRecurrent: bipUpdate.modifyNexts,
      operation: bipUpdate.operationId ? { id: bipUpdate.operationId } : null,
      budgetItem: bipUpdate.budgetItem,
    } as IBudgetItemPeriod | NewBudgetItemPeriod;
  }

  convertBudgetItemPeriodToBudgetItemPeriodUpdate(bip: IBudgetItemPeriod): IMyaBudgetItemPeriodUpdate {
    return {
      id: bip.id,
      month: bip.month,
      dayInMonth: bip.date ? bip.date.date() : null,
      amount: bip.amount,
      isSmoothed: bip.isSmoothed,
      modifyNexts: bip.isRecurrent,
      operationId: bip.operation ? bip.operation.id : null,
      budgetItem: bip.budgetItem,
    };
  }

  resetForm(form: MyaBudgetItemPeriodFormGroup, budgetItemPeriod: MyaBudgetItemPeriodFormGroupInput): void {
    const budgetItemPeriodRawValue = { ...this.getFormDefaults(), ...budgetItemPeriod };
    form.reset(
      {
        ...budgetItemPeriodRawValue,
        id: { value: budgetItemPeriodRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MyaBudgetItemPeriodFormDefaults {
    return {
      id: null,
      isSmoothed: false,
      modifyNexts: false,
    };
  }
}
