import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import 'chartjs-adapter-moment';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-evolution-between-dates',

  templateUrl: './mya-evolution-between-dates.component.html',
})
export class MyaEvolutionBetweenDatesComponent implements OnChanges {
  @Input() dateFrom: Date | null = null;
  @Input() dateTo: Date | null = null;
  @Input() height = '30vh';
  @Input() bankAccountId: number | null = null;
  data: any;
  options: any;
  maxDate: Date | null = null;
  maxAmount: number | null = null;

  constructor(private dashboardService: MyaDashboardService) {}
  ngOnChanges(): void {
    this.loadAll();
  }
  protected feedReportData(res: HttpResponse<any>): void {
    const dates = new Array<any>();
    const amounts = new Array<any>();
    const predictiveAmounts = new Array<any>();
    for (let i = 0; i < res.body.dates.length; i++) {
      if (res.body.amounts[i] !== null || res.body.predictiveAmounts[i] !== null) {
        dates.push(res.body.dates[i]);
        amounts.push(res.body.amounts[i]);
        predictiveAmounts.push(res.body.predictiveAmounts[i]);
      }
    }
    this.maxDate = dates[dates.length - 1];
    this.maxAmount = amounts[amounts.length - 1];
    this.data = {
      labels: dates,
      datasets: [
        {
          label: 'Operations',
          data: amounts,
          borderColor: '#0099ff',
          backgroundColor: '#0099ff',
          fill: false,
          pointRadius: 0,
        },
        {
          label: 'Projections',
          data: predictiveAmounts,
          borderColor: '#ff0000',
          backgroundColor: '#ff0000',
          fill: false,
          pointRadius: 0,
        },
      ],
    };
    this.options = {
      responsive: true,
      plugins: {
        title: {
          display: false,
          text: 'Evolutions du solde',
          fontSize: 12,
        },
        tooltip: {
          position: 'average',
          mode: 'index',
          intersect: false,
          callbacks: {
            title(context: any): string[] {
              return [dayjs(context[0].label).format('MMM-YY')];
            },
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
        legend: {
          display: false,
          position: 'bottom',
        },
      },

      scales: {
        y: {
          title: {
            display: false,
            text: 'Montants',
          },
          ticks: {
            callback(value: string): string {
              return value + ' €';
            },
          },
        },
        x: {
          title: {
            display: true,
          },
          type: 'time',

          time: {
            unit: 'month',
            stepSize: 1,
            displayFormats: {
              month: 'MMM YYYY',
            },
          },
        },
      },
    };
  }

  protected loadAll(): void {
    if (this.dateFrom && this.dateTo) {
      this.dashboardService
        .getEvolutionBetweenDates(dayjs(this.dateFrom), dayjs(this.dateTo), this.bankAccountId)
        .subscribe((res: HttpResponse<any>) => this.feedReportData(res));
    }
  }
}
