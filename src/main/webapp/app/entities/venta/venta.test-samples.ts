import dayjs from 'dayjs/esm';

import { IVenta, NewVenta } from './venta.model';

export const sampleWithRequiredData: IVenta = {
  id: 26086,
  fechaVenta: dayjs('2024-10-24T00:49'),
};

export const sampleWithPartialData: IVenta = {
  id: 842,
  fechaVenta: dayjs('2024-10-23T22:38'),
};

export const sampleWithFullData: IVenta = {
  id: 11144,
  fechaVenta: dayjs('2024-10-23T21:27'),
  ganancia: 26093.25,
};

export const sampleWithNewData: NewVenta = {
  fechaVenta: dayjs('2024-10-23T21:54'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
