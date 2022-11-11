import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StockMarketDataFormService, StockMarketDataFormGroup } from './stock-market-data-form.service';
import { IStockMarketData } from '../stock-market-data.model';
import { StockMarketDataService } from '../service/stock-market-data.service';

@Component({
  selector: 'jhi-stock-market-data-update',
  templateUrl: './stock-market-data-update.component.html',
})
export class StockMarketDataUpdateComponent implements OnInit {
  isSaving = false;
  stockMarketData: IStockMarketData | null = null;

  editForm: StockMarketDataFormGroup = this.stockMarketDataFormService.createStockMarketDataFormGroup();

  constructor(
    protected stockMarketDataService: StockMarketDataService,
    protected stockMarketDataFormService: StockMarketDataFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockMarketData }) => {
      this.stockMarketData = stockMarketData;
      if (stockMarketData) {
        this.updateForm(stockMarketData);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stockMarketData = this.stockMarketDataFormService.getStockMarketData(this.editForm);
    if (stockMarketData.id !== null) {
      this.subscribeToSaveResponse(this.stockMarketDataService.update(stockMarketData));
    } else {
      this.subscribeToSaveResponse(this.stockMarketDataService.create(stockMarketData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStockMarketData>>): void {
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

  protected updateForm(stockMarketData: IStockMarketData): void {
    this.stockMarketData = stockMarketData;
    this.stockMarketDataFormService.resetForm(this.editForm, stockMarketData);
  }
}
