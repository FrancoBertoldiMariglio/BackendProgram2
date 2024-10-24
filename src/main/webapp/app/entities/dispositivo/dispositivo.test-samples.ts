import { IDispositivo, NewDispositivo } from './dispositivo.model';

export const sampleWithRequiredData: IDispositivo = {
  id: 32246,
  codigo: 'rapid seeker quietly',
  nombre: 'per',
  descripcion: '../fake-data/blob/hipster.txt',
  precioBase: 2694.66,
  moneda: 'winding selfish',
};

export const sampleWithPartialData: IDispositivo = {
  id: 31746,
  codigo: 'considering',
  nombre: 'value via',
  descripcion: '../fake-data/blob/hipster.txt',
  precioBase: 6686.92,
  moneda: 'however circa',
};

export const sampleWithFullData: IDispositivo = {
  id: 3136,
  codigo: 'why ew',
  nombre: 'zowie',
  descripcion: '../fake-data/blob/hipster.txt',
  precioBase: 4359.48,
  moneda: 'er brilliant',
};

export const sampleWithNewData: NewDispositivo = {
  codigo: 'monumental lest whose',
  nombre: 'acceptable annually into',
  descripcion: '../fake-data/blob/hipster.txt',
  precioBase: 25602.37,
  moneda: 'likewise ha notable',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
