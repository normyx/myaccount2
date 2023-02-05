import { HttpResponse } from '@angular/common/http';
import { Component, Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DATE_FORMAT } from 'app/config/input.constants';

import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { NewOperation } from 'app/entities/operation/operation.model';
import { SubCategoryService } from 'app/entities/sub-category/service/sub-category.service';
import { ISubCategory } from 'app/entities/sub-category/sub-category.model';
import { EVENT_LOAD_BANK_ACCOUNTS } from 'app/mya/config/mya.event.constants';
import { MyaOperationService } from 'app/mya/mya-operation/service/mya-operation.service';
import dayjs from 'dayjs/esm';
import { EventManager } from '../../../core/util/event-manager.service';

interface IBankAccountBalanceUpdate {
  initialAmount?: number | null;
  atDateAmount?: number | null;
  balanceDate?: dayjs.Dayjs | null;
  category?: ICategory | null;
  subCategory?: ISubCategory | null;
}

type MyaBankAccountBalanceUpdateFormGroupContent = {
  initialAmount: FormControl<IBankAccountBalanceUpdate['initialAmount']>;
  atDateAmount: FormControl<IBankAccountBalanceUpdate['atDateAmount']>;
  balanceDate: FormControl<IBankAccountBalanceUpdate['balanceDate']>;
  category: FormControl<IBankAccountBalanceUpdate['category']>;
  subCategory: FormControl<IBankAccountBalanceUpdate['subCategory']>;
};
type MyaBankAccountBalanceUpdateFormGroup = FormGroup<MyaBankAccountBalanceUpdateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MyaBankAccountBalanceUpdateFormService {
  createBankAccountBalanceUpdateFormGroup(bankAccountBalanceUpdate: IBankAccountBalanceUpdate = {}): MyaBankAccountBalanceUpdateFormGroup {
    const bankAccountBalanceUpdateRawValue = {
      ...bankAccountBalanceUpdate,
    };
    return new FormGroup<MyaBankAccountBalanceUpdateFormGroupContent>({
      initialAmount: new FormControl(bankAccountBalanceUpdateRawValue.initialAmount, {
        validators: [Validators.required],
      }),
      atDateAmount: new FormControl(bankAccountBalanceUpdateRawValue.initialAmount, {
        validators: [Validators.required],
      }),
      balanceDate: new FormControl(bankAccountBalanceUpdateRawValue.balanceDate, {
        validators: [Validators.required],
      }),
      category: new FormControl(bankAccountBalanceUpdateRawValue.category, {
        validators: [Validators.required],
      }),
      subCategory: new FormControl(bankAccountBalanceUpdateRawValue.subCategory, {
        validators: [Validators.required],
      }),
    });
  }

  getBankAccountBalanceUpdate(form: MyaBankAccountBalanceUpdateFormGroup): IBankAccountBalanceUpdate {
    return form.getRawValue();
  }

  resetFormBalanceUpdate(form: MyaBankAccountBalanceUpdateFormGroup, bankAccount: IBankAccountBalanceUpdate): void {
    const bankAccountRawValue = { ...bankAccount };
    form.reset(
      {
        ...bankAccountRawValue,
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }
}

@Component({
  templateUrl: './mya-bank-account-balanceupdate-dialog.component.html',
})
export class MyaBankAccountBalanceUpdateDialogComponent {
  bankAccount: IBankAccount | null = null;
  bankAccountBalanceUpdate: IBankAccountBalanceUpdate | null = null;
  sumOfOperationAmount: number | null = null;
  //lastOperationDate: dayjs.Dayjs | null = null;
  defaultSubCategory: ISubCategory | null = null;
  categories: ICategory[] | null = null;
  selectedCategory: ICategory | null = null;
  subCategoriesOptions: ISubCategory[] | null = null;
  subCategories: ISubCategory[] | null = null;

  editForm: MyaBankAccountBalanceUpdateFormGroup = this.formService.createBankAccountBalanceUpdateFormGroup();

  constructor(
    protected bankAccountService: BankAccountService,
    protected activeModal: NgbActiveModal,
    protected formService: MyaBankAccountBalanceUpdateFormService,
    protected operationService: MyaOperationService,
    protected subCategoryService: SubCategoryService,
    protected categoryService: CategoryService,
    private eventManager: EventManager
  ) {}

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  compareSubCategory = (o1: ISubCategory | null, o2: ISubCategory | null): boolean => this.subCategoryService.compareSubCategory(o1, o2);

  cancel(): void {
    this.activeModal.dismiss();
  }

  setSubCategoriesEvent(): void {
    this.setSubCategories(this.selectedCategory);
  }

  setSubCategories(category: ICategory | null): void {
    if (category) {
      this.subCategoriesOptions = this.subCategories!.filter(sc => sc.category?.id === category.id);
      this.editForm.patchValue({ subCategory: this.subCategoriesOptions[0] });
    } else {
      this.subCategoriesOptions = new Array<ISubCategory>();
    }
  }

  setSubCaterory(subCategory: ISubCategory): void {
    this.setSubCategories(subCategory.category!);
  }

  getCategory(subCategory: ISubCategory): ICategory {
    const cat = this.categories?.find(e => e.id === subCategory.category?.id);
    if (cat) {
      return cat;
    } else {
      throw new Error('Catégory non trouvée');
    }
  }

  setBankAccount(bankAccount: IBankAccount): void {
    this.bankAccount = bankAccount;
    this.categoryService.query().subscribe((res: HttpResponse<ICategory[]>) => {
      this.categories = res.body;
      this.subCategoryService.query().subscribe((res2: HttpResponse<ISubCategory[]>) => {
        this.subCategories = res2.body;
        this.defaultSubCategory = this.subCategories!.find(e => e.id === 31)!;
        this.operationService.sumOfAmountForBankAccount(this.bankAccount!.id).subscribe((res3: HttpResponse<number>) => {
          this.sumOfOperationAmount = res3.body;
          this.selectedCategory = this.getCategory(this.defaultSubCategory!);
          this.bankAccountBalanceUpdate = {
            initialAmount: this.bankAccount!.initialAmount,
            atDateAmount: this.sumOfOperationAmount! + this.bankAccount!.initialAmount!,
            balanceDate: dayjs(),
            category: this.selectedCategory,
            subCategory: this.defaultSubCategory,
          };

          this.setSubCategories(this.selectedCategory);
          this.formService.resetFormBalanceUpdate(this.editForm, this.bankAccountBalanceUpdate);
        });
      });
    });

    this.subCategoryService.find(31).subscribe((res: HttpResponse<ISubCategory>) => {
      this.defaultSubCategory = res.body;
    });
    /*this.operationService.lastOperationDateForBankAccount(this.bankAccount.id).subscribe((res: HttpResponse<dayjs.Dayjs>) => {
      this.lastOperationDate = res.body;
    });*/
  }

  confirm(): void {
    const bankAccountBalanceUpdate = this.formService.getBankAccountBalanceUpdate(this.editForm);
    this.bankAccount!.initialAmount = bankAccountBalanceUpdate.initialAmount;
    const adjustmentAmount = bankAccountBalanceUpdate.atDateAmount! - this.sumOfOperationAmount! - this.bankAccount!.initialAmount!;
    if (adjustmentAmount !== 0) {
      const operation: NewOperation = {
        id: null,
        label: 'Ajustement',
        date: bankAccountBalanceUpdate.balanceDate,
        amount: adjustmentAmount,
        note: 'Ajustement du ' + dayjs().format(DATE_FORMAT),

        isUpToDate: true,
        deletingHardLock: true,
        subCategory: bankAccountBalanceUpdate.subCategory,
        account: this.bankAccount!.account,
        bankAccount: this.bankAccount,
        budgetItemPeriod: null,
      };
      this.operationService.create(operation).subscribe();
    }
    this.bankAccountService.update(this.bankAccount!).subscribe(() => {
      this.eventManager.broadcast({ name: EVENT_LOAD_BANK_ACCOUNTS, content: 'OK' });
      this.activeModal.close();
    });
  }
}
