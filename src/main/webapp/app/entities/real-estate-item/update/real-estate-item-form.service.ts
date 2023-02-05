import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRealEstateItem, NewRealEstateItem } from '../real-estate-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRealEstateItem for edit and NewRealEstateItemFormGroupInput for create.
 */
type RealEstateItemFormGroupInput = IRealEstateItem | PartialWithRequiredKeyOf<NewRealEstateItem>;

type RealEstateItemFormDefaults = Pick<NewRealEstateItem, 'id'>;

type RealEstateItemFormGroupContent = {
  id: FormControl<IRealEstateItem['id'] | NewRealEstateItem['id']>;
  loanValue: FormControl<IRealEstateItem['loanValue']>;
  totalValue: FormControl<IRealEstateItem['totalValue']>;
  percentOwned: FormControl<IRealEstateItem['percentOwned']>;
  itemDate: FormControl<IRealEstateItem['itemDate']>;
  bankAccount: FormControl<IRealEstateItem['bankAccount']>;
};

export type RealEstateItemFormGroup = FormGroup<RealEstateItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RealEstateItemFormService {
  createRealEstateItemFormGroup(realEstateItem: RealEstateItemFormGroupInput = { id: null }): RealEstateItemFormGroup {
    const realEstateItemRawValue = {
      ...this.getFormDefaults(),
      ...realEstateItem,
    };
    return new FormGroup<RealEstateItemFormGroupContent>({
      id: new FormControl(
        { value: realEstateItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      loanValue: new FormControl(realEstateItemRawValue.loanValue, {
        validators: [Validators.required, Validators.min(0)],
      }),
      totalValue: new FormControl(realEstateItemRawValue.totalValue, {
        validators: [Validators.required, Validators.min(0)],
      }),
      percentOwned: new FormControl(realEstateItemRawValue.percentOwned, {
        validators: [Validators.required, Validators.min(0), Validators.max(100)],
      }),
      itemDate: new FormControl(realEstateItemRawValue.itemDate, {
        validators: [Validators.required],
      }),
      bankAccount: new FormControl(realEstateItemRawValue.bankAccount, {
        validators: [Validators.required],
      }),
    });
  }

  getRealEstateItem(form: RealEstateItemFormGroup): IRealEstateItem | NewRealEstateItem {
    return form.getRawValue() as IRealEstateItem | NewRealEstateItem;
  }

  resetForm(form: RealEstateItemFormGroup, realEstateItem: RealEstateItemFormGroupInput): void {
    const realEstateItemRawValue = { ...this.getFormDefaults(), ...realEstateItem };
    form.reset(
      {
        ...realEstateItemRawValue,
        id: { value: realEstateItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RealEstateItemFormDefaults {
    return {
      id: null,
    };
  }
}
