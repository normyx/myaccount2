import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RealEstateItemFormService, RealEstateItemFormGroup } from './real-estate-item-form.service';
import { IRealEstateItem } from '../real-estate-item.model';
import { RealEstateItemService } from '../service/real-estate-item.service';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';

@Component({
  selector: 'jhi-real-estate-item-update',
  templateUrl: './real-estate-item-update.component.html',
})
export class RealEstateItemUpdateComponent implements OnInit {
  isSaving = false;
  realEstateItem: IRealEstateItem | null = null;

  bankAccountsSharedCollection: IBankAccount[] = [];

  editForm: RealEstateItemFormGroup = this.realEstateItemFormService.createRealEstateItemFormGroup();

  constructor(
    protected realEstateItemService: RealEstateItemService,
    protected realEstateItemFormService: RealEstateItemFormService,
    protected bankAccountService: BankAccountService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareBankAccount = (o1: IBankAccount | null, o2: IBankAccount | null): boolean => this.bankAccountService.compareBankAccount(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ realEstateItem }) => {
      this.realEstateItem = realEstateItem;
      if (realEstateItem) {
        this.updateForm(realEstateItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const realEstateItem = this.realEstateItemFormService.getRealEstateItem(this.editForm);
    if (realEstateItem.id !== null) {
      this.subscribeToSaveResponse(this.realEstateItemService.update(realEstateItem));
    } else {
      this.subscribeToSaveResponse(this.realEstateItemService.create(realEstateItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRealEstateItem>>): void {
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

  protected updateForm(realEstateItem: IRealEstateItem): void {
    this.realEstateItem = realEstateItem;
    this.realEstateItemFormService.resetForm(this.editForm, realEstateItem);

    this.bankAccountsSharedCollection = this.bankAccountService.addBankAccountToCollectionIfMissing<IBankAccount>(
      this.bankAccountsSharedCollection,
      realEstateItem.bankAccount
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bankAccountService
      .query()
      .pipe(map((res: HttpResponse<IBankAccount[]>) => res.body ?? []))
      .pipe(
        map((bankAccounts: IBankAccount[]) =>
          this.bankAccountService.addBankAccountToCollectionIfMissing<IBankAccount>(bankAccounts, this.realEstateItem?.bankAccount)
        )
      )
      .subscribe((bankAccounts: IBankAccount[]) => (this.bankAccountsSharedCollection = bankAccounts));
  }
}
