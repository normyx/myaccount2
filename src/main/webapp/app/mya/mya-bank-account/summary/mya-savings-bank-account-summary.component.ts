import { HttpResponse } from '@angular/common/http';
import { Component, OnChanges, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';
import { MyaOperationService } from 'app/mya/mya-operation/service/mya-operation.service';
import 'chartjs-adapter-moment';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { MyaBankAccountService } from '../service/mya-bank-account.service';

@Component({
  selector: 'jhi-mya-savings-bank-account-summary',
  templateUrl: './mya-savings-bank-account-summary.component.html',
})
export class MyaSavingsBankAccountSummaryComponent implements OnInit {
  dateFrom = new Date();
  dateTo = new Date();
  height = '10vh';
  bankAccounts: IBankAccount[] | null = null;
  selectedBankAccount: IBankAccount | null = null;
  sumOfOperationAmount: number | null = null;
  totalAmount = 0;

  constructor(
    protected bankAccountService: MyaBankAccountService,
    protected operationService: MyaOperationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      this.selectedBankAccount = bankAccount;
    });
    this.loadDependencies();
    this.load();
  }

  onChange(newValue: IBankAccount): void {
    this.selectedBankAccount = newValue;
    this.load();
  }

  loadDependencies(): void {
    this.bankAccountService.queryWithSignedInUser().subscribe((bankAccounts: HttpResponse<IBankAccount[]>) => {
      this.bankAccounts = bankAccounts.body!.filter(ba => ba.accountType === BankAccountType.SAVINGSACCOUNT);
    });
  }
  load(): void {
    this.sumOfOperationAmount = 0;
    this.totalAmount = 0;
    if (this.selectedBankAccount) {
      this.operationService.sumOfAmountForBankAccount(this.selectedBankAccount.id).subscribe((res: HttpResponse<number>) => {
        this.sumOfOperationAmount = res.body;
        if (this.selectedBankAccount && this.sumOfOperationAmount) {
          this.totalAmount +=
            this.selectedBankAccount.initialAmount! + this.selectedBankAccount.adjustmentAmount! + this.sumOfOperationAmount;
        }
      });
    }
  }
}
