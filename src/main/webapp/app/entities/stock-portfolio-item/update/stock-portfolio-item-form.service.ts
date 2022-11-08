import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
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

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockPortfolioItem | NewStockPortfolioItem> = Omit<T, 'lastStockUpdate' | 'lastCurrencyUpdate'> & {
  lastStockUpdate?: string | null;
  lastCurrencyUpdate?: string | null;
};

type StockPortfolioItemFormRawValue = FormValueOf<IStockPortfolioItem>;

type NewStockPortfolioItemFormRawValue = FormValueOf<NewStockPortfolioItem>;

type StockPortfolioItemFormDefaults = Pick<NewStockPortfolioItem, 'id' | 'lastStockUpdate' | 'lastCurrencyUpdate'>;

type StockPortfolioItemFormGroupContent = {
  id: FormControl<StockPortfolioItemFormRawValue['id'] | NewStockPortfolioItem['id']>;
  stockSymbol: FormControl<StockPortfolioItemFormRawValue['stockSymbol']>;
  stockCurrency: FormControl<StockPortfolioItemFormRawValue['stockCurrency']>;
  stockAcquisitionDate: FormControl<StockPortfolioItemFormRawValue['stockAcquisitionDate']>;
  stockSharesNumber: FormControl<StockPortfolioItemFormRawValue['stockSharesNumber']>;
  stockAcquisitionPrice: FormControl<StockPortfolioItemFormRawValue['stockAcquisitionPrice']>;
  stockCurrentPrice: FormControl<StockPortfolioItemFormRawValue['stockCurrentPrice']>;
  stockCurrentDate: FormControl<StockPortfolioItemFormRawValue['stockCurrentDate']>;
  stockAcquisitionCurrencyFactor: FormControl<StockPortfolioItemFormRawValue['stockAcquisitionCurrencyFactor']>;
  stockCurrentCurrencyFactor: FormControl<StockPortfolioItemFormRawValue['stockCurrentCurrencyFactor']>;
  stockPriceAtAcquisitionDate: FormControl<StockPortfolioItemFormRawValue['stockPriceAtAcquisitionDate']>;
  stockType: FormControl<StockPortfolioItemFormRawValue['stockType']>;
  lastStockUpdate: FormControl<StockPortfolioItemFormRawValue['lastStockUpdate']>;
  lastCurrencyUpdate: FormControl<StockPortfolioItemFormRawValue['lastCurrencyUpdate']>;
  bankAccount: FormControl<StockPortfolioItemFormRawValue['bankAccount']>;
};

export type StockPortfolioItemFormGroup = FormGroup<StockPortfolioItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockPortfolioItemFormService {
  createStockPortfolioItemFormGroup(stockPortfolioItem: StockPortfolioItemFormGroupInput = { id: null }): StockPortfolioItemFormGroup {
    const stockPortfolioItemRawValue = this.convertStockPortfolioItemToStockPortfolioItemRawValue({
      ...this.getFormDefaults(),
      ...stockPortfolioItem,
    });
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
      stockPriceAtAcquisitionDate: new FormControl(stockPortfolioItemRawValue.stockPriceAtAcquisitionDate, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockType: new FormControl(stockPortfolioItemRawValue.stockType, {
        validators: [Validators.required],
      }),
      lastStockUpdate: new FormControl(stockPortfolioItemRawValue.lastStockUpdate),
      lastCurrencyUpdate: new FormControl(stockPortfolioItemRawValue.lastCurrencyUpdate),
      bankAccount: new FormControl(stockPortfolioItemRawValue.bankAccount),
    });
  }

  getStockPortfolioItem(form: StockPortfolioItemFormGroup): IStockPortfolioItem | NewStockPortfolioItem {
    return this.convertStockPortfolioItemRawValueToStockPortfolioItem(
      form.getRawValue() as StockPortfolioItemFormRawValue | NewStockPortfolioItemFormRawValue
    );
  }

  resetForm(form: StockPortfolioItemFormGroup, stockPortfolioItem: StockPortfolioItemFormGroupInput): void {
    const stockPortfolioItemRawValue = this.convertStockPortfolioItemToStockPortfolioItemRawValue({
      ...this.getFormDefaults(),
      ...stockPortfolioItem,
    });
    form.reset(
      {
        ...stockPortfolioItemRawValue,
        id: { value: stockPortfolioItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StockPortfolioItemFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastStockUpdate: currentTime,
      lastCurrencyUpdate: currentTime,
    };
  }

  private convertStockPortfolioItemRawValueToStockPortfolioItem(
    rawStockPortfolioItem: StockPortfolioItemFormRawValue | NewStockPortfolioItemFormRawValue
  ): IStockPortfolioItem | NewStockPortfolioItem {
    return {
      ...rawStockPortfolioItem,
      lastStockUpdate: dayjs(rawStockPortfolioItem.lastStockUpdate, DATE_TIME_FORMAT),
      lastCurrencyUpdate: dayjs(rawStockPortfolioItem.lastCurrencyUpdate, DATE_TIME_FORMAT),
    };
  }

  private convertStockPortfolioItemToStockPortfolioItemRawValue(
    stockPortfolioItem: IStockPortfolioItem | (Partial<NewStockPortfolioItem> & StockPortfolioItemFormDefaults)
  ): StockPortfolioItemFormRawValue | PartialWithRequiredKeyOf<NewStockPortfolioItemFormRawValue> {
    return {
      ...stockPortfolioItem,
      lastStockUpdate: stockPortfolioItem.lastStockUpdate ? stockPortfolioItem.lastStockUpdate.format(DATE_TIME_FORMAT) : undefined,
      lastCurrencyUpdate: stockPortfolioItem.lastCurrencyUpdate
        ? stockPortfolioItem.lastCurrencyUpdate.format(DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
