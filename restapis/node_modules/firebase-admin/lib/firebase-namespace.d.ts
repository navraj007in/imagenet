/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
import { AppHook, FirebaseApp, FirebaseAppOptions } from './firebase-app';
import { FirebaseServiceFactory, FirebaseServiceInterface } from './firebase-service';
import { Credential } from './auth/credential';
export interface FirebaseServiceNamespace<T extends FirebaseServiceInterface> {
    (app?: FirebaseApp): T;
}
export declare class FirebaseNamespaceInternals {
    firebase_: any;
    serviceFactories: {
        [serviceName: string]: FirebaseServiceFactory;
    };
    private apps_;
    private appHooks_;
    constructor(firebase_: any);
    /**
     * Initializes the FirebaseApp instance.
     *
     * @param {FirebaseAppOptions} options Options for the FirebaseApp instance.
     * @param {string} [appName] Optional name of the FirebaseApp instance.
     *
     * @return {FirebaseApp} A new FirebaseApp instance.
     */
    initializeApp(options: FirebaseAppOptions, appName?: string): FirebaseApp;
    /**
     * Returns the FirebaseApp instance with the provided name (or the default FirebaseApp instance
     * if no name is provided).
     *
     * @param {string} [appName=DEFAULT_APP_NAME] Optional name of the FirebaseApp instance to return.
     * @return {FirebaseApp} The FirebaseApp instance which has the provided name.
     */
    app(appName?: string): FirebaseApp;
    readonly apps: FirebaseApp[];
    removeApp(appName: string): void;
    registerService(serviceName: string, createService: FirebaseServiceFactory, serviceProperties?: Object, appHook?: AppHook): FirebaseServiceNamespace<FirebaseServiceInterface>;
    /**
     * Calls the app hooks corresponding to the provided event name for each service within the
     * provided FirebaseApp instance.
     *
     * @param {FirebaseApp} app The FirebaseApp instance whose app hooks to call.
     * @param {string} eventName The event name representing which app hooks to call.
     */
    private callAppHooks_(app, eventName);
}
/**
 * Global Firebase context object.
 */
export declare class FirebaseNamespace {
    credential: {
        cert: (serviceAccountPathOrObject: string | Object) => Credential;
        refreshToken: (refreshTokenPathOrObject: string | Object) => Credential;
        applicationDefault: () => Credential;
    };
    SDK_VERSION: string;
    INTERNAL: FirebaseNamespaceInternals;
    Promise: any;
    constructor();
    /**
     * Firebase services available off of a FirebaseNamespace instance. These are monkey-patched via
     * registerService(), but we need to include a dummy implementation to get TypeScript to
     * compile it without errors.
     */
    auth(): FirebaseServiceInterface;
    database(): FirebaseServiceInterface;
    /**
     * Initializes the FirebaseApp instance.
     *
     * @param {FirebaseAppOptions} options Options for the FirebaseApp instance.
     * @param {string} [appName] Optional name of the FirebaseApp instance.
     *
     * @return {FirebaseApp} A new FirebaseApp instance.
     */
    initializeApp(options: FirebaseAppOptions, appName?: string): FirebaseApp;
    /**
     * Returns the FirebaseApp instance with the provided name (or the default FirebaseApp instance
     * if no name is provided).
     *
     * @param {string} [appName] Optional name of the FirebaseApp instance to return.
     * @return {FirebaseApp} The FirebaseApp instance which has the provided name.
     */
    app(appName?: string): FirebaseApp;
    readonly apps: FirebaseApp[];
}
