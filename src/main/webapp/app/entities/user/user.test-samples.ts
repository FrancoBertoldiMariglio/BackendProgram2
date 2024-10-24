import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 19726,
  login: 'UHD',
};

export const sampleWithPartialData: IUser = {
  id: 9035,
  login: 'w!9}.R@W\\hc\\rq\\,pX\\Sbml',
};

export const sampleWithFullData: IUser = {
  id: 9085,
  login: 'Esy',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
