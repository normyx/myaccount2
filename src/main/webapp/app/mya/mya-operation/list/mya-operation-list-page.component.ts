import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbDate, NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';

import { ICategory } from '../../../entities/category/category.model';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { CategoryService } from '../../../entities/category/service/category.service';
import { MyaOperationService } from '../service/mya-operation.service';
import { MyaBankAccountService } from '../../mya-bank-account/service/mya-bank-account.service';

@Component({
  selector: 'jhi-mya-operation-list-page',
  templateUrl: './mya-operation-list-page.component.html',
})
export class MyaOperationListPageComponent implements OnInit {
  currentSearch = '';

  dateFromStruct: NgbDateStruct | null = null;
  dateToStruct: NgbDateStruct | null = null;

  eventSubscriber: Subscription | null = null;

  dateFrom: Date | null = null;
  dateTo: Date | null = null;
  categories: ICategory[] | null = null;
  bankAccounts: IBankAccount[] | null = null;

  selectedCategory: ICategory | null = null;
  selectedBankAccount: IBankAccount | null = null;
  contains: string | null = null;

  constructor(
    protected modalService: NgbModal,
    protected categoryService: CategoryService,
    protected operationService: MyaOperationService,
    protected bankAccountService: MyaBankAccountService
  ) {}

  resetFilters(): void {
    this.contains = null;
    this.dateFrom = null;
    this.dateTo = null;
    this.selectedCategory = null;
    this.selectedBankAccount = null;
  }

  setDateFrom(dateFrom: NgbDate): void {
    this.dateFromStruct = dateFrom;
    this.dateFrom = new Date(dateFrom.year, dateFrom.month, dateFrom.day);
  }

  setDateTo(dateTo: NgbDate): void {
    this.dateToStruct = dateTo;
    this.dateTo = new Date(dateTo.year, dateTo.month, dateTo.day);
  }

  setContains(containsEvent: any): void {
    this.contains = containsEvent.target.value;
  }

  setCurrentSearch(search: string): void {
    this.currentSearch = search;
  }

  ngOnInit(): void {
    this.loadDependencies();
  }

  loadDependencies(): void {
    this.categoryService.query().subscribe((categories: HttpResponse<ICategory[]>) => {
      this.categories = categories.body;
    });
    this.bankAccountService.queryWithSignedInUser().subscribe((bankAccounts: HttpResponse<IBankAccount[]>) => {
      this.bankAccounts = bankAccounts.body;
    });
  }
}
