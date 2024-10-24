import { ICaracteristica, NewCaracteristica } from './caracteristica.model';

export const sampleWithRequiredData: ICaracteristica = {
  id: 24237,
  nombre: 'solidly bold tensely',
  descripcion: 'cheap',
};

export const sampleWithPartialData: ICaracteristica = {
  id: 31100,
  nombre: 'ugh mid sunbonnet',
  descripcion: 'stare',
};

export const sampleWithFullData: ICaracteristica = {
  id: 19179,
  nombre: 'regarding',
  descripcion: 'along',
};

export const sampleWithNewData: NewCaracteristica = {
  nombre: 'shabby',
  descripcion: 'deadly republic',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
