import { Component, OnInit } from '@angular/core';
import { EventManager } from 'app/core/util/event-manager.service';

import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';
import { EVENT_LOAD_BANK_ACCOUNTS } from 'app/mya/config/mya.event.constants';
import { Subscription } from 'rxjs';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { EntityArrayResponseType } from '../../../entities/bank-account/service/bank-account.service';
import { IBankAccountTotal } from '../row/mya-bank-account-total.model';
import { MyaBankAccountService } from '../service/mya-bank-account.service';

@Component({
  selector: 'jhi-mya-bank-account-list',
  templateUrl: './mya-bank-account-list.component.html',
})
export class MyaBankAccountListComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['accountName', 'accountBank', 'shortName', 'accountType'];

  currentAccountTotal = 0;
  savingsAccountTotal = 0;
  portfolioTotal = 0;
  total = 0;
  withArchived = false;
  dashboardType = 'all';

  bankAccounts: IBankAccount[] | null = null;
  currentBankAccounts: IBankAccount[] | null = null;
  savingsBankAccounts: IBankAccount[] | null = null;
  portfolioBankAccounts: IBankAccount[] | null = null;

  eventSubscriber: Subscription | null = null;

  constructor(protected bankAccountService: MyaBankAccountService, private eventManager: EventManager) {}

  addToTotalEvent(total: IBankAccountTotal): void {
    switch (total.bankAccount.accountType) {
      case BankAccountType.CURRENTACCOUNT:
        this.currentAccountTotal += total.total;
        break;
      case BankAccountType.SAVINGSACCOUNT:
        this.savingsAccountTotal += total.total;
        break;
      case BankAccountType.STOCKPORTFOLIO:
        this.portfolioTotal += total.total;
        break;
    }
    this.total += total.total;
  }

  changeDashboardType(type: string): void {
    this.dashboardType = type;
  }

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe(EVENT_LOAD_BANK_ACCOUNTS, () => this.load());
    this.load();
  }

  load(): void {
    this.currentAccountTotal = 0;
    this.savingsAccountTotal = 0;
    this.portfolioTotal = 0;
    this.total = 0;
    this.bankAccountService.queryWithSignedInUser().subscribe((res: EntityArrayResponseType) => {
      this.bankAccounts = res.body;
      if (this.bankAccounts) {
        this.currentBankAccounts = this.sortBankAccounts(this.bankAccounts.filter(ba => ba.accountType === BankAccountType.CURRENTACCOUNT));

        this.savingsBankAccounts = this.sortBankAccounts(this.bankAccounts.filter(ba => ba.accountType === BankAccountType.SAVINGSACCOUNT));
        this.portfolioBankAccounts = this.sortBankAccounts(
          this.bankAccounts.filter(ba => ba.accountType === BankAccountType.STOCKPORTFOLIO)
        );
      }
    });
  }

  private sortBankAccounts(bankAccount: IBankAccount[]): IBankAccount[] {
    return bankAccount.sort((a: IBankAccount, b: IBankAccount) => {
      if (a.archived === b.archived) {
        if (a.accountBank === b.accountBank) {
          return a.accountName! < b.accountName! ? 1 : -1;
        } else {
          return a.accountBank! < b.accountBank! ? 1 : -1;
        }
      } else {
        return a.archived ? 1 : -1;
      }
    });
  }
}
