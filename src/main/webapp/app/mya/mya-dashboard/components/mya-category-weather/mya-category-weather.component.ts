import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import { ChartConfiguration, ChartOptions, Tick } from 'chart.js';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-category-weather',
  templateUrl: './mya-category-weather.component.html',
})
export class MyaCategoryWeatherComponent implements OnChanges {
  @Input() monthFrom: Date | null = null;
  @Input() monthTo: Date | null = null;
  @Input() categoryId: number | null = null;
  budgetADate: number | null = null;
  operationADate: number | null = null;
  data: ChartConfiguration<'bar'>['data'] | null = null;
  options: ChartOptions<'bar'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}

  loadAll(): void {
    this.dashboardService
      .getCategoryAmountPerMonthWithMarked(this.categoryId!, dayjs(this.monthFrom), dayjs(this.monthTo))
      .subscribe((res: HttpResponse<any>) => {
        this.budgetADate = res.body.budgetAtDateAmounts[res.body.budgetAtDateAmounts.length - 1];
        this.operationADate = res.body.operationAmounts[res.body.operationAmounts.length - 1];

        this.data = {
          labels: [''],
          datasets: [
            {
              label: 'Budget',
              data: [this.budgetADate!],
              borderColor: '#000000',
              backgroundColor: '#ffffff00',

              borderWidth: 4,
            },
            {
              label: 'Operations',
              data: [this.operationADate!],
              borderColor: '#49ab81',
              backgroundColor: '#49ab81',
              borderWidth: 0,
            },
          ],
        };
        this.options = {
          responsive: true,
          indexAxis: 'y',
          plugins: {
            title: {
              display: false,
              text: 'Consommation du budget',
            },
            legend: {
              display: false,
              position: 'bottom',
            },
            tooltip: {
              position: 'average',
              mode: 'index',
              intersect: false,
              callbacks: {
                label(context: any): string {
                  let label: string = context.dataset.label || '';
                  if (label) {
                    label += ' : ';
                  }
                  label += context.parsed.x.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' });
                  return label;
                },
              },
            },
          },

          scales: {
            y: {
              stacked: true,
              display: false,
              grid: {
                display: false,
              },
            },
            x: {
              display: false,
              ticks: {
                //beginAtZero: true,
                display: false,
                //grid: {
                //  display: false,
                //},
                callback(value: string | number, index: number, ticks: Tick[]): string {
                  return String(value) + ' €';
                },
              },
            },
          },
        };
      });
  }

  ngOnChanges(): void {
    this.loadAll();
  }

  isGreen(): boolean {
    if (!this.operationADate || !this.budgetADate) {
      return true;
    }
    return this.getDeltaRatio() >= 0;
  }

  isOrange(): boolean {
    return !this.isGreen() && this.getDeltaRatio() >= -0.2;
  }

  isRed(): boolean {
    return !this.isGreen() && !this.isOrange();
  }

  getDeltaRatio(): number {
    return (this.operationADate! - this.budgetADate!) / Math.abs(this.budgetADate!);
  }
}
