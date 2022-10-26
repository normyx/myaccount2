import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

// tslint:disable-next-line:no-duplicate-imports
import { ActivatedRoute, Params } from '@angular/router';
import dayjs, { Dayjs } from 'dayjs';
import { BsDatepickerConfig, BsDatepickerViewMode } from 'ngx-bootstrap/datepicker';
import { ICategory } from '../../../entities/category/category.model';
import { CategoryService } from '../../../entities/category/service/category.service';

@Component({
  selector: 'jhi-mya-dashboard-category-details',
  templateUrl: './mya-dashboard-category-details.component.html',
})
export class MyaDashboardCategoryDetailsComponent implements OnInit {
  currentMonth: Date;
  category: ICategory | null = null;
  selectedCategoryId: number | null = null;
  categories: ICategory[] | null = null;

  selectedMonth: Date = new Date();
  selectedMonthLastDay: Date = new Date();
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
    this.selectedMonthLastDay = dayjs(this.selectedMonth).clone().add(1, 'month').add(-1, 'day').toDate();
  }

  setSelectedCategoryId(): void {
    if (this.categories && this.selectedCategoryId) {
      this.categories.forEach(c => {
        if (c.id === this.selectedCategoryId) {
          this.category = c;
        }
      });
    }
  }
}
