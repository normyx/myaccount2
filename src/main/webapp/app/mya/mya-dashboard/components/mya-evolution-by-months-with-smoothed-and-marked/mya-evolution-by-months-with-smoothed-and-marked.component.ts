import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import { ChartConfiguration, ChartOptions, Tick } from 'chart.js';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-evolution-by-months-with-smoothed-and-marked',
  templateUrl: './mya-evolution-by-months-with-smoothed-and-marked.component.html',
})
export class MyaEvolutionByMonthsWithSmoothedAndMarkedComponent implements OnChanges {
  @Input() categoryId: number | null = null;
  @Input() monthTo: Date | null = null;
  @Input() monthFrom: Date | null = null;
  @Input() height: string | null = null;
  @Input() displayX = true;
  data: ChartConfiguration<'line'>['data'] | null = null;
  options: ChartOptions<'line'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}

  loadAll(): void {
    if (this.categoryId && this.monthTo && this.monthFrom) {
      this.dashboardService
        .getCategoryAmountPerMonthWithMarked(this.categoryId, dayjs(this.monthFrom), dayjs(this.monthTo))
        .subscribe((res: HttpResponse<any>) => {
          this.data = {
            labels: res.body.months,
            datasets: [
              {
                label: 'Montant',
                data: res.body.operationAmounts,
                borderColor: '#49ab81',
                backgroundColor: '#49ab81',
                pointBorderColor: '#49ab81',
                pointBackgroundColor: '#49ab81',
                fill: false,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
                borderWidth: 2,
              },
              {
                label: 'Budget non lissé pointé',
                data: res.body.budgetUnSmoothedMarkedAmounts,
                borderColor: '#3b5998',
                backgroundColor: '#3b5998',
                pointBorderColor: '#3b5998',
                pointBackgroundColor: '#3b5998',
                fill: true,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
                borderWidth: 2,
              },
              {
                label: 'Montant non lissé non pointé',
                data: res.body.budgetUnSmoothedUnMarkedAmounts,
                borderColor: '#8b9dc3',
                backgroundColor: '#8b9dc3',
                pointBorderColor: '#8b9dc3',
                pointBackgroundColor: '#8b9dc3',
                fill: true,
                borderWidth: 2,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
              },
              {
                label: 'Budget Lissé',
                data: res.body.budgetSmoothedAmounts,
                borderColor: '#dfe3ee',
                backgroundColor: '#dfe3ee',
                pointBorderColor: '#dfe3ee',
                pointBackgroundColor: '#dfe3ee',
                fill: true,
                borderWidth: 2,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
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
                position: 'average',
                mode: 'index',
                intersect: false,
                callbacks: {
                  label(context: any): string {
                    let label: string = context.dataset.label || '';
                    if (label) {
                      label += ' : ';
                    }
                    label += Math.round(context.parsed.y * 100) / 100;
                    label += ' €';
                    return label;
                  },
                },
              },
            },
            scales: {
              y: {
                title: {
                  display: false,
                  text: 'Montants',
                },
                ticks: {
                  //suggestedMax: 0,
                  callback(value: string | number, index: number, ticks: Tick[]): string {
                    return String(value) + ' €';
                  },
                },
              },
              x: {
                display: this.displayX,
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
