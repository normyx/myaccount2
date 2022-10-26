import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-category-split',
  templateUrl: './mya-category-split.component.html',
})
export class MyaCategorySplitComponent implements OnChanges {
  @Input() categoryId: number | null = null;
  @Input() month: Date | null = null;
  @Input() numberOfMonth: number | null = null;
  @Input() height: string | null = null;
  data: any;
  options: any;

  constructor(private dashboardService: MyaDashboardService) {}

  getCategoryColors(n: number): string[] {
    return [
      '#F44336',
      '#9C27B0',
      '#FFC107',
      '#673AB7',
      '#009688',
      '#4CAF50',
      '#607D8B',
      '#CDDC39',
      '#795548',
      '#9E9E9E',
      '#E91E63',
      '#FF9800',
    ].slice(0, n);
  }

  loadAll(): void {
    if (this.categoryId && this.month && this.numberOfMonth) {
      this.dashboardService
        .getSubCategorySplit(this.categoryId, dayjs(this.month), this.numberOfMonth)
        .subscribe((res: HttpResponse<any>) => {
          this.data = {
            labels: res.body.categoryNames,
            datasets: [
              {
                data: res.body.amounts,
                backgroundColor: this.getCategoryColors(res.body.amounts.length),
              },
            ],
          };
          this.options = {
            responsive: true,
            plugins: {
              legend: {
                display: false,
                position: 'bottom',
              },
              tooltip: {
                callbacks: {
                  label(context: any): string {
                    let l: string = context.label || '';
                    if (l) {
                      l += ' : ';
                    }
                    l += Math.round(context.parsed * 100) / 100;
                    l += ' €';
                    return l;
                  },
                },
              },
            },
          };
        });
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }
}
