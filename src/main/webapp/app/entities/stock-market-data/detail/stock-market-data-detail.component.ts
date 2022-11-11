import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStockMarketData } from '../stock-market-data.model';

@Component({
  selector: 'jhi-stock-market-data-detail',
  templateUrl: './stock-market-data-detail.component.html',
})
export class StockMarketDataDetailComponent implements OnInit {
  stockMarketData: IStockMarketData | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockMarketData }) => {
      this.stockMarketData = stockMarketData;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
