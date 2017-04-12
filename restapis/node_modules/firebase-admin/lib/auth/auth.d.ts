/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
import { Credential } from './credential';
import { FirebaseApp } from '../firebase-app';
import { FirebaseServiceInterface, FirebaseServiceInternalsInterface } from '../firebase-service';
import { UserRecord } from './user-record';
/**
 * Auth service bound to the provided app.
 *
 * @param {Object} app The app for this auth service
 * @constructor
 */
declare class Auth implements FirebaseServiceInterface {
    private app_;
    private tokenGenerator_;
    private authTokenManager_;
    private authRequestHandler;
    private userWriteMap;
    constructor(app: FirebaseApp);
    readonly app: FirebaseApp;
    readonly INTERNAL: AuthTokenManager;
    /**
     * Creates a new custom token that can be sent back to a client to use with
     * signInWithCustomToken().
     *
     * @param {string} uid The uid to use as the JWT subject.
     * @param {Object=} developerClaims Optional additional claims to include in the JWT payload.
     *
     * @return {Promise<string>} A JWT for the provided payload.
     */
    createCustomToken(uid: string, developerClaims?: Object): Promise<string>;
    /**
     * Verifies a JWT auth token. Returns a Promise with the tokens claims. Rejects
     * the promise if the token could not be verified.
     *
     * @param {string} idToken The JWT to verify.
     * @return {Object} A Promise that will be fulfilled after a successful verification.
     */
    verifyIdToken(idToken: string): Promise<Object>;
    /**
     * Looks up the user identified by the provided user id and returns a promise that is
     * fulfilled with a user record for the given user if that user is found.
     *
     * @param {string} uid The uid of the user to look up.
     * @return {Promise<UserRecord>} A promise that resolves with the corresponding user record.
     */
    getUser(uid: string): Promise<UserRecord>;
    /**
     * Looks up the user identified by the provided email and returns a promise that is
     * fulfilled with a user record for the given user if that user is found.
     *
     * @param {string} email The email of the user to look up.
     * @return {Promise<UserRecord>} A promise that resolves with the corresponding user record.
     */
    getUserByEmail(email: string): Promise<UserRecord>;
    /**
     * Creates a new user with the properties provided.
     *
     * @param {Object} properties The properties to set on the new user record to be created.
     * @return {Promise<UserRecord>} A promise that resolves with the newly created user record.
     */
    createUser(properties: Object): Promise<UserRecord>;
    /**
     * Deletes the user identified by the provided user id and returns a promise that is
     * fulfilled when the user is found and successfully deleted.
     *
     * @param {string} uid The uid of the user to delete.
     * @return {Promise<void>} A promise that resolves when the user is successfully deleted.
     */
    deleteUser(uid: string): Promise<void>;
    /**
     * Updates an existing user with the properties provided.
     *
     * @param {string} uid The uid identifier of the user to update.
     * @param {Object} properties The properties to update on the existing user.
     * @return {Promise<UserRecord>} A promise that resolves with the modified user record.
     */
    updateUser(uid: string, properties: Object): Promise<UserRecord>;
    /**
     * Deletes the user identified by the provided user id and returns a promise that is
     * fulfilled when the user is found and successfully deleted.
     * This will run without being serialized in the user write queue.
     *
     * @param {string} uid The uid of the user to delete.
     * @return {Promise<void>} A promise that resolves when the user is successfully deleted.
     */
    private deleteUserUnserialized(uid);
    /**
     * Updates an existing user with the properties provided.
     * This will run without being serialized in the user write queue.
     *
     * @param {string} uid The uid identifier of the user to update.
     * @param {Object} properties The properties to update on the existing user.
     * @return {Promise<UserRecord>} A promise that resolves with the modified user record.
     */
    private updateUserUnserialized(uid, properties);
    /**
     * @param {string} uid The uid identifier of the request.
     * @param {() => Promise<any>} boundFn Promise returning function to queue with this
     *     context and arguments already bound.
     * @return {Promise<any>} The resulting promise which resolves when all pending previous
     *     promises on the same user are resolved.
     */
    private serializeApiRequest(uid, boundFn);
}
export declare class FirebaseAccessToken {
    accessToken: string;
    expirationTime: number;
}
export declare class AuthTokenManager implements FirebaseServiceInternalsInterface {
    private credential;
    private cachedToken;
    private tokenListeners;
    constructor(credential: Credential);
    /**
     * Deletes the service and its associated resources.
     *
     * @return {Promise<()>} An empty Promise that will be fulfilled when the service is deleted.
     */
    delete(): Promise<void>;
    /**
     * Gets an auth token for the associated app.
     *
     * @param {boolean} forceRefresh Whether or not to force a token refresh.
     * @return {Promise<Object>} A Promise that will be fulfilled with the current or new token.
     */
    getToken(forceRefresh?: boolean): Promise<FirebaseAccessToken>;
    /**
     * Adds a listener that is called each time a token changes.
     *
     * @param {function(string)} listener The listener that will be called with each new token.
     */
    addAuthTokenListener(listener: (token: string) => void): void;
    /**
     * Removes a token listener.
     *
     * @param {function(string)} listener The listener to remove.
     */
    removeAuthTokenListener(listener: (token: string) => void): void;
}
export { Auth };
