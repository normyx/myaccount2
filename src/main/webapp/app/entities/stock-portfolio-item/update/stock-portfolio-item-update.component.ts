import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StockPortfolioItemFormService, StockPortfolioItemFormGroup } from './stock-portfolio-item-form.service';
import { IStockPortfolioItem } from '../stock-portfolio-item.model';
import { StockPortfolioItemService } from '../service/stock-portfolio-item.service';
import { Currency } from 'app/entities/enumerations/currency.model';

@Component({
  selector: 'jhi-stock-portfolio-item-update',
  templateUrl: './stock-portfolio-item-update.component.html',
})
export class StockPortfolioItemUpdateComponent implements OnInit {
  isSaving = false;
  stockPortfolioItem: IStockPortfolioItem | null = null;
  currencyValues = Object.keys(Currency);

  editForm: StockPortfolioItemFormGroup = this.stockPortfolioItemFormService.createStockPortfolioItemFormGroup();

  constructor(
    protected stockPortfolioItemService: StockPortfolioItemService,
    protected stockPortfolioItemFormService: StockPortfolioItemFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockPortfolioItem }) => {
      this.stockPortfolioItem = stockPortfolioItem;
      if (stockPortfolioItem) {
        this.updateForm(stockPortfolioItem);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stockPortfolioItem = this.stockPortfolioItemFormService.getStockPortfolioItem(this.editForm);
    if (stockPortfolioItem.id !== null) {
      this.subscribeToSaveResponse(this.stockPortfolioItemService.update(stockPortfolioItem));
    } else {
      this.subscribeToSaveResponse(this.stockPortfolioItemService.create(stockPortfolioItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStockPortfolioItem>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(stockPortfolioItem: IStockPortfolioItem): void {
    this.stockPortfolioItem = stockPortfolioItem;
    this.stockPortfolioItemFormService.resetForm(this.editForm, stockPortfolioItem);
  }
}
