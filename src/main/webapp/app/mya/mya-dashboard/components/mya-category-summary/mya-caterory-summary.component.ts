import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DATE_FORMAT } from '../../../../config/input.constants';
import { ICategory } from '../../../../entities/category/category.model';
import { CategoryService } from '../../../../entities/category/service/category.service';

@Component({
  selector: 'jhi-mya-category-summary',
  templateUrl: './mya-caterory-summary.component.html',
})
export class MyaCategorySummaryComponent implements OnChanges {
  @Input() categoryId: number | null = null;
  @Input() monthTo: Date | null = null;
  @Input() monthFrom: Date | null = null;
  @Input() isSummary = true;

  category: Observable<ICategory | null> | null = null;

  constructor(private categoryService: CategoryService) {}

  loadAll(): void {
    if (this.categoryId) {
      this.category = this.categoryService.find(this.categoryId).pipe(map((res: HttpResponse<ICategory>) => res.body));
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }

  getMonthStr(): string | null {
    if (this.monthTo) {
      return dayjs(this.monthTo).format(DATE_FORMAT);
    } else {
      return null;
    }
  }
}
