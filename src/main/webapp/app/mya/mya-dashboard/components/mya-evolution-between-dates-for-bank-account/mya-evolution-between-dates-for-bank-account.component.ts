import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import 'chartjs-adapter-moment';
import { IBankAccount } from '../../../../entities/bank-account/bank-account.model';
import { MyaBankAccountService } from '../../../mya-bank-account/service/mya-bank-account.service';

@Component({
  selector: 'jhi-mya-evolution-between-dates-for-bank-account',
  templateUrl: './mya-evolution-between-dates-for-bank-account.component.html',
})
export class MyaEvolutionBetweenDatesForBankAccountComponent implements OnInit {
  @Input() dateFrom: Date | null = null;
  @Input() dateTo: Date | null = null;
  @Input() height = '30vh';
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
    });
  }
}
