import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOperation, NewOperation } from '../operation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOperation for edit and NewOperationFormGroupInput for create.
 */
type OperationFormGroupInput = IOperation | PartialWithRequiredKeyOf<NewOperation>;

type OperationFormDefaults = Pick<NewOperation, 'id' | 'isUpToDate' | 'deletingHardLock'>;

type OperationFormGroupContent = {
  id: FormControl<IOperation['id'] | NewOperation['id']>;
  label: FormControl<IOperation['label']>;
  date: FormControl<IOperation['date']>;
  amount: FormControl<IOperation['amount']>;
  note: FormControl<IOperation['note']>;
  checkNumber: FormControl<IOperation['checkNumber']>;
  isUpToDate: FormControl<IOperation['isUpToDate']>;
  deletingHardLock: FormControl<IOperation['deletingHardLock']>;
  subCategory: FormControl<IOperation['subCategory']>;
  account: FormControl<IOperation['account']>;
  bankAccount: FormControl<IOperation['bankAccount']>;
};

export type OperationFormGroup = FormGroup<OperationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OperationFormService {
  createOperationFormGroup(operation: OperationFormGroupInput = { id: null }): OperationFormGroup {
    const operationRawValue = {
      ...this.getFormDefaults(),
      ...operation,
    };
    return new FormGroup<OperationFormGroupContent>({
      id: new FormControl(
        { value: operationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      label: new FormControl(operationRawValue.label, {
        validators: [Validators.required, Validators.maxLength(400)],
      }),
      date: new FormControl(operationRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(operationRawValue.amount, {
        validators: [Validators.required],
      }),
      note: new FormControl(operationRawValue.note, {
        validators: [Validators.maxLength(400)],
      }),
      checkNumber: new FormControl(operationRawValue.checkNumber, {
        validators: [Validators.maxLength(20)],
      }),
      isUpToDate: new FormControl(operationRawValue.isUpToDate, {
        validators: [Validators.required],
      }),
      deletingHardLock: new FormControl(operationRawValue.deletingHardLock),
      subCategory: new FormControl(operationRawValue.subCategory),
      account: new FormControl(operationRawValue.account),
      bankAccount: new FormControl(operationRawValue.bankAccount, {
        validators: [Validators.required],
      }),
    });
  }

  getOperation(form: OperationFormGroup): IOperation | NewOperation {
    return form.getRawValue() as IOperation | NewOperation;
  }

  resetForm(form: OperationFormGroup, operation: OperationFormGroupInput): void {
    const operationRawValue = { ...this.getFormDefaults(), ...operation };
    form.reset(
      {
        ...operationRawValue,
        id: { value: operationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OperationFormDefaults {
    return {
      id: null,
      isUpToDate: false,
      deletingHardLock: false,
    };
  }
}
