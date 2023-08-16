import { IValidatorCompareToOptions } from "./validator-compare-to-options";

export interface IValidatorOptions {
  compareTo: keyof IValidatorCompareToOptions,
  message: string
}
