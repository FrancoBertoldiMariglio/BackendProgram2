import { IOpcion, NewOpcion } from './opcion.model';

export const sampleWithRequiredData: IOpcion = {
  id: 31053,
  codigo: 'voluntarily whoa millennium',
  nombre: 'defense now',
  descripcion: 'lazily',
  precioAdicional: 27644.87,
};

export const sampleWithPartialData: IOpcion = {
  id: 14638,
  codigo: 'regarding flintlock whereas',
  nombre: 'lest',
  descripcion: 'miaow humongous poorly',
  precioAdicional: 25474.45,
};

export const sampleWithFullData: IOpcion = {
  id: 19109,
  codigo: 'meh crick station',
  nombre: 'aha',
  descripcion: 'anti yuck sonnet',
  precioAdicional: 24225.89,
};

export const sampleWithNewData: NewOpcion = {
  codigo: 'beyond psychologist',
  nombre: 'dollop',
  descripcion: 'equally incidentally now',
  precioAdicional: 4698.81,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
