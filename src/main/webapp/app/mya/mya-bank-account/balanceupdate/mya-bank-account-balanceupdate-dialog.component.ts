import { HttpResponse } from '@angular/common/http';
import { Component, Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';
import { EVENT_LOAD_BANK_ACCOUNTS } from 'app/mya/config/mya.event.constants';
import { MyaOperationService } from 'app/mya/mya-operation/service/mya-operation.service';
import { Dayjs } from 'dayjs';
import { EventManager } from '../../../core/util/event-manager.service';

interface IBankAccountBalanceUpdate {
  initialAmount?: number | null;
  atDateAmount?: number | null;
}

type MyaBankAccountBalanceUpdateFormGroupContent = {
  initialAmount: FormControl<IBankAccountBalanceUpdate['initialAmount']>;
  atDateAmount: FormControl<IBankAccountBalanceUpdate['atDateAmount']>;
};
type MyaBankAccountBalanceUpdateFormGroup = FormGroup<MyaBankAccountBalanceUpdateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MyaBankAccountBalanceUpdateFormService {
  createBankAccountBalanceUpdateFormGroup(bankAccountBalanceUpdate: IBankAccountBalanceUpdate = {}): MyaBankAccountBalanceUpdateFormGroup {
    const bankAccountBalanceUpdateRawValue = {
      ...bankAccountBalanceUpdate,
    };
    return new FormGroup<MyaBankAccountBalanceUpdateFormGroupContent>({
      initialAmount: new FormControl(bankAccountBalanceUpdateRawValue.initialAmount, {
        validators: [Validators.required],
      }),
      atDateAmount: new FormControl(bankAccountBalanceUpdateRawValue.initialAmount, {
        validators: [Validators.required],
      }),
    });
  }

  getBankAccountBalanceUpdate(form: MyaBankAccountBalanceUpdateFormGroup): IBankAccountBalanceUpdate {
    return form.getRawValue();
  }

  resetFormBalanceUpdate(form: MyaBankAccountBalanceUpdateFormGroup, bankAccount: IBankAccountBalanceUpdate): void {
    const bankAccountRawValue = { ...bankAccount };
    form.reset(
      {
        ...bankAccountRawValue,
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }
}

@Component({
  templateUrl: './mya-bank-account-balanceupdate-dialog.component.html',
})
export class MyaBankAccountBalanceUpdateDialogComponent {
  bankAccount: IBankAccount | null = null;
  bankAccountBalanceUpdate: IBankAccountBalanceUpdate | null = null;
  sumOfOperationAmount: number | null = null;
  lastOperationDate: Dayjs | null = null;

  editForm: MyaBankAccountBalanceUpdateFormGroup = this.formService.createBankAccountBalanceUpdateFormGroup();

  constructor(
    protected bankAccountService: BankAccountService,
    protected activeModal: NgbActiveModal,
    protected formService: MyaBankAccountBalanceUpdateFormService,
    protected operationService: MyaOperationService,
    private eventManager: EventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  setBankAccount(bankAccount: IBankAccount): void {
    this.bankAccount = bankAccount;
    this.operationService.sumOfAmountForBankAccount(this.bankAccount.id).subscribe((res: HttpResponse<number>) => {
      this.sumOfOperationAmount = res.body;
      this.bankAccountBalanceUpdate = {
        initialAmount: this.bankAccount!.initialAmount,
        atDateAmount: this.sumOfOperationAmount! + this.bankAccount!.initialAmount! + this.bankAccount!.adjustmentAmount!,
      };
      this.formService.resetFormBalanceUpdate(this.editForm, this.bankAccountBalanceUpdate);
    });
    this.operationService.lastOperationDateForBankAccount(this.bankAccount.id).subscribe((res: HttpResponse<Dayjs>) => {
      this.lastOperationDate = res.body;
    });
  }

  confirm(): void {
    const bankAccountBalanceUpdate = this.formService.getBankAccountBalanceUpdate(this.editForm);
    this.bankAccount!.initialAmount = bankAccountBalanceUpdate.initialAmount;
    this.bankAccount!.adjustmentAmount =
      bankAccountBalanceUpdate.atDateAmount! - this.sumOfOperationAmount! - this.bankAccount!.initialAmount!;
    this.bankAccountService.update(this.bankAccount!).subscribe(() => {
      this.eventManager.broadcast({ name: EVENT_LOAD_BANK_ACCOUNTS, content: 'OK' });
      this.activeModal.close();
    });
  }
}
