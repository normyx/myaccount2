import { Component, OnInit } from '@angular/core';

// tslint:disable-next-line:no-duplicate-imports
import dayjs, { Dayjs } from 'dayjs';
import { BsDatepickerConfig, BsDatepickerViewMode } from 'ngx-bootstrap/datepicker';
import { MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES } from '../../config/mya.constants';

@Component({
  selector: 'jhi-mya-dashboard-account',
  templateUrl: './mya-dashboard-account.component.html',
})
export class MyaDashboardAccountComponent implements OnInit {
  currentMonth: Date;
  balanceDateFrom: Date;
  balanceDateTo: Date;
  selectedMonthMinusNumberOfMonth: Date;

  categoryIds: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

  selectedMonth: Date = new Date();
  bsConfig?: Partial<BsDatepickerConfig>;
  minMode: BsDatepickerViewMode = 'month';

  constructor() {
    const currentDate: Dayjs = dayjs(Date.now());
    this.currentMonth = new Date(currentDate.year(), currentDate.month(), 1);
    this.selectedMonth = this.currentMonth;
    this.balanceDateFrom = new Date(currentDate.year(), currentDate.month() - MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES, currentDate.day());
    this.selectedMonthMinusNumberOfMonth = new Date(currentDate.year(), currentDate.month() - MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES, 1);
    this.balanceDateTo = new Date(currentDate.year(), currentDate.month() + MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES, currentDate.day());
  }

  ngOnInit(): void {
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
    this.balanceDateFrom = new Date(
      this.selectedMonth.getFullYear(),
      this.selectedMonth.getMonth() - MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES,
      1
    );
    this.balanceDateTo = new Date(
      this.selectedMonth.getFullYear(),
      this.selectedMonth.getMonth() + MYA_REPORT_NUMBER_OF_MONTHS_FOR_RANGES,
      1
    );
    this.selectedMonthMinusNumberOfMonth = this.balanceDateFrom;
  }
}
