/**
 * A Utility Class for testing.
 */
export default class TestUtils {
  /** Gets an array of object own property names (including functions but excluding constructors) */
  static getOwnPropertyNames(obj: any) {
    const props = [];
    const objProperties = Object.getOwnPropertyNames(obj);
    for (const property of objProperties) {
      if (property !== 'constructor') {
        props.push(property);
      }
    }
    return props;
  }

  /** Gets an array of object own method names (excluding constructors) */
  static getOwnMethodNames(obj: any) {
    const meths = [];
    const objProperties = Object.getOwnPropertyNames(obj);
    for (const property of objProperties) {
      if (property !== 'constructor' && typeof obj[property] === 'function') {
        meths.push(property);
      }
    }
    return meths;
  }

  /** Gets an array of object hierarchy property names (including functions but excluding constructors) */
  static getPropertyNames(obj: any) {
    const props = [];
    for (; obj != null; obj = Object.getPrototypeOf(obj)) {
      const objProperties = Object.getOwnPropertyNames(obj);
      for (const property of objProperties) {
        if (property !== 'constructor' && props.indexOf(property) === -1) {
          props.push(property);
        }
      }
    }
    return props;
  }

  /** Gets an array of object hierarchy method names (excluding constructors) */
  static getMethodNames(obj: any) {
    const meths = [];
    for (; obj != null; obj = Object.getPrototypeOf(obj)) {
      const objProperties = Object.getOwnPropertyNames(obj);
      for (const property of objProperties) {
        if (property !== 'constructor' && meths.indexOf(property) === -1 && typeof obj[property] === 'function') {
          meths.push(property);
        }
      }
    }
    return meths;
  }

}