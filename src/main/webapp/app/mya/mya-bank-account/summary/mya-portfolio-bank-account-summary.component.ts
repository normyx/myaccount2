import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';
import { StockPortfolioItemService } from 'app/entities/stock-portfolio-item/service/stock-portfolio-item.service';
import { IStockPortfolioItem } from 'app/entities/stock-portfolio-item/stock-portfolio-item.model';
import { MyaOperationService } from 'app/mya/mya-operation/service/mya-operation.service';
import 'chartjs-adapter-moment';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { MyaBankAccountService } from '../service/mya-bank-account.service';

class MyaPortfolioSymbol {
  symbol: string;
  stockPortfolioItems: IStockPortfolioItem[];
  constructor(symbol: string) {
    this.stockPortfolioItems = new Array<IStockPortfolioItem>();
    this.symbol = symbol;
  }
}

@Component({
  selector: 'jhi-mya-portfolio-bank-account-summary',
  templateUrl: './mya-portfolio-bank-account-summary.component.html',
})
export class MyaPortfolioBankAccountSummaryComponent implements OnInit {
  dateFrom = new Date();
  dateTo = new Date();
  height = '10vh';
  bankAccounts: IBankAccount[] | null = null;
  selectedBankAccount: IBankAccount | null = null;
  sumOfOperationAmount: number | null = null;
  stockPortfolioItems: IStockPortfolioItem[] | null = null;
  totalAmount = 0;
  portfolioSymbols: MyaPortfolioSymbol[] = new Array<MyaPortfolioSymbol>();
  chartType = 'portfolio';
  chartSymbol = '';

  constructor(
    protected bankAccountService: MyaBankAccountService,
    protected stockPortfolioItemService: StockPortfolioItemService,
    protected operationService: MyaOperationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.load();
  }

  changeChartSymbol(symbol: string): void {
    if (symbol === 'all') {
      this.chartType = 'portfolio';
    } else {
      this.chartType = 'stock';
      this.chartSymbol = symbol;
    }
  }

  onChange(newValue: IBankAccount): void {
    this.selectedBankAccount = newValue;
  }

  load(): void {
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      this.selectedBankAccount = bankAccount;
    });
    this.stockPortfolioItemService
      .query(`{bankAccountId.equals: ${this.selectedBankAccount!.id.toString()}}`)
      .subscribe((response: HttpResponse<IStockPortfolioItem[]>) => {
        this.stockPortfolioItems = response.body;
        this.stockPortfolioItems?.forEach(stockPortfolioItem => {
          let portfolioSymbol = this.portfolioSymbols.find(e => e.symbol === stockPortfolioItem.stockSymbol);
          if (!portfolioSymbol) {
            portfolioSymbol = new MyaPortfolioSymbol(stockPortfolioItem.stockSymbol!);
            this.portfolioSymbols.push(portfolioSymbol);
          }
          portfolioSymbol.stockPortfolioItems.push(stockPortfolioItem);
        });
      });

    this.bankAccountService.queryWithSignedInUser().subscribe((bankAccounts: HttpResponse<IBankAccount[]>) => {
      this.bankAccounts = bankAccounts.body!.filter(
        ba => ba.accountType === BankAccountType.STOCKPORTFOLIO && ba.id === this.selectedBankAccount!.id
      );

      this.selectedBankAccount = this.bankAccounts[0];
      this.operationService.sumOfAmountForBankAccount(this.selectedBankAccount.id).subscribe((res: HttpResponse<number>) => {
        this.sumOfOperationAmount = res.body;
        if (this.selectedBankAccount && this.sumOfOperationAmount) {
          this.totalAmount =
            this.selectedBankAccount.initialAmount! + this.selectedBankAccount.adjustmentAmount! + this.sumOfOperationAmount;
          if (this.stockPortfolioItems) {
            this.stockPortfolioItems.forEach(spi => {
              this.totalAmount += spi.stockCurrentPrice! * spi.stockCurrentCurrencyFactor! * spi.stockSharesNumber!;
            });
          }
        }
      });
    });
  }
}
