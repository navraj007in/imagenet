/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
import { Credential } from './auth/credential';
import { FirebaseAccessToken } from './auth/auth';
import { FirebaseServiceInterface } from './firebase-service';
import { FirebaseNamespaceInternals } from './firebase-namespace';
/**
 * Type representing a callback which is called every time an app lifecycle event occurs.
 */
export declare type AppHook = (event: string, app: FirebaseApp) => void;
/**
 * Type representing the options object passed into initializeApp().
 */
export declare type FirebaseAppOptions = {
    databaseURL?: string;
    credential?: Credential;
    serviceAccount?: string | Object;
    databaseAuthVariableOverride?: Object;
};
/**
 * Interface representing the internals of a FirebaseApp instance.
 */
export interface FirebaseAppInternalsInterface {
    getToken?(): Promise<FirebaseAccessToken>;
    addAuthTokenListener?(fn: (token?: string) => void): void;
    removeAuthTokenListener?(fn: (token?: string) => void): void;
}
/**
 * Global context object for a collection of services using a shared authentication state.
 */
export declare class FirebaseApp {
    private firebaseInternals_;
    INTERNAL: FirebaseAppInternalsInterface;
    private name_;
    private options_;
    private services_;
    private isDeleted_;
    constructor(options: FirebaseAppOptions, name: string, firebaseInternals_: FirebaseNamespaceInternals);
    /**
     * Firebase services available off of a FirebaseApp instance. These are monkey-patched via
     * registerService(), but we need to include a dummy implementation to get TypeScript to
     * compile it without errors.
     */
    auth(): FirebaseServiceInterface;
    database(): FirebaseServiceInterface;
    /**
     * Returns the name of the FirebaseApp instance.
     *
     * @returns {string} The name of the FirebaseApp instance.
     */
    readonly name: string;
    /**
     * Returns the options for the FirebaseApp instance.
     *
     * @returns {FirebaseAppOptions} The options for the FirebaseApp instance.
     */
    readonly options: FirebaseAppOptions;
    /**
     * Deletes the FirebaseApp instance.
     *
     * @returns {Promise<void>} An empty Promise fulfilled once the FirebaseApp instance is deleted.
     */
    delete(): Promise<void>;
    /**
     * Returns the service instance associated with this FirebaseApp instance (creating it on demand
     * if needed).
     *
     * @param {string} serviceName The name of the service instance to return.
     * @return {FirebaseServiceInterface} The service instance with the provided name.
     */
    private getService_(serviceName);
    /**
     * Callback function used to extend an App instance at the time of service instance creation.
     */
    private extendApp_(props);
    /**
     * Throws an Error if the FirebaseApp instance has already been deleted.
     */
    private checkDestroyed_();
}
