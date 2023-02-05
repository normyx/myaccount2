import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges, OnDestroy } from '@angular/core';
import { DATE_FORMAT } from 'app/config/input.constants';
import { Chart, Tick } from 'chart.js';
import 'chartjs-adapter-moment';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-all-account-evolution',
  templateUrl: './mya-all-account-evolution.html',
})
export class MyaAllAccountEvolutionComponent implements OnChanges, OnDestroy {
  @Input() data: string | null = null;
  @Input() type: string | null = null;
  chart: Chart | null = null;
  //data: ChartConfiguration<'line'>['data'] | null = null;
  //options: ChartOptions<'line'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}
  ngOnDestroy(): void {
    this.chart?.destroy();
    //throw new Error('Method not implemented.');
  }

  changeRange(range: string): void {
    switch (range) {
      case '5y':
        this.chart!.options.scales!.x!.min = dayjs().add(-5, 'year').format(DATE_FORMAT);
        break;
      case '2y':
        this.chart!.options.scales!.x!.min = dayjs().add(-2, 'year').format(DATE_FORMAT);
        break;
      case '1y':
        this.chart!.options.scales!.x!.min = dayjs().add(-1, 'year').format(DATE_FORMAT);
        break;
      case 'all':
        this.chart!.options.scales!.x!.min = typeof this.chart!.data.labels![0] === 'string' ? this.chart!.data.labels![0] : 0;
        break;
      case '6m':
        this.chart!.options.scales!.x!.min = dayjs().add(-6, 'month').format(DATE_FORMAT);
        break;
      case '1m':
        this.chart!.options.scales!.x!.min = dayjs().add(-1, 'month').format(DATE_FORMAT);
        break;
    }
    //this.chart!.options.scales!.x!.min = '2020-01-01';
    this.chart?.update();
  }

  loadAll(): void {
    if (this.type && this.type === 'all') {
      this.dashboardService.getAllAccountEvolutionByType().subscribe((res: HttpResponse<any>) => {
        this.buildSingleChart(res.body);
      });
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }

  getPoints(data: any): number[] {
    const values: number[] = new Array<number>();
    data.points.forEach((point: any) => {
      values.push(point.data ? point.data.value : null);
    });
    return values;
  }

  buildSingleChart(points: any): void {
    if (this.chart) {
      this.chart.destroy();
    }
    if (this.type) {
      this.chart = new Chart('myaAmountEvolution', {
        type: 'line',

        data: {
          labels: points.dates,
          datasets: [
            {
              label: 'Immobilier',
              data: this.getPoints(points.realEstatePoints),
              borderColor: '#233D4D',
              backgroundColor: '#233D4D80',
              pointBorderColor: '#233D4D',
              pointBackgroundColor: '#233D4D',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Epargne',
              data: this.getPoints(points.savingsPoints),
              borderColor: '#FE7F2D',
              backgroundColor: '#FE7F2D80',
              pointBorderColor: '#FE7F2D',
              pointBackgroundColor: '#FE7F2D',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Courant',
              data: this.getPoints(points.currentPoints),
              borderColor: '#FCCA46',
              backgroundColor: '#FCCA4680',
              pointBorderColor: '#FCCA46',
              pointBackgroundColor: '#FCCA46',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Actions',
              data: this.getPoints(points.stockData),
              borderColor: '#619B8A',
              backgroundColor: '#619B8A80',
              pointBorderColor: '#619B8A',
              pointBackgroundColor: '#619B8A',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          interaction: {
            intersect: false,
          },
          plugins: {
            legend: {
              display: true,
              position: 'top',
            },
            tooltip: {
              position: 'average',
              mode: 'index',
              intersect: false,
              callbacks: {
                title(context: any): string[] {
                  return [dayjs(context[0].label).format('D-MMM-YY')];
                },
              },
            },
          },
          scales: {
            y: {
              stacked: true,
              beginAtZero: true,
              title: {
                display: false,
              },
              ticks: {
                callback(value: string | number, index: number, ticks: Tick[]): string {
                  return String(value);
                },
              },
            },
            x: {
              display: true,
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
        },
      });
    }
  }

  buildStockChart(points: any[]): void {
    if (this.chart) {
      this.chart.destroy();
    }
    if (this.type) {
      const dates: string[] = new Array<string>();
      const valuesInCurrency: number[] = new Array<number>();
      const valuesInEuro: number[] = new Array<number>();
      points.forEach(point => {
        dates.push(point.date);
      });
      points.forEach(point => {
        valuesInCurrency.push(point.data.valueInCurrency);
      });
      points.forEach(point => {
        valuesInEuro.push(point.data.valueInEuro);
      });
      this.chart = new Chart('myaAmountEvolution', {
        type: 'line',
        //points.forEach(point => { valueInEuro.push(point.data.valueInEuro) });
        data: {
          labels: dates,
          datasets: [
            {
              label: 'Value in Currency',
              data: valuesInCurrency,
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
              label: 'Value in Euro',
              data: valuesInEuro,
              borderColor: '#3b5998',
              backgroundColor: '#3b5998',
              pointBorderColor: '#3b5998',
              pointBackgroundColor: '#3b5998',
              fill: false,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          interaction: {
            intersect: false,
          },
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
                  return [dayjs(context[0].label).format('D-MMM-YY')];
                },
              },
            },
          },
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: false,
              },
              ticks: {
                callback(value: string | number, index: number, ticks: Tick[]): string {
                  return String(value);
                },
              },
            },
            x: {
              display: true,
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
        },
      });
    }
  }
}
