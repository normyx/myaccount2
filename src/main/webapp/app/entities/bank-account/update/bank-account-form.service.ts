import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBankAccount, NewBankAccount } from '../bank-account.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBankAccount for edit and NewBankAccountFormGroupInput for create.
 */
type BankAccountFormGroupInput = IBankAccount | PartialWithRequiredKeyOf<NewBankAccount>;

type BankAccountFormDefaults = Pick<NewBankAccount, 'id' | 'archived'>;

type BankAccountFormGroupContent = {
  id: FormControl<IBankAccount['id'] | NewBankAccount['id']>;
  accountName: FormControl<IBankAccount['accountName']>;
  accountBank: FormControl<IBankAccount['accountBank']>;
  initialAmount: FormControl<IBankAccount['initialAmount']>;
  archived: FormControl<IBankAccount['archived']>;
  shortName: FormControl<IBankAccount['shortName']>;
  accountType: FormControl<IBankAccount['accountType']>;
  adjustmentAmount: FormControl<IBankAccount['adjustmentAmount']>;
  account: FormControl<IBankAccount['account']>;
};

export type BankAccountFormGroup = FormGroup<BankAccountFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BankAccountFormService {
  createBankAccountFormGroup(bankAccount: BankAccountFormGroupInput = { id: null }): BankAccountFormGroup {
    const bankAccountRawValue = {
      ...this.getFormDefaults(),
      ...bankAccount,
    };
    return new FormGroup<BankAccountFormGroupContent>({
      id: new FormControl(
        { value: bankAccountRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      accountName: new FormControl(bankAccountRawValue.accountName, {
        validators: [Validators.required],
      }),
      accountBank: new FormControl(bankAccountRawValue.accountBank, {
        validators: [Validators.required],
      }),
      initialAmount: new FormControl(bankAccountRawValue.initialAmount, {
        validators: [Validators.required],
      }),
      archived: new FormControl(bankAccountRawValue.archived, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(bankAccountRawValue.shortName, {
        validators: [Validators.maxLength(40)],
      }),
      accountType: new FormControl(bankAccountRawValue.accountType, {
        validators: [Validators.required],
      }),
      adjustmentAmount: new FormControl(bankAccountRawValue.adjustmentAmount, {
        validators: [Validators.required],
      }),
      account: new FormControl(bankAccountRawValue.account, {
        validators: [Validators.required],
      }),
    });
  }

  getBankAccount(form: BankAccountFormGroup): IBankAccount | NewBankAccount {
    return form.getRawValue() as IBankAccount | NewBankAccount;
  }

  resetForm(form: BankAccountFormGroup, bankAccount: BankAccountFormGroupInput): void {
    const bankAccountRawValue = { ...this.getFormDefaults(), ...bankAccount };
    form.reset(
      {
        ...bankAccountRawValue,
        id: { value: bankAccountRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BankAccountFormDefaults {
    return {
      id: null,
      archived: false,
    };
  }
}
