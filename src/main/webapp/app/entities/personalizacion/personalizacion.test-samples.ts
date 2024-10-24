import { IPersonalizacion, NewPersonalizacion } from './personalizacion.model';

export const sampleWithRequiredData: IPersonalizacion = {
  id: 27569,
  nombre: 'ack',
  descripcion: 'onto loyally circumnavigate',
};

export const sampleWithPartialData: IPersonalizacion = {
  id: 18321,
  nombre: 'longingly obnoxiously meanwhile',
  descripcion: 'deluge extremely manager',
};

export const sampleWithFullData: IPersonalizacion = {
  id: 25778,
  nombre: 'deduce force organ',
  descripcion: 'once',
};

export const sampleWithNewData: NewPersonalizacion = {
  nombre: 'quizzically gen',
  descripcion: 'subcomponent but',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
