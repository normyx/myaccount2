import { IApplicationUser, NewApplicationUser } from './application-user.model';

export const sampleWithRequiredData: IApplicationUser = {
  id: 60827,
  nickName: 'c',
};

export const sampleWithPartialData: IApplicationUser = {
  id: 55912,
  nickName: 'withdrawal',
};

export const sampleWithFullData: IApplicationUser = {
  id: 77799,
  nickName: 'enable optical Electronics',
};

export const sampleWithNewData: NewApplicationUser = {
  nickName: 'multi-byte next-generation eyeballs',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
