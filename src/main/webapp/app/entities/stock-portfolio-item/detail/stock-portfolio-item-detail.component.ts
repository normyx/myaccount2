import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStockPortfolioItem } from '../stock-portfolio-item.model';

@Component({
  selector: 'jhi-stock-portfolio-item-detail',
  templateUrl: './stock-portfolio-item-detail.component.html',
})
export class StockPortfolioItemDetailComponent implements OnInit {
  stockPortfolioItem: IStockPortfolioItem | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockPortfolioItem }) => {
      this.stockPortfolioItem = stockPortfolioItem;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
