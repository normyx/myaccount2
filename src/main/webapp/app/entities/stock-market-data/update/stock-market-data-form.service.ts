import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStockMarketData, NewStockMarketData } from '../stock-market-data.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockMarketData for edit and NewStockMarketDataFormGroupInput for create.
 */
type StockMarketDataFormGroupInput = IStockMarketData | PartialWithRequiredKeyOf<NewStockMarketData>;

type StockMarketDataFormDefaults = Pick<NewStockMarketData, 'id'>;

type StockMarketDataFormGroupContent = {
  id: FormControl<IStockMarketData['id'] | NewStockMarketData['id']>;
  symbol: FormControl<IStockMarketData['symbol']>;
  dataDate: FormControl<IStockMarketData['dataDate']>;
  closeValue: FormControl<IStockMarketData['closeValue']>;
};

export type StockMarketDataFormGroup = FormGroup<StockMarketDataFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockMarketDataFormService {
  createStockMarketDataFormGroup(stockMarketData: StockMarketDataFormGroupInput = { id: null }): StockMarketDataFormGroup {
    const stockMarketDataRawValue = {
      ...this.getFormDefaults(),
      ...stockMarketData,
    };
    return new FormGroup<StockMarketDataFormGroupContent>({
      id: new FormControl(
        { value: stockMarketDataRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      symbol: new FormControl(stockMarketDataRawValue.symbol, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(10)],
      }),
      dataDate: new FormControl(stockMarketDataRawValue.dataDate, {
        validators: [Validators.required],
      }),
      closeValue: new FormControl(stockMarketDataRawValue.closeValue, {
        validators: [Validators.required, Validators.min(0)],
      }),
    });
  }

  getStockMarketData(form: StockMarketDataFormGroup): IStockMarketData | NewStockMarketData {
    return form.getRawValue() as IStockMarketData | NewStockMarketData;
  }

  resetForm(form: StockMarketDataFormGroup, stockMarketData: StockMarketDataFormGroupInput): void {
    const stockMarketDataRawValue = { ...this.getFormDefaults(), ...stockMarketData };
    form.reset(
      {
        ...stockMarketDataRawValue,
        id: { value: stockMarketDataRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StockMarketDataFormDefaults {
    return {
      id: null,
    };
  }
}
