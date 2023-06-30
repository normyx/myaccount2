import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import { ChartConfiguration, ChartOptions, Tick } from 'chart.js';
import 'chartjs-adapter-moment';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-evolution-in-month',
  templateUrl: './mya-evolution-in-month.component.html',
})
export class MyaEvolutionInMonthReportComponent implements OnChanges {
  @Input() month: Date | null = null;
  @Input() categoryId: number | null = null;
  @Input() height = '40vh';
  @Input() displayX = true;
  // accountCategoryMonthReport: IAccountCategoryMonthReport;
  data: ChartConfiguration<'line'>['data'] | null = null;
  options: ChartOptions<'line'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}

  feedReportData(res: HttpResponse<any>): void {
    const dates = res.body.dates;
    const operationAmounts = res.body.operationAmounts;
    const budgetAmounts = res.body.budgetAmounts;
    const predictiveBudgetAmounts = res.body.predictiveBudgetAmounts;
    const deltaAmount = new Array<number>();
    for (let _i = 0; _i < operationAmounts.length; _i++) {
      if (operationAmounts[_i]) {
        deltaAmount.push(operationAmounts[_i] - budgetAmounts[_i]);
      }
    }
    this.data = {
      labels: dates,
      datasets: [
        {
          label: 'Operation',
          data: operationAmounts,
          borderColor: '#0099ff',
          backgroundColor: '#0099ff',
          pointBorderColor: '#0099ff',
          pointBackgroundColor: '#0099ff',
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
          borderWidth: 2,
          yAxisID: 'y',
        },
        {
          label: 'Budget',
          data: budgetAmounts,
          borderColor: '#565656',
          backgroundColor: '#565656',
          pointBorderColor: '#565656',
          pointBackgroundColor: '#565656',
          borderWidth: 1,
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
          yAxisID: 'y',
        },
        {
          label: 'Evolution prévue',
          data: predictiveBudgetAmounts,
          borderColor: '#ff0000',
          backgroundColor: '#ff0000',
          pointBorderColor: '#ff0000',
          pointBackgroundColor: '#ff0000',
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
          borderWidth: 2,
          yAxisID: 'y',
        },
        {
          label: 'Delta',
          data: deltaAmount,
          borderColor: '#2222222',
          backgroundColor: '#2222222',
          pointBorderColor: '#2222222',
          pointBackgroundColor: '#2222222',
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
          borderWidth: 1,
          borderDash: [2, 2],
          yAxisID: 'y1',
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
            title(context: any): string[] {
              return [dayjs(context[0].label).format('DD MMM-YY')];
            },
            label(context: any): string {
              let label: string = context.dataset.label || '';
              if (label) {
                label += ' : ';
              }
              label += context.parsed.y.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' });
              return label;
            },
          },
        },
      },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Montants',
          },
          ticks: {
            callback(value: string | number, index: number, ticks: Tick[]): string {
              return String(value) + ' €';
            },
          },
        },
        y1: {
          title: {
            display: true,
            text: 'Delta',
          },
          ticks: {
            callback(value: string | number, index: number, ticks: Tick[]): string {
              return String(value) + ' €';
            },
          },
          position: 'right',
          grid: {
            drawOnChartArea: true, // only want the grid lines for one axis to show up
            drawTicks: false,

            color(context) {
              if (context.tick.value === 0) {
                return 'red';
              } else {
                return 'transparent';
              }
            },
          },
        },
        x: {
          display: this.displayX,
          title: {
            display: true,
          },
          type: 'time',

          time: {
            unit: 'day',
            stepSize: 1,
            displayFormats: {
              month: 'DD MMM-YY',
            },
          },
        },
      },
    };
  }

  loadAll(): void {
    if (this.month) {
      this.dashboardService
        .getEvolutionInMonth(dayjs(this.month), this.categoryId)
        .subscribe((res: HttpResponse<any>) => this.feedReportData(res));
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }
}
