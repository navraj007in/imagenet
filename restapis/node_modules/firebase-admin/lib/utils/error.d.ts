/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="node" />
/// <reference types="chai" />
/**
 * Defines error info type. This includes a code and message string.
 */
declare type ErrorInfo = {
    code: string;
    message: string;
};
/**
 * Firebase error code structure. This extends Error.
 *
 * @param {ErrorInfo} errorInfo The error information (code and message).
 * @constructor
 */
declare class FirebaseError extends Error {
    private errorInfo;
    constructor(errorInfo: ErrorInfo);
    /** @return {string} The error code. */
    readonly code: string;
    /** @return {string} The error message. */
    readonly message: string;
    /** @return {Object} The object representation of the error. */
    toJSON(): Object;
}
/**
 * Firebase Auth error code structure. This extends FirebaseError.
 *
 * @param {ErrorInfo} info The error code info.
 * @param {string} [message] The error message. This will override the default
 *     message if provided.
 * @constructor
 */
declare class FirebaseAuthError extends FirebaseError {
    /**
     * Creates the developer facing error corresponding to the backend error code.
     *
     * @param {string} serverErrorCode The server error code.
     * @param {string} [message] The error message. The default message is used
     *     if not provided.
     * @return {FirebaseAuthError} The corresponding developer facing error.
     */
    static fromServerError(serverErrorCode: string, message?: string): FirebaseAuthError;
    constructor(info: ErrorInfo, message?: string);
}
/** @const {TODO} Auth client error codes and their default messages. */
declare class AuthClientErrorCode {
    static INVALID_ARGUMENT: {
        code: string;
        message: string;
    };
    static EMAIL_ALREADY_EXISTS: {
        code: string;
        message: string;
    };
    static INTERNAL_ERROR: {
        code: string;
        message: string;
    };
    static INVALID_CREDENTIAL: {
        code: string;
        message: string;
    };
    static INVALID_DISABLED_FIELD: {
        code: string;
        message: string;
    };
    static INVALID_DISPLAY_NAME: {
        code: string;
        message: string;
    };
    static INVALID_EMAIL_VERIFIED: {
        code: string;
        message: string;
    };
    static INVALID_EMAIL: {
        code: string;
        message: string;
    };
    static INVALID_PASSWORD: {
        code: string;
        message: string;
    };
    static INVALID_PHOTO_URL: {
        code: string;
        message: string;
    };
    static INVALID_SERVICE_ACCOUNT: {
        code: string;
        message: string;
    };
    static INVALID_UID: {
        code: string;
        message: string;
    };
    static MISSING_UID: {
        code: string;
        message: string;
    };
    static OPERATION_NOT_ALLOWED: {
        code: string;
        message: string;
    };
    static PROJECT_NOT_FOUND: {
        code: string;
        message: string;
    };
    static UID_ALREADY_EXISTS: {
        code: string;
        message: string;
    };
    static USER_NOT_FOUND: {
        code: string;
        message: string;
    };
}
export { ErrorInfo, FirebaseError, FirebaseAuthError, AuthClientErrorCode };
