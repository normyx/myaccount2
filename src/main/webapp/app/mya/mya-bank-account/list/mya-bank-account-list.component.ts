import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';

import { ASC, DEFAULT_SORT_DATA, DESC, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { SortService } from 'app/shared/sort/sort.service';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { BankAccountDeleteDialogComponent } from '../../../entities/bank-account/delete/bank-account-delete-dialog.component';
import { EntityArrayResponseType } from '../../../entities/bank-account/service/bank-account.service';
import { MyaBankAccountService } from '../service/mya-bank-account.service';
import { IBankAccountTotal } from '../row/mya-bank-account-total.model';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';

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

  bankAccounts: IBankAccount[] | null = null;
  currentBankAccounts: IBankAccount[] | null = null;
  savingsBankAccounts: IBankAccount[] | null = null;
  portfolioBankAccounts: IBankAccount[] | null = null;

  constructor(protected bankAccountService: MyaBankAccountService) {}

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

  ngOnInit(): void {
    this.load();
  }

  load(): void {
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
