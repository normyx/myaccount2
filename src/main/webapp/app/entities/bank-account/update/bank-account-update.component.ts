import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BankAccountFormService, BankAccountFormGroup } from './bank-account-form.service';
import { IBankAccount } from '../bank-account.model';
import { BankAccountService } from '../service/bank-account.service';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { IStockPortfolioItem } from 'app/entities/stock-portfolio-item/stock-portfolio-item.model';
import { StockPortfolioItemService } from 'app/entities/stock-portfolio-item/service/stock-portfolio-item.service';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';

@Component({
  selector: 'jhi-bank-account-update',
  templateUrl: './bank-account-update.component.html',
})
export class BankAccountUpdateComponent implements OnInit {
  isSaving = false;
  bankAccount: IBankAccount | null = null;
  bankAccountTypeValues = Object.keys(BankAccountType);

  applicationUsersSharedCollection: IApplicationUser[] = [];
  stockPortfolioItemsSharedCollection: IStockPortfolioItem[] = [];

  editForm: BankAccountFormGroup = this.bankAccountFormService.createBankAccountFormGroup();

  constructor(
    protected bankAccountService: BankAccountService,
    protected bankAccountFormService: BankAccountFormService,
    protected applicationUserService: ApplicationUserService,
    protected stockPortfolioItemService: StockPortfolioItemService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareApplicationUser = (o1: IApplicationUser | null, o2: IApplicationUser | null): boolean =>
    this.applicationUserService.compareApplicationUser(o1, o2);

  compareStockPortfolioItem = (o1: IStockPortfolioItem | null, o2: IStockPortfolioItem | null): boolean =>
    this.stockPortfolioItemService.compareStockPortfolioItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      this.bankAccount = bankAccount;
      if (bankAccount) {
        this.updateForm(bankAccount);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bankAccount = this.bankAccountFormService.getBankAccount(this.editForm);
    if (bankAccount.id !== null) {
      this.subscribeToSaveResponse(this.bankAccountService.update(bankAccount));
    } else {
      this.subscribeToSaveResponse(this.bankAccountService.create(bankAccount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankAccount>>): void {
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

  protected updateForm(bankAccount: IBankAccount): void {
    this.bankAccount = bankAccount;
    this.bankAccountFormService.resetForm(this.editForm, bankAccount);

    this.applicationUsersSharedCollection = this.applicationUserService.addApplicationUserToCollectionIfMissing<IApplicationUser>(
      this.applicationUsersSharedCollection,
      bankAccount.account
    );
    this.stockPortfolioItemsSharedCollection =
      this.stockPortfolioItemService.addStockPortfolioItemToCollectionIfMissing<IStockPortfolioItem>(
        this.stockPortfolioItemsSharedCollection,
        bankAccount.stockPortfolioItem
      );
  }

  protected loadRelationshipsOptions(): void {
    this.applicationUserService
      .query()
      .pipe(map((res: HttpResponse<IApplicationUser[]>) => res.body ?? []))
      .pipe(
        map((applicationUsers: IApplicationUser[]) =>
          this.applicationUserService.addApplicationUserToCollectionIfMissing<IApplicationUser>(applicationUsers, this.bankAccount?.account)
        )
      )
      .subscribe((applicationUsers: IApplicationUser[]) => (this.applicationUsersSharedCollection = applicationUsers));

    this.stockPortfolioItemService
      .query()
      .pipe(map((res: HttpResponse<IStockPortfolioItem[]>) => res.body ?? []))
      .pipe(
        map((stockPortfolioItems: IStockPortfolioItem[]) =>
          this.stockPortfolioItemService.addStockPortfolioItemToCollectionIfMissing<IStockPortfolioItem>(
            stockPortfolioItems,
            this.bankAccount?.stockPortfolioItem
          )
        )
      )
      .subscribe((stockPortfolioItems: IStockPortfolioItem[]) => (this.stockPortfolioItemsSharedCollection = stockPortfolioItems));
  }
}
