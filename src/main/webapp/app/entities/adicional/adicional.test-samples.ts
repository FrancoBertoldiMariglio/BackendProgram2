import { IAdicional, NewAdicional } from './adicional.model';

export const sampleWithRequiredData: IAdicional = {
  id: 8509,
  nombre: 'in hurtle ring',
  descripcion: 'joyfully eventuate far',
  precio: 7982.32,
};

export const sampleWithPartialData: IAdicional = {
  id: 22984,
  nombre: 'as',
  descripcion: 'meanwhile guard er',
  precio: 24456.07,
  precioGratis: 25761.22,
};

export const sampleWithFullData: IAdicional = {
  id: 28171,
  nombre: 'fen',
  descripcion: 'yet via',
  precio: 27492.68,
  precioGratis: 6965.48,
};

export const sampleWithNewData: NewAdicional = {
  nombre: 'medium',
  descripcion: 'frankly',
  precio: 31924.58,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
