/** Shared DTOs aligned with backend (entity-service / timesheet-service). */

export interface AddressDto {
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
}

export type Status = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
export type EmployeeType = 'HOURLY' | 'SALARIED';
export type EmployeeRole = 'WORKER' | 'COMPANY_ADMIN' | 'CUSTOMER_ADMIN';

export interface CustomerDto {
  id?: number;
  name: string;
  code: string;
  companyIds?: number[];
  createdOn?: string;
  modifiedOn?: string;
}

export interface CompanyDto {
  id?: number;
  name: string;
  code: string;
  address?: AddressDto;
  customerId?: number;
  createdOn?: string;
  modifiedOn?: string;
}

export interface ProjectDto {
  id?: number;
  name: string;
  code: string;
  projectStatus?: Status;
  customerId?: number;
  address?: AddressDto;
  taskIds?: number[];
  createdOn?: string;
  modifiedOn?: string;
}

export interface EmployeeDto {
  id?: number;
  firstName: string;
  lastName: string;
  payRate?: number;
  employeeType?: EmployeeType;
  employeeRole?: EmployeeRole;
  companyId?: number;
  address?: AddressDto;
  userId?: number;
  username: string;
  email: string;
  password?: string;
  active?: boolean;
  createdOn?: string;
  modifiedOn?: string;
}

export interface TaskDto {
  id?: number;
  name: string;
  code: string;
  taskStatus?: Status;
  projectId?: number;
  createdOn?: string;
  modifiedOn?: string;
}

export interface CostCodeDto {
  id?: number;
  name: string;
  code: string;
  costCodeStatus?: Status;
  parentId?: number;
  customerId?: number;
  createdOn?: string;
  modifiedOn?: string;
}

export interface ProjectBudgetDto {
  id?: number;
  taskId?: number;
  taskCode?: string;
  costCodeId?: number;
  costCode?: string;
  active?: boolean;
  createdOn?: string;
  modifiedOn?: string;
}

/** Backend: dateWorked (YYYY-MM-DD), startTime (HH:mm:ss), seconds (ISO-8601 duration e.g. PT8H30M). */
export interface TimesheetDto {
  id?: number;
  customerId: number;
  employeeId: number;
  projectId: number;
  taskId?: number;
  costCodeId?: number;
  /** ISO-8601 duration e.g. "PT8H30M". */
  seconds: string;
  /** Date as YYYY-MM-DD. */
  dateWorked: string;
  /** Time as HH:mm:ss. */
  startTime: string;
  createdOn?: string;
  modifiedOn?: string;
}

/** Convert seconds (number) to ISO-8601 duration string (e.g. PT8H30M). */
export function secondsToIsoDuration(totalSeconds: number): string {
  const h = Math.floor(totalSeconds / 3600);
  const m = Math.floor((totalSeconds % 3600) / 60);
  const s = totalSeconds % 60;
  const parts: string[] = [];
  if (h) parts.push(`${h}H`);
  if (m) parts.push(`${m}M`);
  if (s) parts.push(`${s}S`);
  return 'PT' + (parts.length ? parts.join('') : '0S');
}

/** Parse ISO-8601 duration (e.g. PT8H30M) or number to total seconds. */
export function parseDurationToSeconds(value: string | number | undefined): number {
  if (value == null) return 0;
  if (typeof value === 'number') return value;
  const s = value.trim().toUpperCase();
  if (!s.startsWith('PT')) return 0;
  let sec = 0;
  const h = s.match(/(\d+)H/);
  const m = s.match(/(\d+)M/);
  const secMatch = s.match(/(\d+)S/);
  if (h) sec += parseInt(h[1], 10) * 3600;
  if (m) sec += parseInt(m[1], 10) * 60;
  if (secMatch) sec += parseInt(secMatch[1], 10);
  return sec;
}
