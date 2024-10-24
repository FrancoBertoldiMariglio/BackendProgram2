import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '00d184f7-c4cf-447d-bada-d6c94556f087',
};

export const sampleWithPartialData: IAuthority = {
  name: '1d34d903-8f8a-4e6d-a474-b4e823338999',
};

export const sampleWithFullData: IAuthority = {
  name: '355e0f90-d842-453f-8abc-0ebe1391b430',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
