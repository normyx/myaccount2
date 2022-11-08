import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStockPortfolioItem, NewStockPortfolioItem } from '../stock-portfolio-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockPortfolioItem for edit and NewStockPortfolioItemFormGroupInput for create.
 */
type StockPortfolioItemFormGroupInput = IStockPortfolioItem | PartialWithRequiredKeyOf<NewStockPortfolioItem>;

type StockPortfolioItemFormDefaults = Pick<NewStockPortfolioItem, 'id'>;

type StockPortfolioItemFormGroupContent = {
  id: FormControl<IStockPortfolioItem['id'] | NewStockPortfolioItem['id']>;
  stockSymbol: FormControl<IStockPortfolioItem['stockSymbol']>;
  stockCurrency: FormControl<IStockPortfolioItem['stockCurrency']>;
  stockAcquisitionDate: FormControl<IStockPortfolioItem['stockAcquisitionDate']>;
  stockSharesNumber: FormControl<IStockPortfolioItem['stockSharesNumber']>;
  stockAcquisitionPrice: FormControl<IStockPortfolioItem['stockAcquisitionPrice']>;
  stockCurrentPrice: FormControl<IStockPortfolioItem['stockCurrentPrice']>;
  stockCurrentDate: FormControl<IStockPortfolioItem['stockCurrentDate']>;
  stockAcquisitionCurrencyFactor: FormControl<IStockPortfolioItem['stockAcquisitionCurrencyFactor']>;
  stockCurrentCurrencyFactor: FormControl<IStockPortfolioItem['stockCurrentCurrencyFactor']>;
  bankAccount: FormControl<IStockPortfolioItem['bankAccount']>;
};

export type StockPortfolioItemFormGroup = FormGroup<StockPortfolioItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockPortfolioItemFormService {
  createStockPortfolioItemFormGroup(stockPortfolioItem: StockPortfolioItemFormGroupInput = { id: null }): StockPortfolioItemFormGroup {
    const stockPortfolioItemRawValue = {
      ...this.getFormDefaults(),
      ...stockPortfolioItem,
    };
    return new FormGroup<StockPortfolioItemFormGroupContent>({
      id: new FormControl(
        { value: stockPortfolioItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      stockSymbol: new FormControl(stockPortfolioItemRawValue.stockSymbol, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(10)],
      }),
      stockCurrency: new FormControl(stockPortfolioItemRawValue.stockCurrency, {
        validators: [Validators.required],
      }),
      stockAcquisitionDate: new FormControl(stockPortfolioItemRawValue.stockAcquisitionDate, {
        validators: [Validators.required],
      }),
      stockSharesNumber: new FormControl(stockPortfolioItemRawValue.stockSharesNumber, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockAcquisitionPrice: new FormControl(stockPortfolioItemRawValue.stockAcquisitionPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockCurrentPrice: new FormControl(stockPortfolioItemRawValue.stockCurrentPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockCurrentDate: new FormControl(stockPortfolioItemRawValue.stockCurrentDate, {
        validators: [Validators.required],
      }),
      stockAcquisitionCurrencyFactor: new FormControl(stockPortfolioItemRawValue.stockAcquisitionCurrencyFactor, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockCurrentCurrencyFactor: new FormControl(stockPortfolioItemRawValue.stockCurrentCurrencyFactor, {
        validators: [Validators.required, Validators.min(0)],
      }),
      bankAccount: new FormControl(stockPortfolioItemRawValue.bankAccount),
    });
  }

  getStockPortfolioItem(form: StockPortfolioItemFormGroup): IStockPortfolioItem | NewStockPortfolioItem {
    return form.getRawValue() as IStockPortfolioItem | NewStockPortfolioItem;
  }

  resetForm(form: StockPortfolioItemFormGroup, stockPortfolioItem: StockPortfolioItemFormGroupInput): void {
    const stockPortfolioItemRawValue = { ...this.getFormDefaults(), ...stockPortfolioItem };
    form.reset(
      {
        ...stockPortfolioItemRawValue,
        id: { value: stockPortfolioItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StockPortfolioItemFormDefaults {
    return {
      id: null,
    };
  }
}
