/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
import { Credential } from './credential';
import { ApiSettings } from '../utils/api-request';
/** Instantiates the getAccountInfo endpoint settings. */
export declare const FIREBASE_AUTH_GET_ACCOUNT_INFO: ApiSettings;
/** Instantiates the deleteAccount endpoint settings. */
export declare const FIREBASE_AUTH_DELETE_ACCOUNT: ApiSettings;
/** Instantiates the setAccountInfo endpoint settings for updating existing accounts. */
export declare const FIREBASE_AUTH_SET_ACCOUNT_INFO: ApiSettings;
/**
 * Instantiates the uploadAccount endpoint settings for creating a new user with uid
 * specified.
 */
export declare const FIREBASE_AUTH_UPLOAD_ACCOUNT: ApiSettings;
/**
 * Instantiates the signupNewUser endpoint settings for creating a new user without
 * uid being specified. The backend will create a new one and return it.
 */
export declare const FIREBASE_AUTH_SIGN_UP_NEW_USER: ApiSettings;
/**
 * Class that provides mechanism to send requests to the Firebase Auth backend endpoints.
 *
 * @param {Credential} credential The service account credential used to sign HTTP requests.
 * @constructor
 */
export declare class FirebaseAuthRequestHandler {
    private host;
    private port;
    private path;
    private headers;
    private timeout;
    private signedApiRequestHandler;
    /**
     * @param {Object} response The response to check for errors.
     * @return {string} The error code if present, an empty string otherwise.
     */
    private static getErrorCode(response);
    constructor(credential: Credential);
    /**
     * Looks a user by uid.
     *
     * @param {string} uid The uid of the user to lookup.
     * @return {Promise<Object>} A promise that resolves with the user information.
     */
    getAccountInfoByUid(uid: string): Promise<Object>;
    /**
     * Looks a user by email.
     *
     * @param {string} email The email of the user to lookup.
     * @return {Promise<Object>} A promise that resolves with the user information.
     */
    getAccountInfoByEmail(email: string): Promise<Object>;
    /**
     * Deletes an account identified by a uid.
     *
     * @param {string} uid The uid of the user to delete.
     * @return {Promise<Object>} A promise that resolves when the user is deleted.
     */
    deleteAccount(uid: string): Promise<Object>;
    /**
     * Edits an existing user.
     *
     * @param {string} uid The user to edit.
     * @param {Object} properties The properties to set on the user.
     * @return {Promise<string>} A promise that resolves when the operation completes
     *     with the user id that was edited.
     */
    updateExistingAccount(uid: string, properties: Object): Promise<string>;
    /**
     * Create a new user with the properties supplied.
     *
     * @param {Object} properties The properties to set on the user.
     * @return {Promise<string>} A promise that resolves when the operation completes
     *     with the user id that was created.
     */
    createNewAccount(properties: Object): Promise<string>;
    /**
     * Invokes the request handler based on the API settings object passed.
     *
     * @param {ApiSettings} apiSettings The API endpoint settings to apply to request and response.
     * @param {Object} requestData The request data.
     * @return {Promise<Object>} A promise that resolves with the response.
     */
    private invokeRequestHandler(apiSettings, requestData);
}
