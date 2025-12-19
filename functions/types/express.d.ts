declare module '@google-cloud/functions-framework' {
  import type { Express } from 'express';
  export function http(name: string, app: Express): void;
}