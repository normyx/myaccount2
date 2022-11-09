import { HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';
import { StockPortfolioItemService } from 'app/entities/stock-portfolio-item/service/stock-portfolio-item.service';
import { IStockPortfolioItem } from 'app/entities/stock-portfolio-item/stock-portfolio-item.model';
import { MyaOperationService } from 'app/mya/mya-operation/service/mya-operation.service';
import { FilterOptions, IFilterOptions } from 'app/shared/filter/filter.model';

import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { MyaBankAccountService } from '../service/mya-bank-account.service';
import { IBankAccountTotal } from './mya-bank-account-total.model';

@Component({
  selector: '[jhi-mya-bank-account-row]',
  templateUrl: './mya-bank-account-row.component.html',
})
export class MyaBankAccountRowComponent implements OnInit {
  @Input() bankAccount: IBankAccount | null = null;

  sumOfOperationAmount: number | null = null;
  totalAmount = 0;

  @Output() totalEvent = new EventEmitter<IBankAccountTotal>();

  stockPortfolioItems: IStockPortfolioItem[] | null = null;

  constructor(
    protected bankAccountService: MyaBankAccountService,
    protected stockPortfolioItemService: StockPortfolioItemService,
    protected operationService: MyaOperationService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const filters: IFilterOptions = new FilterOptions();
    filters.addFilter('bankAccountId.equals', this.bankAccount!.id.toString());
    this.stockPortfolioItemService
      .query(`{bankAccountId.equals: ${this.bankAccount!.id.toString()}}`)
      .subscribe((response: HttpResponse<IStockPortfolioItem[]>) => {
        this.stockPortfolioItems = response.body;
        this.operationService.sumOfAmountForBankAccount(this.bankAccount!.id).subscribe((res: HttpResponse<number>) => {
          this.sumOfOperationAmount = res.body;
          if (this.bankAccount && this.sumOfOperationAmount) {
            this.totalAmount += this.bankAccount.initialAmount! + this.bankAccount.adjustmentAmount! + this.sumOfOperationAmount;
            if (this.bankAccount.accountType === BankAccountType.STOCKPORTFOLIO) {
              if (this.stockPortfolioItems) {
                this.stockPortfolioItems.forEach(spi => {
                  this.totalAmount += spi.stockCurrentPrice! * spi.stockCurrentCurrencyFactor! * spi.stockSharesNumber!;
                });
              }
            }
            this.totalEvent.emit({ bankAccount: this.bankAccount, total: this.totalAmount });
          }
        });
      });
  }
}
