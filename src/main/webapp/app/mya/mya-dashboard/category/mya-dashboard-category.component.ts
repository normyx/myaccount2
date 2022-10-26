import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

import { ActivatedRoute, Params } from '@angular/router';
import dayjs, { Dayjs } from 'dayjs';
import { BsDatepickerConfig, BsDatepickerViewMode } from 'ngx-bootstrap/datepicker';
import { DATE_FORMAT } from '../../../config/input.constants';
import { ICategory } from '../../../entities/category/category.model';
import { CategoryService } from '../../../entities/category/service/category.service';
import { MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES } from '../../config/mya.constants';

@Component({
  selector: 'jhi-mya-dashboard-category',
  templateUrl: './mya-dashboard-category.component.html',
})
export class MyaDashboardCategoryComponent implements OnInit {
  currentMonth: Date;

  selectedMonthMinusNumberOfMonth: Date = new Date();
  category: ICategory | null = null;
  selectedCategoryId: number | null = null;
  categories: ICategory[] | null = null;
  selectedMonth: Date = new Date();
  bsConfig?: Partial<BsDatepickerConfig>;
  minMode: BsDatepickerViewMode = 'month';

  constructor(protected activatedRoute: ActivatedRoute, protected categoryService: CategoryService) {
    const currentDate: Dayjs = dayjs(Date.now());
    this.currentMonth = new Date(currentDate.year(), currentDate.month(), 1);
    this.selectedMonth = this.currentMonth;
    this.setSelectedMonth();
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((params: Params) => {
      if (params['month']) {
        this.selectedMonth = dayjs(params['month']).toDate();
        this.setSelectedMonth();
      }
    });
    this.activatedRoute.data.subscribe(({ category }) => {
      if (category) {
        this.category = category;
        this.selectedCategoryId = category.id;
      }
    });
    this.categoryService.query().subscribe((res: HttpResponse<ICategory[]>) => (this.categories = res.body!));
    this.bsConfig = Object.assign(
      {},
      {
        minMode: this.minMode,
        containerClass: 'theme-default',
        isAnimated: true,
        dateInputFormat: 'MMM-YY',
      }
    );
  }

  setSelectedMonth(): void {
    this.selectedMonthMinusNumberOfMonth = new Date(
      this.selectedMonth.getFullYear(),
      this.selectedMonth.getMonth() - MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES,
      1
    );
  }

  getMonthStr(): string | null {
    return dayjs(this.selectedMonth).format(DATE_FORMAT);
  }
}
