/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/**
 * Returns a deep copy of an object or array.
 *
 * @param {Object|array} value The object or array to deep copy.
 * @return {Object|array} A deep copy of the provided object or array.
 */
export declare function deepCopy<T>(value: T): T;
/**
 * Copies properties from source to target (recursively allows extension of objects and arrays).
 * Scalar values in the target are over-written. If target is undefined, an object of the
 * appropriate type will be created (and returned).
 *
 * We recursively copy all child properties of plain objects in the source - so that namespace-like
 * objects are merged.
 *
 * Note that the target can be a function, in which case the properties in the source object are
 * copied onto it as static properties of the function.
 *
 * @param {any} target The value which is being extended.
 * @param {any} source The value whose properties are extending the target.
 * @return {any} The target value.
 */
export declare function deepExtend(target: any, source: any): any;
