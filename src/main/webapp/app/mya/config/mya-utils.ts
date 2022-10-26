import dayjs from 'dayjs';

export function convertDateStringToMillisecondes(dateString: string): number {
  return dayjs(dateString).valueOf();
}

export function convertDateStringToMillisecondesArray(dateString: string[]): number[] {
  const result = new Array<number>();
  dateString.forEach(d => result.push(convertDateStringToMillisecondes(d)));
  return result;
}
