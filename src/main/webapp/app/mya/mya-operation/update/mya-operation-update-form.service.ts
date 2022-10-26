import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { IOperation } from '../../../entities/operation/operation.model';

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBudgetItem for edit and NewBudgetItemFormGroupInput for create.
 */
type MyaOperationUpdateFormGroupInput = IOperation;

type MyaOperationFormGroupContent = {
  id: FormControl<IOperation['id']>;
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

export type MyaOperationUpdateFormGroup = FormGroup<MyaOperationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MyaOperationUpdateFormService {
  createOperationFormGroup(operation: MyaOperationUpdateFormGroupInput): MyaOperationUpdateFormGroup {
    const operationRawValue = {
      ...operation,
    };
    return new FormGroup<MyaOperationFormGroupContent>({
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

  getOperation(form: MyaOperationUpdateFormGroup): IOperation {
    return form.getRawValue() as IOperation;
  }

  resetForm(form: MyaOperationUpdateFormGroup, operation: MyaOperationUpdateFormGroupInput): void {
    const operationRawValue = { ...operation };
    form.reset(
      {
        ...operationRawValue,
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }
}
