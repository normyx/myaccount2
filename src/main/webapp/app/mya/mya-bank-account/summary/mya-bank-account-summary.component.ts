import { HttpResponse } from '@angular/common/http';
import { Component, OnChanges, OnInit } from '@angular/core';
import 'chartjs-adapter-moment';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { MyaBankAccountService } from '../../mya-bank-account/service/mya-bank-account.service';

@Component({
  selector: 'jhi-mya-bank-account-summary',
  templateUrl: './mya-bank-account-summary.component.html',
})
export class MyaBankAccountSummaryComponent implements OnInit {
  dateFrom = new Date();
  dateTo = new Date();
  height = '10vh';
  bankAccounts: IBankAccount[] | null = null;
  selectedBankAccount: IBankAccount | null = null;

  constructor(protected bankAccountService: MyaBankAccountService) {}

  ngOnInit(): void {
    this.loadDependencies();
  }

  onChange(newValue: IBankAccount): void {
    this.selectedBankAccount = newValue;
  }

  loadDependencies(): void {
    this.bankAccountService.queryWithSignedInUser().subscribe((bankAccounts: HttpResponse<IBankAccount[]>) => {
      this.bankAccounts = bankAccounts.body;
      if (this.bankAccounts && this.bankAccounts.length !== 0) {
        this.selectedBankAccount = this.bankAccounts[0];
      }
    });
  }
}
