import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';
import { StockPortfolioItemService } from 'app/entities/stock-portfolio-item/service/stock-portfolio-item.service';
import { IStockPortfolioItem } from 'app/entities/stock-portfolio-item/stock-portfolio-item.model';
import { MyaOperationService } from 'app/mya/mya-operation/service/mya-operation.service';
import 'chartjs-adapter-moment';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { MyaBankAccountService } from '../service/mya-bank-account.service';

@Component({
  selector: 'jhi-mya-portfolio-bank-account-symbol-summary',
  templateUrl: './mya-portfolio-bank-account-symbol-summary.component.html',
})
export class MyaPortfolioBankAccountSymbolSummaryComponent {
  @Input() symbol: string | null = null;
  @Input() stockPortfolioItems: IStockPortfolioItem[] | null = null;

  constructor(
    protected bankAccountService: MyaBankAccountService,
    protected stockPortfolioItemService: StockPortfolioItemService,
    protected operationService: MyaOperationService,
    protected activatedRoute: ActivatedRoute
  ) {}
}
